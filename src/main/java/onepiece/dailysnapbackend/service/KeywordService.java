package onepiece.dailysnapbackend.service;

import java.time.LocalDate;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import onepiece.dailysnapbackend.object.dto.DailyKeywordResponse;
import onepiece.dailysnapbackend.object.postgres.Keyword;
import onepiece.dailysnapbackend.repository.postgres.KeywordRepository;
import onepiece.dailysnapbackend.util.exception.CustomException;
import onepiece.dailysnapbackend.util.exception.ErrorCode;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeywordService {

  private final KeywordRepository keywordRepository;
  private final RedisTemplate<String, String> redisTemplate;

  private static final String KEYWORD_CACHE_KEY = "daily_keyword";

  public DailyKeywordResponse getDailyKeyword() {
    String keywordId = redisTemplate.opsForValue().get(KEYWORD_CACHE_KEY);
    // redis 에 오늘의 키워드가 없다면 DB 에서 조회 후 업데이트
    if (keywordId == null) {
      keywordId = fetchKeywordFromDB().toString();
      redisTemplate.opsForValue().set(KEYWORD_CACHE_KEY, keywordId);
    }

    Keyword keyword = keywordRepository.findKeywordByKeywordId(UUID.fromString(keywordId))
        .orElseThrow(() -> new CustomException(ErrorCode.KEYWORD_NOT_FOUND));

    log.info("오늘의 키워드: {}", keyword.getKeyword());
    return DailyKeywordResponse.builder()
        .keyword(keyword.getKeyword())
        .date(keyword.getDate())
        .build();
  }

  // DB 에서 오늘의 키워드 id 받아오기
  private UUID fetchKeywordFromDB() {
    LocalDate today = LocalDate.now();
    Keyword keyword = keywordRepository.findKeywordByDate(today)
        .orElseThrow(() -> new CustomException(ErrorCode.KEYWORD_NOT_FOUND));

    log.info("{} 키워드: {}", today, keyword.getKeyword());
    return keyword.getKeywordId();
  }

  // 매일 자정 Redis 에 업데이트
  @Scheduled(cron = "0 0 0 * * ?")
  public void refreshDailyKeyword() {
    UUID keywordId = fetchKeywordFromDB();
    log.info("오늘의 키워드를 업데이트했습니다. dailyKeyword: {}", keywordId);
    redisTemplate.opsForValue().set(KEYWORD_CACHE_KEY, keywordId.toString());
  }
}
