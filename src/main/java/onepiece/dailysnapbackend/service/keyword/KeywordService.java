package onepiece.dailysnapbackend.service.keyword;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import onepiece.dailysnapbackend.object.constants.KeywordCategory;
import onepiece.dailysnapbackend.object.dto.KeywordRequest;
import onepiece.dailysnapbackend.object.postgres.Keyword;
import onepiece.dailysnapbackend.repository.postgres.KeywordRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeywordService {

  private final KeywordRepository keywordRepository;
  private final KeywordSelectionService keywordSelectionService;

  /**
   * 🔹 특정 카테고리의 키워드 목록 조회
   */
  @Transactional(readOnly = true)
  public List<KeywordRequest> getKeywordsByCategory(KeywordCategory category) {
    List<Keyword> keywords = keywordRepository.findByCategory(category);
    List<KeywordRequest> keywordList = keywords.stream().map(this::toKeywordRequest).toList();

    log.info("[KeywordService] 카테고리 조회: category={}, count={}", category, keywordList.size());
    return keywordList;
  }

  /**
   * 🔹 특정 날짜의 제공된 키워드 조회 (오늘 포함, 미래 조회 불가)
   */
  @Transactional(readOnly = true)
  public List<KeywordRequest> getKeywordsByDate(LocalDate date) {
    LocalDate today = LocalDate.now();

    // 오늘 날짜 조회 시, getTodayKeyword() 호출
    if (date.isEqual(today)) {
      KeywordRequest todayKeyword = keywordSelectionService.getTodayKeyword();
      log.info("[KeywordService] 오늘 날짜 키워드 조회: keyword='{}', category='{}'", todayKeyword.getKeyword(), todayKeyword.getCategory());
      return List.of(todayKeyword);
    }

    // 과거 날짜 조회
    List<Keyword> keywords = keywordRepository.findByProvidedDate(date);
    List<KeywordRequest> keywordList = keywords.stream().map(this::toKeywordRequest).toList();

    log.info("[KeywordService] 날짜별 키워드 조회: date={}, count={}", date, keywordList.size());
    return keywordList;
  }

  /**
   * 🔹 Keyword 엔티티를 KeywordRequest DTO로 변환 ( ***리펙토링 할게요 mapstruct로 수정 예정*** )
   */
  private KeywordRequest toKeywordRequest(Keyword keyword) {
    return KeywordRequest.builder()
        .keyword(keyword.getKeyword())
        .category(keyword.getCategory().name())
        .specifiedDate(keyword.getSpecifiedDate())
        .providedDate(keyword.getProvidedDate())
        .build();
  }
}
