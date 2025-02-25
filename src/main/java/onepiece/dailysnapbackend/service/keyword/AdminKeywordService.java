package onepiece.dailysnapbackend.service.keyword;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import onepiece.dailysnapbackend.object.constants.KeywordCategory;
import onepiece.dailysnapbackend.object.dto.KeywordRequest;
import onepiece.dailysnapbackend.object.postgres.Keyword;
import onepiece.dailysnapbackend.repository.postgres.KeywordRepository;
import onepiece.dailysnapbackend.util.exception.CustomException;
import onepiece.dailysnapbackend.util.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminKeywordService {

  private final KeywordRepository keywordRepository;

  /**
   * 🔹 특정 카테고리의 키워드가 부족할 경우 OpenAI API를 사용하여 자동 생성
   */
  @Transactional
  public void generateKeywords(KeywordCategory category) {
    log.info("[AdminKeywordService] OpenAI API를 사용하여 '{}' 카테고리 키워드 생성 요청", category);

    // TODO: OpenAI API를 사용하여 키워드 생성

    log.info("[AdminKeywordService] '{}' 카테고리에 대한 키워드가 성공적으로 생성됨", category);
  }

  /**
   * 🔹 특정 날짜에 제공할 키워드 추가 (관리자 전용)
   */
  @Transactional
  public void addAdminKeyword(KeywordRequest request) {
    keywordRepository.save(
        Keyword.builder()
            .keywordId(UUID.randomUUID())
            .category(KeywordCategory.ADMIN_SET)
            .specifiedDate(request.getSpecifiedDate())
            .isUsed(false)
            .build()
    );
    log.info("[AdminKeywordService] '{}' 날짜에 제공될 키워드 '{}' 추가 완료", request.getSpecifiedDate(), request.getKeyword());
  }

  /**
   * 🔹 특정 키워드 삭제 (관리자 전용)
   */
  @Transactional
  public void deleteKeyword(UUID id) {
    if (!keywordRepository.existsById(id)) {
      log.error("[AdminKeywordService] 삭제 요청한 키워드를 찾을 수 없음: {}", id);
      throw new CustomException(ErrorCode.KEYWORD_NOT_FOUND);
    }

    keywordRepository.deleteById(id);
    log.info("[AdminKeywordService] 삭제된 키워드: {}", id);
  }
}
