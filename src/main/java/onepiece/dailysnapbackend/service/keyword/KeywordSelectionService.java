package onepiece.dailysnapbackend.service.keyword;

import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import onepiece.dailysnapbackend.object.constants.KeywordCategory;
import onepiece.dailysnapbackend.object.dto.KeywordRequest;
import onepiece.dailysnapbackend.object.postgres.Keyword;
import onepiece.dailysnapbackend.repository.postgres.KeywordRepository;
import onepiece.dailysnapbackend.util.exception.CustomException;
import onepiece.dailysnapbackend.util.exception.ErrorCode;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeywordSelectionService {

  private final KeywordRepository keywordRepository;
  private final OpenAIKeywordService openAIKeywordService;

  private static final int KEYWORD_THRESHOLD = 10; // 키워드 부족 기준 개수

  /**
   * 🔹 오늘 제공할 키워드를 결정하고 반환
   * 1. '관리자 지정' 키워드가 있으면 해당 키워드를 반환
   * 2. 카테고리를 순환하면서 키워드를 선택
   * 3. 해당 카테고리의 키워드가 부족하면 OpenAI API 호출하여 새로운 키워드 생성
   */
  @Transactional
  public KeywordRequest getTodayKeyword() {
    // 1. '관리자 지정' 키워드가 오늘 날짜로 등록되어 있는지 확인
    Optional<Keyword> adminKeyword = keywordRepository.findByCategoryAndSpecifiedDate(
        KeywordCategory.ADMIN_SET, LocalDate.now());

    if (adminKeyword.isPresent()) {
      log.info("관리자 키워드가 존재합니다. Keyword: {}", adminKeyword.get());
      return toKeywordRequest(adminKeyword.get());
    }

    // 2. 카테고리 순환 방식으로 키워드 선택
    KeywordCategory selectedCategory = getNextCategory();
    Optional<Keyword> keyword = keywordRepository.findTopByCategoryAndIsUsedFalse(selectedCategory);

    // 3. 선택된 카테고리에서 키워드 제공 가능하면 제공
    if (keyword.isPresent()) {
      markKeywordAsUsed(keyword.get());
      return toKeywordRequest(keyword.get());
    }

    // 4. 선택된 카테고리의 키워드가 부족하면 OpenAI API를 통해 자동 생성
    long remainingCount = keywordRepository.countByCategory(selectedCategory);
    if (remainingCount <= KEYWORD_THRESHOLD) {
      log.info("카테고리 '{}' 키워드 부족 → OpenAI API 호출", selectedCategory);
      openAIKeywordService.generateKeywords(selectedCategory);

      keyword = keywordRepository.findTopByCategoryAndIsUsedFalse(selectedCategory);
      if (keyword.isPresent()) {
        markKeywordAsUsed(keyword.get());
        return toKeywordRequest(keyword.get());
      }
    }

    // 5. 키워드를 찾지 못하면 예외 발생
    throw new CustomException(ErrorCode.KEYWORD_NOT_FOUND);
  }

  /**
   * 🔹 다음 카테고리를 결정하는 메서드
   * - 카테고리 순서대로 순환하며, 계절 카테고리는 현재 계절과 일치하는 경우에만 선택
   */
  private KeywordCategory getNextCategory() {
    List<KeywordCategory> categories = List.of(
        KeywordCategory.TRAVEL,
        KeywordCategory.DAILY,
        KeywordCategory.ABSTRACT,
        KeywordCategory.RANDOM,
        KeywordCategory.SPRING,
        KeywordCategory.SUMMER,
        KeywordCategory.AUTUMN,
        KeywordCategory.WINTER
    );

    // 마지막으로 제공된 키워드 확인
    Optional<Keyword> lastKeyword = keywordRepository.findTopByOrderByProvidedDateDesc();
    if (lastKeyword.isPresent()) {
      int index = categories.indexOf(lastKeyword.get().getCategory());

      // 순환하며 다음 카테고리 찾기
      for (int i = 1; i <= categories.size(); i++) {
        KeywordCategory nextCategory = categories.get((index + i) % categories.size());

        // 계절 카테고리는 현재 계절과 일치할 때만 선택
        if (isSeasonCategory(nextCategory) && !isCurrentSeason(nextCategory)) {
          continue;
        }

        return nextCategory;
      }
    }

    // 기본적으로 첫 번째 카테고리 선택
    return categories.get(0);
  }

  /**
   * 🔹 주어진 카테고리가 계절 카테고리인지 확인
   */
  private boolean isSeasonCategory(KeywordCategory category) {
    return category == KeywordCategory.SPRING ||
           category == KeywordCategory.SUMMER ||
           category == KeywordCategory.AUTUMN ||
           category == KeywordCategory.WINTER;
  }

  /**
   * 🔹 현재 월을 기준으로 주어진 계절이 맞는지 확인
   */
  private boolean isCurrentSeason(KeywordCategory category) {
    int month = LocalDate.now().getMonthValue();
    return (category == KeywordCategory.SPRING && month >= 3 && month <= 5) ||
           (category == KeywordCategory.SUMMER && month >= 6 && month <= 8) ||
           (category == KeywordCategory.AUTUMN && month >= 9 && month <= 11) ||
           (category == KeywordCategory.WINTER && (month == 12 || month <= 2));
  }

  /**
   * 🔹 제공된 키워드를 사용된 상태로 업데이트
   */
  private void markKeywordAsUsed(Keyword keyword) {
    keyword.setUsed(true);
    keyword.setProvidedDate(LocalDate.now());
    keywordRepository.save(keyword);
  }

  /**
   * 🔹 Keyword 엔티티를 KeywordRequest DTO로 변환 ( ***리펙토링 할게요 mapstruct로 수정 예정*** )
   */
  private KeywordRequest toKeywordRequest(Keyword keyword) {
    return KeywordRequest.builder()
        .keyword(keyword.getCategory())
        .category(keyword.getCategory().name())
        .specifiedDate(keyword.getSpecifiedDate())
        .providedDate(keyword.getProvidedDate())
        .build();
  }
}
