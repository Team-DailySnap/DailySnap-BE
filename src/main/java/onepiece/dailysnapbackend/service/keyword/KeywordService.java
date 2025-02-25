package onepiece.dailysnapbackend.service.keyword;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import onepiece.dailysnapbackend.object.dto.KeywordFilterRequest;
import onepiece.dailysnapbackend.object.dto.KeywordFilterResponse;
import onepiece.dailysnapbackend.object.postgres.Keyword;
import onepiece.dailysnapbackend.repository.postgres.KeywordRepository;
import onepiece.dailysnapbackend.util.exception.CustomException;
import onepiece.dailysnapbackend.util.exception.ErrorCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeywordService {

  private final KeywordRepository keywordRepository;

  @Transactional(readOnly = true)
  public Page<KeywordFilterResponse> filteredKeywords(KeywordFilterRequest request) {
    log.info("[KeywordService] filteredKeywords() 호출됨");
    log.info("요청 데이터: keyword={}, category={}, providedDate={}, pageNumber={}, pageSize={}, sortField={}, sortDirection={}",
        request.getKeyword(), request.getCategory(), request.getProvidedDate(),
        request.getPageNumber(), request.getPageSize(), request.getSortField(), request.getSortDirection());

    // 1. pageSize 검증 (최대 100 제한)
    if (request.getPageSize() > 100) {
      log.error("[KeywordService] 잘못된 요청: pageSize 값이 100을 초과함");
      throw new CustomException(ErrorCode.INVALID_REQUEST);
    }

    // 2. sortDirection 검증 (ASC or DESC)
    String sortDirection = request.getSortDirection().toUpperCase();
    if (!sortDirection.equals("ASC") && !sortDirection.equals("DESC")) {
      log.error("[KeywordService] 잘못된 요청: sortDirection 값이 ASC 또는 DESC가 아님");
      throw new CustomException(ErrorCode.INVALID_REQUEST);
    }

    // 3. sortField 검증 (Keyword 엔티티에 존재하는 필드인지 확인)
    List<String> allowedFields = List.of("provided_date", "keyword");
    if (!allowedFields.contains(request.getSortField())) {
      log.error("[KeywordService] 잘못된 요청: sortField 값이 유효하지 않음 - {}", request.getSortField());
      throw new CustomException(ErrorCode.INVALID_REQUEST);
    }

    // 4. Pageable 설정
    Pageable pageable = PageRequest.of(
        request.getPageNumber(),
        request.getPageSize(),
        Sort.by(Sort.Direction.fromString(sortDirection), request.getSortField())
    );

    // 5. 필터링할 providedDate 값 설정 (NULL 허용)
    LocalDate providedDate = request.getProvidedDate();
    log.info("[KeywordService] 필터링 실행: keyword={}, category={}, providedDate={}", request.getKeyword(), request.getCategory(), providedDate);

    // 6. 필터링 실행 (쿼리 실행 로그 포함)
    Page<Keyword> page;
    try {
      page = keywordRepository.filteredKeyword(
          request.getKeyword(),
          request.getCategory(),
          providedDate,
          pageable
      );
      log.info("[KeywordService] 쿼리 실행 완료. 결과 개수: {}", page.getTotalElements());
    } catch (Exception e) {
      log.error("[KeywordService] 쿼리 실행 중 예외 발생: ", e);
      throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    // 7. Keyword 엔티티를 DTO로 변환하여 반환
    return page.map(this::toKeywordFilterResponse);
  }

  /**
   * Keyword 엔티티를 KeywordFilterResponse DTO로 변환 (추후에 추가 예정)
   */
  private KeywordFilterResponse toKeywordFilterResponse(Keyword keyword) {
    return KeywordFilterResponse.builder()
        .keyword(keyword.getKeyword())
        .category(keyword.getCategory())
        .providedDate(keyword.getProvidedDate())
        .build();
  }
}
