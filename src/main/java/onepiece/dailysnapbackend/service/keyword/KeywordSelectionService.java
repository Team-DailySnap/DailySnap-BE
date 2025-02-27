package onepiece.dailysnapbackend.service.keyword;

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
  private static final int KEYWORD_THRESHOLD = 5;

  private static int lastIndex = -1; // 이전 선택한 카테고리 인덱스
  private static final KeywordCategory[] allCategories = {
      KeywordCategory.SPRING, KeywordCategory.SUMMER, KeywordCategory.AUTUMN, KeywordCategory.WINTER,
      KeywordCategory.TRAVEL, KeywordCategory.DAILY, KeywordCategory.ABSTRACT, KeywordCategory.RANDOM
  };

  @Transactional
  public KeywordRequest getTodayKeyword() {
    LocalDate today = LocalDate.now();

    log.info("[KeywordSelectionService] 오늘 날짜 키워드 조회 시작: {}", today);

    // ADMIN_SET 카테고리에서 오늘 날짜의 키워드 확인
    Keyword adminKeyword = keywordRepository.findAdminSetKeyword(today);
    if (adminKeyword != null) {
      log.info("[KeywordSelectionService] ADMIN_SET에서 오늘의 키워드를 찾음: {}", adminKeyword);
      return toKeywordRequest(adminKeyword);
    }

    // 계절 기반 카테고리 선택 (순차적 순환)
    KeywordCategory selectedCategory = selectCategory();

    // 해당 카테고리에서 isUsed=false인 키워드 조회
    Keyword unusedKeyword = keywordRepository.findUnusedKeyword(selectedCategory);
    if (unusedKeyword == null) {
      log.warn("[KeywordSelectionService] '{}' 카테고리에서 사용 가능한 키워드가 없음. OpenAI에서 생성한 후 다시 조회", selectedCategory);

      openAIKeywordService.generateKeywords(selectedCategory); // ✅ REQUIRES_NEW 트랜잭션에서 실행
      keywordRepository.flush(); // 즉시 DB 반영

      unusedKeyword = keywordRepository.findUnusedKeyword(selectedCategory);
    }

    if (unusedKeyword != null) {
      markKeywordAsUsed(unusedKeyword);
      log.info("[KeywordSelectionService] OpenAI에서 새 키워드를 가져와 사용함: {}", unusedKeyword);
      return toKeywordRequest(unusedKeyword);
    }



    // 사용 가능한 키워드 개수 확인
    long remainingCount = keywordRepository.countByCategoryAndIsUsedFalse(selectedCategory);
    if (remainingCount <= KEYWORD_THRESHOLD) {
      log.info("[KeywordSelectionService] '{}' 카테고리의 키워드 개수가 부족하여 OpenAI 호출", selectedCategory);
      openAIKeywordService.generateKeywords(selectedCategory);
    }
    // 새롭게 생성된 키워드를 다시 검색
    Keyword newKeyword = keywordRepository.findUnusedKeyword(selectedCategory);
    if (newKeyword != null) {
      markKeywordAsUsed(newKeyword);
      log.info("[KeywordSelectionService] OpenAI에서 새 키워드를 가져와 사용함: {}", newKeyword);
      return toKeywordRequest(newKeyword);
    }

    throw new CustomException(ErrorCode.KEYWORD_NOT_FOUND);
  }

  private void markKeywordAsUsed(Keyword keyword) {
    keyword.setUsed(true);
    keyword.setProvidedDate(LocalDate.now());
    keywordRepository.save(keyword);
  }

  /**
   * 순차적으로 카테고리를 선택하는 로직
   */
  private KeywordCategory selectCategory() {
    int month = LocalDate.now().getMonthValue(); // 현재 월 가져오기
    KeywordCategory[] seasonCategories;

    // 3~5월: 봄
    if (month >= 3 && month <= 5) {
      seasonCategories = new KeywordCategory[]{KeywordCategory.SPRING};
    }
    // ☀6~8월: 여름
    else if (month >= 6 && month <= 8) {
      seasonCategories = new KeywordCategory[]{KeywordCategory.SUMMER};
    }
    // 9~11월: 가을
    else if (month >= 9 && month <= 11) {
      seasonCategories = new KeywordCategory[]{KeywordCategory.AUTUMN};
    }
    // 12~2월: 겨울
    else {
      seasonCategories = new KeywordCategory[]{KeywordCategory.WINTER};
    }

    // 현재 계절이면 해당 계절만 선택
    if (lastIndex == -1 || contains(seasonCategories, allCategories[lastIndex])) {
      lastIndex = 0; // 계절이 맞으면 처음부터 다시 시작
      log.info("[KeywordSelectionService] 계절에 맞는 카테고리 선택: {}", seasonCategories[0]);
      return seasonCategories[0];
    }

    // 계절이 아니면 전체 순환 (이전 선택 다음 순서)
    lastIndex = (lastIndex + 1) % allCategories.length;
    log.info("[KeywordSelectionService] 순차적으로 선택된 카테고리: {}", allCategories[lastIndex]);
    return allCategories[lastIndex];
  }

  // 특정 배열에 값이 존재하는지 확인
  private boolean contains(KeywordCategory[] categories, KeywordCategory category) {
    for (KeywordCategory c : categories) {
      if (c == category) return true;
    }
    return false;
  }

  // **추후에 Mapstruct 추가 예정**
  private KeywordRequest toKeywordRequest(Keyword keyword) {
    return KeywordRequest.builder()
        .keyword(keyword.getKeyword())
        .category(keyword.getCategory())
        .specifiedDate(keyword.getSpecifiedDate())
        .providedDate(keyword.getProvidedDate())
        .build();
  }
}
