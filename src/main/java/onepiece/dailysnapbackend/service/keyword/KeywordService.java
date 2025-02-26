package onepiece.dailysnapbackend.service.keyword;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import onepiece.dailysnapbackend.object.dto.KeywordFilterRequest;
import onepiece.dailysnapbackend.object.dto.KeywordFilterResponse;
import onepiece.dailysnapbackend.object.dto.KeywordRequest;
import onepiece.dailysnapbackend.object.postgres.Keyword;
import onepiece.dailysnapbackend.repository.postgres.KeywordRepository;
import onepiece.dailysnapbackend.util.exception.CustomException;
import onepiece.dailysnapbackend.util.exception.ErrorCode;
import org.springframework.dao.DataAccessException;
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
  private final KeywordSelectionService keywordSelectionService;

  @Transactional(readOnly = true)
  public Page<KeywordFilterResponse> filteredKeywords(KeywordFilterRequest request) {
    log.info("[KeywordService] filteredKeywords() 호출됨");
    log.info("요청 데이터: keyword={}, category={}, providedDate={}, pageNumber={}, pageSize={}, sortField={}, sortDirection={}",
        request.getKeyword(), request.getCategory(), request.getProvidedDate(),
        request.getPageNumber(), request.getPageSize(), request.getSortField(), request.getSortDirection());

    Sort sort = Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortField());

    Pageable pageable = PageRequest.of(
        request.getPageNumber(),
        request.getPageSize(),
        sort
    );

    LocalDate providedDate = request.getProvidedDate();

    // 제공 날짜에 해당하는 키워드가 없으면 OpenAI 서비스 호출
    if (providedDate != null && keywordRepository.findAdminSetKeyword(providedDate) == null) {
      log.warn("[KeywordService] 제공 날짜({})에 해당하는 키워드 없음 → 새 키워드 생성 요청", providedDate);
      try {
        KeywordRequest newKeyword = keywordSelectionService.getTodayKeyword(); // 반환값 저장
        log.info("[KeywordService] 새로 생성된 키워드: {}", newKeyword.getKeyword());
      } catch (Exception e) {
        log.error("[KeywordService] 키워드 생성 중 예외 발생: {}", e.getMessage(), e);
        throw new CustomException(ErrorCode.KEYWORD_SAVE_FAILED);
      }
    }

    Page<Keyword> page;
    try {
      page = keywordRepository.filteredKeyword(
          request.getKeyword(),
          request.getCategory(),
          providedDate,
          pageable
      );
      log.info("[KeywordService] 쿼리 실행 완료. 결과 개수: {}", page.getTotalElements());
    } catch (DataAccessException e) { // ✅ Spring Data 접근 예외 처리
      log.error("🛑 데이터 접근 예외 발생: {}", e.getMessage(), e);
      throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
    } catch (Exception e) { // ✅ 일반적인 예외 처리
      log.error("❌ 예기치 않은 예외 발생: {}", e.getMessage(), e);
      throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    if (page.isEmpty()) {
      log.warn("[KeywordService] 필터링 결과가 없습니다. (keyword={}, category={}, providedDate={})",
          request.getKeyword(), request.getCategory(), providedDate);
      throw new CustomException(ErrorCode.KEYWORD_NOT_FOUND);
    }

    return page.map(this::toKeywordFilterResponse);
  }

  // **추후에 Mapstruct 추가 예정**
  private KeywordFilterResponse toKeywordFilterResponse(Keyword keyword) {
    return KeywordFilterResponse.builder()
        .keyword(keyword.getKeyword())
        .category(keyword.getCategory())
        .providedDate(keyword.getProvidedDate())
        .build();
  }
}
