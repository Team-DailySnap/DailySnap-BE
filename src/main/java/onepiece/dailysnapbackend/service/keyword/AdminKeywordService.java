package onepiece.dailysnapbackend.service.keyword;

import java.time.LocalDate;
import java.util.UUID;
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
  private final KeywordService keywordService;

  /**
   *  특정 날짜에 제공할 키워드 추가 (관리자 전용)
   */
  @Transactional
  public KeywordResponse addKeyword(KeywordRequest request) {

    // specifiedDate가 오늘 이후인지 확인
    LocalDate today = LocalDate.now();
    if (!request.getSpecifiedDate().isAfter(today)) {
      log.error("지정 날짜가 오늘 이전: specifiedDate={}", request.getSpecifiedDate());
      throw new CustomException(ErrorCode.INVALID_SPECIFIED_DATE);
    }

    // 중복 키워드 체크
    if (keywordRepository.existsByKoreanKeyword(request.getKoreanKeyword())) {
      log.error("이미 존재하는 키워드: {}", request.getKoreanKeyword());
      throw new CustomException(ErrorCode.KEYWORD_ALREADY_EXISTS);
    }

    Keyword keyword = Keyword.builder()
        .koreanKeyword(request.getKoreanKeyword())
        .englishKeyword(request.getEnglishKeyword())
        .keywordCategory(KeywordCategory.ADMIN_SET)
        .providedDate(request.getSpecifiedDate())
        .used(false)
        .build();

    Keyword savedKeyword = keywordRepository.save(keyword);

    return KeywordResponse.builder()
        .keywordId(savedKeyword.getKeywordId())
        .koreanKeyword(savedKeyword.getKoreanKeyword())
        .englishKeyword(savedKeyword.getEnglishKeyword())
        .keywordCategory(savedKeyword.getKeywordCategory())
        .providedDate(savedKeyword.getProvidedDate())
        .used(savedKeyword.isUsed())
        .build();
  }

  /**
   *  특정 키워드 삭제 (관리자 전용)
   */
  @Transactional
  public void deleteKeyword(UUID keywordId) {
    Keyword keyword = keywordService.findKeywordById(keywordId);
    keywordRepository.deleteById(keywordId);
    log.info("삭제된 키워드: {}", keyword.getKoreanKeyword());
  }
}