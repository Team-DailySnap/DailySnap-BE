package onepiece.dailysnapbackend.service.keyword;

import static onepiece.dailysnapbackend.object.constants.KeywordCategory.ADMIN_SET;

import jakarta.transaction.Transactional;
import java.time.LocalDate;
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

  private static final KeywordCategory[] allCategories = {
      KeywordCategory.SPRING, KeywordCategory.SUMMER, KeywordCategory.AUTUMN, KeywordCategory.WINTER,
      KeywordCategory.TRAVEL, KeywordCategory.DAILY, KeywordCategory.ABSTRACT, KeywordCategory.RANDOM
  };

  @Transactional
  public KeywordRequest getTodayKeyword() {
    LocalDate today = LocalDate.now();
    LocalDate yesterday = today.minusDays(1);
    log.info("[KeywordSelectionService] 오늘의 키워드 조회 시작: date={}", today);

    // ADMIN_SET 키워드 확인 (특정 날짜 키워드 우선 제공)
    Keyword adminKeyword = keywordRepository.findFirstByCategoryAndSpecifiedDate(ADMIN_SET, today).orElse(null);
    if (adminKeyword != null && !adminKeyword.isUsed()) {
      log.info("[KeywordSelectionService] ADMIN_SET 키워드 발견: keyword={}", adminKeyword.getKeyword());
      markKeywordAsUsed(adminKeyword);
      return toKeywordRequest(adminKeyword);
    }

    // 어제 제공된 키워드 확인 → 그 다음 카테고리 선택
    KeywordCategory selectedCategory = getNextCategory(yesterday);
    log.info("[KeywordSelectionService] 선택된 카테고리: category={}", selectedCategory);

    // 선택된 카테고리에서 미사용 키워드 조회
    Keyword unusedKeyword = keywordRepository.findFirstByCategoryAndIsUsedFalse(selectedCategory).orElse(null);
    if (unusedKeyword == null) {
      log.warn("[KeywordSelectionService] 사용 가능한 키워드 없음, OpenAI로 새 키워드 생성: category={}", selectedCategory);
      openAIKeywordService.generateKeywords(selectedCategory);
      unusedKeyword = keywordRepository.findFirstByCategoryAndIsUsedFalse(selectedCategory).orElse(null);
    }

    if (unusedKeyword != null) {
      log.info("[KeywordSelectionService] 선택된 키워드: keyword={}", unusedKeyword.getKeyword());
      markKeywordAsUsed(unusedKeyword);
      return toKeywordRequest(unusedKeyword);
    }

    log.error("[KeywordSelectionService] 키워드 조회 실패: 사용 가능한 키워드 없음");
    throw new CustomException(ErrorCode.KEYWORD_NOT_FOUND);
  }

  @Transactional
  public void markKeywordAsUsed(Keyword keyword) {
    keyword.setUsed(true);
    keyword.setProvidedDate(LocalDate.now());
    keywordRepository.saveAndFlush(keyword);
    log.info("[KeywordSelectionService] 키워드 사용 처리 완료: keyword={}, isUsed={}, providedDate={}",
        keyword.getKeyword(), keyword.isUsed(), keyword.getProvidedDate());
  }

  /**
   *  어제 카테고리를 기반으로 순환 방식으로 다음 카테고리 선택
   *    - allCategories 배열 순서대로 순환
   *    - 계절 카테고리라면 현재 월과 일치하는지 확인 후 선택
   */
  private KeywordCategory getNextCategory(LocalDate yesterday) {
    // 어제 제공된 키워드 조회
    Keyword lastKeyword = keywordRepository.findFirstByProvidedDate(yesterday).orElse(null);

    // 어제 키워드가 없으면 기본적으로 현재 월의 계절 카테고리를 선택
    if (lastKeyword == null) {
      log.info("[KeywordSelectionService] 어제 키워드 없음 → 현재 시즌 카테고리 선택");
      return getSeasonCategory();
    }

    //  어제 사용된 카테고리 찾기
    int lastIndex = indexOfCategory(lastKeyword.getCategory());
    int nextIndex = (lastIndex + 1) % allCategories.length;

    //  다음 카테고리가 계절이면 현재 월과 비교
    while (isSeasonCategory(allCategories[nextIndex]) && allCategories[nextIndex] != getSeasonCategory()) {
      log.info("[KeywordSelectionService] 계절 불일치 → 다음 카테고리로 이동: {} → {}", allCategories[nextIndex], allCategories[(nextIndex + 1) % allCategories.length]);
      nextIndex = (nextIndex + 1) % allCategories.length;
    }

    log.info("[KeywordSelectionService] 최종 선택된 카테고리: {}", allCategories[nextIndex]);
    return allCategories[nextIndex];
  }

  /**
   *  현재 월에 맞는 계절 카테고리 반환
   */
  private KeywordCategory getSeasonCategory() {
    int month = LocalDate.now().getMonthValue();
    if (month >= 3 && month <= 5) return KeywordCategory.SPRING;
    if (month >= 6 && month <= 8) return KeywordCategory.SUMMER;
    if (month >= 9 && month <= 11) return KeywordCategory.AUTUMN;
    return KeywordCategory.WINTER;
  }

  /**
   *  계절 카테고리인지 여부 확인
   */
  private boolean isSeasonCategory(KeywordCategory category) {
    return category == KeywordCategory.SPRING ||
           category == KeywordCategory.SUMMER ||
           category == KeywordCategory.AUTUMN ||
           category == KeywordCategory.WINTER;
  }

  /**
   *  특정 카테고리의 인덱스를 반환
   */
  private int indexOfCategory(KeywordCategory category) {
    for (int i = 0; i < allCategories.length; i++) {
      if (allCategories[i] == category) return i;
    }
    return 0;
  }

  /**
   *  추후에 mapstruct로 변환 예정
   */
  private KeywordRequest toKeywordRequest(Keyword keyword) {
    return KeywordRequest.builder()
        .keyword(keyword.getKeyword())
        .category(KeywordCategory.valueOf(keyword.getCategory().name()))
        .specifiedDate(keyword.getSpecifiedDate())
        .build();
  }
}
