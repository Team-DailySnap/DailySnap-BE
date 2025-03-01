package onepiece.dailysnapbackend.service.keyword;

import java.time.LocalDate;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import onepiece.dailysnapbackend.object.constants.KeywordCategory;
import onepiece.dailysnapbackend.object.dto.KeywordFilterRequest;
import onepiece.dailysnapbackend.object.dto.KeywordFilterResponse;
import onepiece.dailysnapbackend.object.dto.KeywordRequest;
import onepiece.dailysnapbackend.object.postgres.Keyword;
import onepiece.dailysnapbackend.repository.postgres.KeywordRepository;
import onepiece.dailysnapbackend.util.exception.CustomException;
import onepiece.dailysnapbackend.util.exception.ErrorCode;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeywordService {

  private final KeywordRepository keywordRepository;
  private final KeywordSelectionService keywordSelectionService;

  /**
   *  키워드 필터링 및 조회
   * - 특정 날짜의 키워드를 조회하거나, 전체 키워드를 필터링하여 반환
   */
  @Transactional
  public Page<KeywordFilterResponse> filteredKeywords(KeywordFilterRequest request) {
    log.info("[KeywordService] 키워드 필터링 시작: providedDate={}, keyword={}, category={}",
        request.getProvidedDate(), request.getKeyword(), request.getCategory());

    Pageable pageable = createPageable(request);
    LocalDate providedDate = request.getProvidedDate();
    LocalDate today = LocalDate.now();

    return (providedDate != null)
        ? handleProvidedDateFiltering(providedDate, today, pageable)
        : performFiltering(request, pageable);
  }

  /**
   *  페이지네이션 및 정렬 설정
   */
  private Pageable createPageable(KeywordFilterRequest request) {
    return PageRequest.of(
        request.getPageNumber(),
        request.getPageSize(),
        Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortField())
    );
  }

  /**
   *  특정 날짜(`providedDate`) 필터링 처리
   * - 미래 날짜 요청이면 예외 반환
   * - 해당 날짜의 키워드가 존재하면 반환
   * - 과거 키워드가 없으면 빈 페이지 반환
   * - 오늘 키워드가 없으면 새 키워드 생성
   */
  private Page<KeywordFilterResponse> handleProvidedDateFiltering(LocalDate providedDate, LocalDate today, Pageable pageable) {
    if (providedDate.isAfter(today)) {
      log.warn("[KeywordService] 미래 날짜({}) 조회 불가", providedDate);
      throw new CustomException(ErrorCode.INVALID_DATE_REQUEST);
    }

    Keyword existingKeyword = keywordRepository.findFirstByProvidedDate(providedDate).orElse(null);
    if (existingKeyword != null) {
      log.info("[KeywordService] providedDate={}에 키워드 존재: keyword={}", providedDate, existingKeyword.getKeyword());
      return wrapKeywordAsPage(existingKeyword, pageable);
    }

    if (providedDate.isBefore(today)) {
      log.info("[KeywordService] 과거 날짜({})에 키워드 없음", providedDate);
      return Page.empty(pageable);
    }

    return generateTodayKeywordPage(pageable);
  }

  /**
   *  필터링 수행 (providedDate가 없는 경우)
   * - 키워드, 카테고리, 사용 여부를 기반으로 필터링
   */
  private Page<KeywordFilterResponse> performFiltering(KeywordFilterRequest request, Pageable pageable) {
    Page<Keyword> page = keywordRepository.filteredKeyword(
        request.getKeyword(),
        request.getCategory(),
        null,
        request.getIsUsed(),
        pageable
    );

    if (page.isEmpty()) {
      log.warn("[KeywordService] 필터링 결과 없음: keyword={}, category={}", request.getKeyword(), request.getCategory());
      return Page.empty(pageable);
    }

    log.info("[KeywordService] 필터링 완료: totalElements={}", page.getTotalElements());
    return page.map(this::toKeywordFilterResponse);
  }

  /**
   *  오늘 날짜에 키워드가 없는 경우 새 키워드 생성
   * - `KeywordSelectionService`를 통해 키워드 자동 생성
   */
  @Transactional
  public Page<KeywordFilterResponse> generateTodayKeywordPage(Pageable pageable) {
    log.info("[KeywordService] 오늘 날짜에 키워드가 없음 → 새 키워드 생성 시도");
    KeywordRequest newKeyword = keywordSelectionService.getTodayKeyword();
    log.info("[KeywordService] 새 키워드 생성 완료: keyword={}", newKeyword.getKeyword());

    Page<Keyword> page = keywordRepository.filteredKeyword(
        newKeyword.getKeyword(),
        newKeyword.getCategory().name(),
        LocalDate.now(),
        null,
        pageable
    );

    if (page.isEmpty()) {
      log.warn("[KeywordService] 생성된 키워드 조회 실패: keyword={}", newKeyword.getKeyword());
      return Page.empty(pageable);
    }

    return page.map(this::toKeywordFilterResponse);
  }

  /**
   *  단일 키워드를 페이지로 변환
   */
  private Page<KeywordFilterResponse> wrapKeywordAsPage(Keyword keyword, Pageable pageable) {
    return new PageImpl<>(Collections.singletonList(toKeywordFilterResponse(keyword)), pageable, 1);
  }

  /**
   *  추후에 mapstruct로 변환 예정
   */
  private KeywordFilterResponse toKeywordFilterResponse(Keyword keyword) {
    return KeywordFilterResponse.builder()
        .keyword(keyword.getKeyword())
        .category(KeywordCategory.valueOf(keyword.getCategory().name()))
        .providedDate(keyword.getProvidedDate())
        .build();
  }
}
