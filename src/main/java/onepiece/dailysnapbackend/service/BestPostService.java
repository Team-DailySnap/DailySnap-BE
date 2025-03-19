package onepiece.dailysnapbackend.service;

import static onepiece.dailysnapbackend.util.exception.ErrorCode.INVALID_FILTER;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import onepiece.dailysnapbackend.object.postgres.Post;
import onepiece.dailysnapbackend.repository.postgres.BestPostRepository;
import onepiece.dailysnapbackend.util.exception.CustomException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BestPostService {

  private final BestPostRepository bestPostRepository;
  private final RedisTemplate<String, List<Post>> redisTemplate;

  @Value("${redis.key.daily}")
  private String dailyKey;

  @Value("${redis.key.weekly}")
  private String weeklyKey;

  @Value("${redis.key.monthly}")
  private String monthlyKey;

  // 매일 아침 6시에 일간 인기 사진 갱신 (상위 30개)
  @Transactional
  @Scheduled(cron = "0 0 6 * * ?")
  public void updateDailyBest() {
    List<Post> topDailyPosts = bestPostRepository.findTop30ByOrderByLikeCountDesc();
    redisTemplate.opsForValue().set(dailyKey, topDailyPosts);
    log.info("일간 인기 게시물 갱신 완료: postCount={}", topDailyPosts.size());
  }

  // 매주 월요일 아침 6시에 주간 인기 사진 갱신 (상위 30개)
  @Scheduled(cron = "0 0 6 * * MON")
  @Transactional
  public void updateWeeklyBest() {
    LocalDate sevenDaysAgo = LocalDate.now().minusDays(7);
    List<Post> topWeeklyPosts = bestPostRepository.findByDailyBestCreatedDateGreaterThanEqualOrderByLikeCountDesc(sevenDaysAgo);
    redisTemplate.opsForValue().set(weeklyKey, topWeeklyPosts);
    log.info("주간 인기 게시물 갱신 완료: postCount={}", topWeeklyPosts.size());
  }

  // 매월 1일 아침 6시에 월간 인기 사진 갱신 (상위 30개)
  @Scheduled(cron = "0 0 6 1 * ?")
  @Transactional
  public void updateMonthlyBest() {
    LocalDate fourWeeksAgo = LocalDate.now().minusWeeks(4);
    List<Post> topMonthlyPosts = bestPostRepository.findByWeeklyBestWeekStartDateGreaterThanEqualOrderByLikeCountDesc(fourWeeksAgo);
    redisTemplate.opsForValue().set(monthlyKey, topMonthlyPosts);
    log.info("월간 인기 게시물 갱신 완료: postCount={}", topMonthlyPosts.size());
  }

  // 인기 게시물 조회
  @Transactional(readOnly = true)
  public List<Post> getBestPosts(String filter, LocalDate startDate) {
    ValueOperations<String, List<Post>> ops = redisTemplate.opsForValue();
    List<Post> posts;

    switch (filter) {
      case "daily":
        posts = ops.get(dailyKey);
        if (posts == null) {
          posts = bestPostRepository.findTop30ByOrderByLikeCountDesc();
          redisTemplate.opsForValue().set(dailyKey, posts);
        }
        return posts;
      case "weekly":
        posts = ops.get(weeklyKey);
        if (posts == null) {
          posts = bestPostRepository.findByDailyBestCreatedDateGreaterThanEqualOrderByLikeCountDesc(startDate.minusDays(7));
          redisTemplate.opsForValue().set(weeklyKey, posts);
        }
        return posts;
      case "monthly":
        posts = ops.get(monthlyKey);
        if (posts == null) {
          posts = bestPostRepository.findByWeeklyBestWeekStartDateGreaterThanEqualOrderByLikeCountDesc(startDate.minusWeeks(4));
          redisTemplate.opsForValue().set(monthlyKey, posts);
        }
        return posts;
      default:
        log.error("잘못된 필터 값: {}", filter);
        throw new CustomException(INVALID_FILTER);
    }
  }
}
