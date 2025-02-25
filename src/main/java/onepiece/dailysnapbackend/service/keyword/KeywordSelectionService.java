package onepiece.dailysnapbackend.service.keyword;

import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import onepiece.dailysnapbackend.object.constants.KeywordCategory;
import onepiece.dailysnapbackend.object.dto.KeywordRequest;
import onepiece.dailysnapbackend.object.postgres.Keyword;
import onepiece.dailysnapbackend.repository.postgres.KeywordRepository;
import onepiece.dailysnapbackend.util.exception.CustomException;
import onepiece.dailysnapbackend.util.exception.ErrorCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeywordSelectionService {

  private final KeywordRepository keywordRepository;
  private final OpenAIKeywordService openAIKeywordService;
  private static final int KEYWORD_THRESHOLD = 5;

  @Transactional
  public KeywordRequest getTodayKeyword() {
    LocalDate today = LocalDate.now();
    Pageable pageable = PageRequest.of(0, 1);

    log.info("[KeywordSelectionService] 오늘 날짜 키워드 조회 시작: {}", today);

    // 1️⃣ ADMIN_SET 카테고리에서 오늘 날짜의 키워드 확인
    Page<Keyword> adminKeywords = keywordRepository.findAdminSetKeyword(today, pageable);
    if (!adminKeywords.isEmpty()) {
      Keyword keyword = adminKeywords.getContent().get(0);
      log.info("[KeywordSelectionService] ADMIN_SET에서 오늘의 키워드를 찾음: {}", keyword);
      return toKeywordRequest(keyword);
    }

    // 2️⃣ 사용할 키워드 카테고리 순환 (랜덤 선택)
    KeywordCategory selectedCategory = selectCategory();

    // 3️⃣ 해당 카테고리에서 isUsed=false인 키워드 조회
    Page<Keyword> unusedKeywords = keywordRepository.findUnusedKeywords(selectedCategory, pageable);
    if (!unusedKeywords.isEmpty()) {
      Keyword keyword = unusedKeywords.getContent().get(0);
      markKeywordAsUsed(keyword);
      log.info("[KeywordSelectionService] 사용되지 않은 키워드를 선택함: {}", keyword);
      return toKeywordRequest(keyword);
    }

    // 4️⃣ 사용 가능한 키워드 개수 확인
    long remainingCount = keywordRepository.countUnusedKeywords(selectedCategory);
    if (remainingCount <= KEYWORD_THRESHOLD) {
      log.warn("[KeywordSelectionService] '{}' 카테고리의 키워드 개수가 부족하여 OpenAI 호출", selectedCategory);
      openAIKeywordService.generateKeywords(selectedCategory);
    }

    // 5️⃣ 새롭게 생성된 키워드를 다시 검색
    Page<Keyword> newKeywords = keywordRepository.findUnusedKeywords(selectedCategory, pageable);
    if (!newKeywords.isEmpty()) {
      Keyword keyword = newKeywords.getContent().get(0);
      markKeywordAsUsed(keyword);
      log.info("[KeywordSelectionService] OpenAI에서 새 키워드를 가져와 사용함: {}", keyword);
      return toKeywordRequest(keyword);
    }

    throw new CustomException(ErrorCode.KEYWORD_NOT_FOUND);
  }

  /**
   * 키워드를 제공된 상태로 변경
   */
  private void markKeywordAsUsed(Keyword keyword) {
    keyword.setUsed(true);
    keyword.setProvidedDate(LocalDate.now());
    keywordRepository.save(keyword);
  }

  /**
   * 카테고리를 랜덤으로 선택 (순환 로직 적용 가능)
   */
  private KeywordCategory selectCategory() {
    KeywordCategory[] categories = {
        KeywordCategory.SPRING, KeywordCategory.SUMMER, KeywordCategory.AUTUMN, KeywordCategory.WINTER,
        KeywordCategory.TRAVEL, KeywordCategory.DAILY, KeywordCategory.ABSTRACT, KeywordCategory.RANDOM
    };
    return categories[new Random().nextInt(categories.length)];
  }

  /**
   * Keyword 엔티티를 KeywordRequest DTO로 변환 (추후에 추가 예정)
   */
  private KeywordRequest toKeywordRequest(Keyword keyword) {
    return KeywordRequest.builder()
        .keyword(keyword.getKeyword())
        .category(keyword.getCategory())
        .specifiedDate(keyword.getSpecifiedDate())
        .providedDate(keyword.getProvidedDate())
        .build();
  }
}
