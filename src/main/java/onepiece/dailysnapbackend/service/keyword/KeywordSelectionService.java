package onepiece.dailysnapbackend.service.keyword;

import static onepiece.dailysnapbackend.object.constants.KeywordCategory.ADMIN_SET;

import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import onepiece.dailysnapbackend.mapper.EntityMapper;
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
  private final EntityMapper entityMapper;

  private static final List<KeywordCategory> allCategories = Arrays.stream(KeywordCategory.values())
      .filter(category -> category != KeywordCategory.ADMIN_SET)
      .toList();

  @Transactional
  public KeywordRequest getTodayKeyword() {
    LocalDate today = LocalDate.now();
    LocalDate yesterday = today.minusDays(1);
    log.info("오늘의 키워드 조회 시작: date={}", today);

    // ADMIN_SET 키워드 확인 (특정 날짜 키워드 우선 제공)
    Optional<Keyword> optionalAdminKeyword = keywordRepository.findByCategoryAndSpecifiedDate(ADMIN_SET, today);
    if (optionalAdminKeyword.isPresent()) {
      Keyword adminKeyword = optionalAdminKeyword.get();
      if (!adminKeyword.isUsed()) {
        log.info("ADMIN_SET 키워드 발견: keyword={}", adminKeyword.getKoreanKeyword());
        markKeywordAsUsed(adminKeyword);
        return toKeywordRequest(adminKeyword);
      }
    }

    // 어제 제공된 키워드 확인 → 그 다음 카테고리 선택
    KeywordCategory selectedCategory = getNextCategory(yesterday);
    log.info("선택된 카테고리: category={}", selectedCategory);

    // 선택된 카테고리에서 미사용 키워드 조회
    Keyword unusedKeyword = keywordRepository.findFirstByCategoryAndIsUsedFalse(selectedCategory)
        .orElseGet(() -> {
          log.warn("사용 가능한 키워드 없음, OpenAI로 새 키워드 생성: category={}", selectedCategory);
          openAIKeywordService.generateKeywords(selectedCategory);
          return keywordRepository.findFirstByCategoryAndIsUsedFalse(selectedCategory)
              .orElseThrow(() -> {
                log.error("키워드 조회 실패: 사용 가능한 키워드 없음");
                return new CustomException(ErrorCode.KEYWORD_NOT_FOUND);
              });
        });

    log.info("선택된 키워드: keyword={}", unusedKeyword.getKoreanKeyword());
    markKeywordAsUsed(unusedKeyword);
    return toKeywordRequest(unusedKeyword);
  }

  @Transactional
  public void markKeywordAsUsed(Keyword keyword) {
    keyword.setUsed(true);
    keyword.setProvidedDate(LocalDate.now());
    keywordRepository.save(keyword);
    log.info("키워드 사용 처리 완료: keyword={}, isUsed={}, providedDate={}",
        keyword.getKoreanKeyword(), keyword.isUsed(), keyword.getProvidedDate());
  }

  /**
   *  어제 카테고리를 기반으로 순환 방식으로 다음 카테고리 선택
   *    - allCategories 배열 순서대로 순환
   *    - 계절 카테고리라면 현재 월과 일치하는지 확인 후 선택
   */
  private KeywordCategory getNextCategory(LocalDate yesterday) {
    // 어제 제공된 키워드 조회
    Optional<Keyword> optionalLastKeyword = keywordRepository.findByProvidedDate(yesterday);
    if (!optionalLastKeyword.isPresent()) {
      log.info("어제 키워드 없음 → 현재 시즌 카테고리 선택");
      return getSeasonCategory();
    }

    // 어제 사용된 카테고리 찾기
    Keyword lastKeyword = optionalLastKeyword.get();
    int lastIndex = indexOfCategory(lastKeyword.getKeywordCategory());
    int nextIndex = (lastIndex + 1) % allCategories.size();

    // 다음 카테고리가 계절이면 현재 월과 비교
    while (isSeasonCategory(allCategories.get(nextIndex)) && allCategories.get(nextIndex) != getSeasonCategory()) {
      log.info("계절 불일치 → 다음 카테고리로 이동: {} → {}", allCategories.get(nextIndex), allCategories.get((nextIndex + 1) % allCategories.size()));
      nextIndex = (nextIndex + 1) % allCategories.size();
    }

    log.info("최종 선택된 카테고리: {}", allCategories.get(nextIndex));
    return allCategories.get(nextIndex);
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
    for (int i = 0; i < allCategories.size(); i++) {
      if (allCategories.get(i) == category) return i;
    }
    return 0;
  }

  /**
   *  추후에 mapstruct로 변환 예정
   */
  private KeywordRequest toKeywordRequest(Keyword keyword) {
    return entityMapper.toKeywordRequest(keyword);
  }
}
