package onepiece.dailysnapbackend.service.keyword;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import onepiece.dailysnapbackend.mapper.EntityMapper;
import onepiece.dailysnapbackend.object.dto.DailyKeywordResponse;
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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeywordService {

  private final KeywordRepository keywordRepository;
  private final KeywordSelectionService keywordSelectionService;
  private final EntityMapper entityMapper;
  private final RedisTemplate<String, String> redisTemplate;

  private static final String KEYWORD_CACHE_KEY = "daily_keyword";

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

    Optional<Keyword> optionalKeyword = keywordRepository.findByProvidedDate(providedDate);
    if (optionalKeyword.isPresent()) {
      Keyword existingKeyword = optionalKeyword.get();
      log.info("providedDate={}에 키워드 존재: keyword={}", providedDate, existingKeyword.getKoreanKeyword());
      return new PageImpl<>(Collections.singletonList(entityMapper.toKeywordFilterResponse(existingKeyword)), pageable, 1);    }

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
    Boolean isUsed = request.getIsUsed();

    Page<Keyword> page = keywordRepository.filteredKeyword(keyword, category, providedDate, isUsed, pageable);
    if (page.isEmpty()) {
      log.error("필터링 결과 없음: keyword={}, category={}", keyword, category);
      return Page.empty(pageable);
    }

    log.info("필터링 완료: totalElements={}", page.getTotalElements());
    return page.map(entityMapper::toKeywordFilterResponse);
  }

  /**
   *  오늘 날짜에 키워드가 없는 경우 새 키워드 생성
   */
  @Transactional
  public Page<KeywordFilterResponse> generateTodayKeywordPage(Pageable pageable) {
    log.info("오늘 날짜에 키워드가 없음 → 새 키워드 생성 시도");
    KeywordRequest newKeyword = keywordSelectionService.getTodayKeyword();
    log.info("새 키워드 생성 완료: keyword={}", newKeyword.getKoreanKeyword());

    boolean isUsed = true;
    Page<Keyword> page = keywordRepository.filteredKeyword(
        newKeyword.getKoreanKeyword(),
        newKeyword.getCategory().name(),
        LocalDate.now().toString(),
        isUsed,
        pageable
    );

    if (page.isEmpty()) {
      log.error("생성된 키워드 조회 실패: keyword={}", newKeyword.getKoreanKeyword());
      return Page.empty(pageable);
    }

    return page.map(entityMapper::toKeywordFilterResponse);
  }

  @Transactional(readOnly = true)
  public DailyKeywordResponse getDailyKeyword() {
    String keywordId = redisTemplate.opsForValue().get(KEYWORD_CACHE_KEY);
    // redis 에 오늘의 키워드가 없다면 DB 에서 조회 후 업데이트
    if (keywordId == null) {
      keywordId = fetchKeywordFromDB().toString();
      redisTemplate.opsForValue().set(KEYWORD_CACHE_KEY, keywordId);
    }

    Keyword keyword = keywordRepository.findKeywordByKeywordId(UUID.fromString(keywordId))
        .orElseThrow(() -> new CustomException(ErrorCode.KEYWORD_NOT_FOUND));

    log.info("오늘의 키워드: {}", keyword.getKoreanKeyword());
    return DailyKeywordResponse.builder()
        .keyword(keyword.getKoreanKeyword())
        .category(keyword.getCategory())
        .providedDate(keyword.getProvidedDate())
        .build();
  }

  // DB 에서 오늘의 키워드 id 받아오기
  private UUID fetchKeywordFromDB() {
    LocalDate today = LocalDate.now();
    Keyword keyword = keywordRepository.findByProvidedDate(today)
        .orElseThrow(() -> new CustomException(ErrorCode.KEYWORD_NOT_FOUND));

    log.info("{} 키워드: {}", today, keyword.getKoreanKeyword());
    return keyword.getKeywordId();
  }

  // 매일 자정 Redis 에 업데이트
  @Scheduled(cron = "0 0 0 * * ?")
  @Transactional(readOnly = true)
  public void refreshDailyKeyword() {
    UUID keywordId = fetchKeywordFromDB();
    log.info("오늘의 키워드를 업데이트했습니다. dailyKeyword: {}", keywordId);
    redisTemplate.opsForValue().set(KEYWORD_CACHE_KEY, keywordId.toString());
  }

  public Keyword findKeywordById(UUID keywordId) {
    return keywordRepository.findById(keywordId)
        .orElseThrow(() -> {
          log.error("삭제 요청한 키워드를 찾을 수 없음: {}", keywordId);
          return new CustomException(ErrorCode.KEYWORD_NOT_FOUND);
        });
  }
}