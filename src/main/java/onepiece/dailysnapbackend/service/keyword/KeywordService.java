package onepiece.dailysnapbackend.service.keyword;

import java.time.LocalDate;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import onepiece.dailysnapbackend.object.dto.KeywordFilterRequest;
import onepiece.dailysnapbackend.object.dto.KeywordResponse;
import onepiece.dailysnapbackend.object.postgres.Keyword;
import onepiece.dailysnapbackend.repository.postgres.KeywordQueryDslRepository;
import onepiece.dailysnapbackend.repository.postgres.KeywordRepository;
import onepiece.dailysnapbackend.util.exception.CustomException;
import onepiece.dailysnapbackend.util.exception.ErrorCode;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeywordService {

  private final KeywordRepository keywordRepository;
  private final KeywordQueryDslRepository keywordQueryDslRepository;

  /**
   * 키워드 필터링 조회
   */
  @Transactional(readOnly = true)
  public Page<KeywordResponse> filteredKeywords(KeywordFilterRequest request) {
    Page<Keyword> keywordPage = keywordQueryDslRepository.filteredKeyword(request);
    return keywordPage.map(KeywordResponse::of);
  }

  public Keyword findKeywordById(UUID keywordId) {
    return keywordRepository.findById(keywordId)
        .orElseThrow(() -> {
          log.error("요청 PK: {}에 해당하는 키워드를 찾을 수 없음", keywordId);
          return new CustomException(ErrorCode.KEYWORD_NOT_FOUND);
        });
  }

  public Keyword findKeywordByProvidedDate(LocalDate providedDate) {
    return keywordRepository.findByProvidedDate(providedDate)
        .orElseThrow(() -> {
          log.error("날짜: {}에 해당하는 키워드를 찾을 수 없음", providedDate);
          return new CustomException(ErrorCode.KEYWORD_NOT_FOUND);
        });
  }
}