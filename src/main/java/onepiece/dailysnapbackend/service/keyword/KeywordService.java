package onepiece.dailysnapbackend.service.keyword;

import java.time.LocalDate;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import onepiece.dailysnapbackend.mapper.KeywordMapper;
import onepiece.dailysnapbackend.object.dto.KeywordFilterRequest;
import onepiece.dailysnapbackend.object.dto.KeywordFilterResponse;
import onepiece.dailysnapbackend.object.dto.KeywordRequest;
import onepiece.dailysnapbackend.object.postgres.Keyword;
import onepiece.dailysnapbackend.repository.postgres.KeywordRepository;
import onepiece.dailysnapbackend.util.CommonUtil;
import onepiece.dailysnapbackend.util.exception.CustomException;
import onepiece.dailysnapbackend.util.exception.ErrorCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeywordService {

  private final KeywordRepository keywordRepository;
  private final KeywordSelectionService keywordSelectionService;
  private final KeywordMapper keywordMapper = KeywordMapper.INSTANCE;

  /**
   *  키워드 필터링 및 조회
   */
  @Transactional
  public Page<KeywordFilterResponse> filteredKeywords(KeywordFilterRequest request) {

    Pageable pageable = createPageable(request);
    LocalDate providedDate = StringUtils.hasText(request.getProvidedDate()) ? LocalDate.parse(request.getProvidedDate()) : null;
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
   *  특정 날짜(providedDate) 필터링 처리
   */
  private Page<KeywordFilterResponse> handleProvidedDateFiltering(LocalDate providedDate, LocalDate today, Pageable pageable) {
    if (providedDate.isAfter(today)) {
      log.error("미래 날짜({}) 조회 불가", providedDate);
      throw new CustomException(ErrorCode.INVALID_DATE_REQUEST);
    }

    Keyword existingKeyword = keywordRepository.findByProvidedDate(providedDate)
        .orElseGet(() -> null);
    if (existingKeyword != null) {
      log.info("providedDate={}에 키워드 존재: keyword={}", providedDate, existingKeyword.getKeyword());
      return wrapKeywordAsPage(existingKeyword, pageable);
    }

    if (providedDate.isBefore(today)) {
      log.info("과거 날짜({})에 키워드 없음", providedDate);
      return Page.empty(pageable);
    }

    return generateTodayKeywordPage(pageable);
  }

  /**
   *  필터링 수행 (providedDate가 없는 경우)
   */
  private Page<KeywordFilterResponse> performFiltering(KeywordFilterRequest request, Pageable pageable) {
    String keyword = CommonUtil.nvl(request.getKeyword(), "");
    String category = CommonUtil.nvl(request.getCategory(), "");
    String providedDate = CommonUtil.nvl(request.getProvidedDate(), "");
    String isUsed = CommonUtil.nvl(request.getIsUsed() != null ? request.getIsUsed().toString() : "", "");

    Page<Keyword> page = keywordRepository.filteredKeyword(keyword, category, providedDate, isUsed, pageable);
    if (page.isEmpty()) {
      log.error("필터링 결과 없음: keyword={}, category={}", keyword, category);
      return Page.empty(pageable);
    }

    log.info("필터링 완료: totalElements={}", page.getTotalElements());
    return page.map(keywordMapper::toKeywordFilterResponse);
  }

  /**
   *  오늘 날짜에 키워드가 없는 경우 새 키워드 생성
   */
  @Transactional
  public Page<KeywordFilterResponse> generateTodayKeywordPage(Pageable pageable) {
    log.info("오늘 날짜에 키워드가 없음 → 새 키워드 생성 시도");
    KeywordRequest newKeyword = keywordSelectionService.getTodayKeyword();
    log.info("새 키워드 생성 완료: keyword={}", newKeyword.getKeyword());

    Page<Keyword> page = keywordRepository.filteredKeyword(
        newKeyword.getKeyword(),
        newKeyword.getCategory().name(),
        LocalDate.now().toString(),
        "true",
        pageable
    );

    if (page.isEmpty()) {
      log.error("생성된 키워드 조회 실패: keyword={}", newKeyword.getKeyword());
      return Page.empty(pageable);
    }

    return page.map(keywordMapper::toKeywordFilterResponse);
  }

  /**
   *  단일 키워드를 페이지로 변환
   */
  private Page<KeywordFilterResponse> wrapKeywordAsPage(Keyword keyword, Pageable pageable) {
    return new PageImpl<>(Collections.singletonList(keywordMapper.toKeywordFilterResponse(keyword)), pageable, 1);
  }

}