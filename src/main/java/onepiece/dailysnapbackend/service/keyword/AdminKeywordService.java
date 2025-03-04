package onepiece.dailysnapbackend.service.keyword;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import onepiece.dailysnapbackend.object.constants.KeywordCategory;
import onepiece.dailysnapbackend.object.dto.KeywordRequest;
import onepiece.dailysnapbackend.object.dto.KeywordResponse;
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
   *  특정 날짜에 제공할 키워드 추가 (관리자 전용)
   */
  @Transactional
  public KeywordResponse addKeyword(KeywordRequest request) {
    // 입력 유효성 검사
    if (request.getKeyword() == null || request.getKeyword().trim().isEmpty()) {
      log.error("키워드가 null이거나 빈 값: request={}", request);
      throw new CustomException(ErrorCode.INVALID_REQUEST);
    }

    if (request.getSpecifiedDate() == null) {
      log.error("specifiedDate가 null: request={}", request);
      throw new CustomException(ErrorCode.INVALID_REQUEST);
    }

    // specifiedDate가 오늘 이후인지 확인
    LocalDate today = LocalDate.now();
    if (!request.getSpecifiedDate().isAfter(today)) {
      log.error("지정 날짜가 오늘 이전: specifiedDate={}", request.getSpecifiedDate());
      throw new CustomException(ErrorCode.INVALID_SPECIFIED_DATE);
    }

    // 중복 키워드 체크
    if (keywordRepository.existsByKeyword(request.getKeyword())) {
      log.error("이미 존재하는 키워드: {}", request.getKeyword());
      throw new CustomException(ErrorCode.KEYWORD_ALREADY_EXISTS);
    }

    Keyword keywordEntity = Keyword.builder()
        .keyword(request.getKeyword())
        .category(KeywordCategory.ADMIN_SET)
        .specifiedDate(request.getSpecifiedDate())
        .isUsed(false)
        .build();

    log.debug("저장 전 키워드 객체: keyword={}, specifiedDate={}",
        keywordEntity.getKeyword(), keywordEntity.getSpecifiedDate());

    Keyword savedKeyword = keywordRepository.save(keywordEntity);

    log.info("'{}' 날짜에 제공될 키워드 '{}' 추가 완료, savedId={}",
        savedKeyword.getSpecifiedDate(), savedKeyword.getKeywordId());

    return KeywordResponse.builder()
        .keyword(savedKeyword.getKeyword())
        .category(savedKeyword.getCategory())
        .specifiedDate(savedKeyword.getSpecifiedDate())
        .providedDate(savedKeyword.getProvidedDate())
        .build();
  }


  /**
   *  특정 키워드 삭제 (관리자 전용)
   */
  @Transactional
  public void deleteKeyword(String keyword) {
    if (!keywordRepository.existsByKeyword(keyword)) {
      log.error("삭제 요청한 키워드를 찾을 수 없음: {}", keyword);
      throw new CustomException(ErrorCode.KEYWORD_NOT_FOUND);
    }

    keywordRepository.deleteKeywordByKeyword(keyword);
    log.info("삭제된 키워드: {}", keyword);
  }
}