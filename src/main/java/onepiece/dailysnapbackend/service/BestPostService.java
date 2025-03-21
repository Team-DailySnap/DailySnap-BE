package onepiece.dailysnapbackend.service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import onepiece.dailysnapbackend.mapper.EntityMapper;
import onepiece.dailysnapbackend.object.dto.BestPostRequest;
import onepiece.dailysnapbackend.object.dto.BestPostResponse;
import onepiece.dailysnapbackend.object.postgres.Post;
import onepiece.dailysnapbackend.repository.postgres.BestPostRepository;
import onepiece.dailysnapbackend.util.exception.CustomException;
import onepiece.dailysnapbackend.util.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BestPostService {

  private final BestPostRepository bestPostRepository;
  private final RedisTemplate<String, Object> redisTemplate;
  private final EntityMapper entityMapper;

  @Value("${redis.key.daily}")
  private String dailyKey;

  @Value("${redis.key.weekly}")
  private String weeklyKey;

  @Value("${redis.key.monthly}")
  private String monthlyKey;

  // 단일 스케줄러로 모든 작업 처리
  @Scheduled(cron = "0 0 6 * * ?")
  @Transactional
  public void updateBestPosts() {
    LocalDateTime today6am = LocalDateTime.now().withHour(6).withMinute(0).withSecond(0);

    // 1. 일간 베스트 업데이트
    updateDailyBest(today6am);

    // 2. 주간 베스트 업데이트 (월요일에만)
    if (today6am.getDayOfWeek() == DayOfWeek.MONDAY) {
      updateWeeklyBest(today6am);
    }

    // 3. 월간 베스트 업데이트 (매월 1일에만)
    if (today6am.getDayOfMonth() == 1) {
      updateMonthlyBest(today6am);
    }
  }

  private void updateDailyBest(LocalDateTime today6am) {
    LocalDateTime yesterday6am = today6am.minusDays(1);
    List<Post> topDailyPosts = bestPostRepository.findTop30ByCreatedAtBetweenOrderByLikeCountDescDaily(
        yesterday6am, today6am);
    assignRanks(topDailyPosts, "daily");
    String dailyRedisKey = dailyKey + ":" + today6am.toLocalDate();
    redisTemplate.opsForValue().set(dailyRedisKey, topDailyPosts);
    redisTemplate.opsForValue().set(dailyKey, topDailyPosts);
    log.info("일간 인기 게시물 갱신 완료: postCount={}", topDailyPosts.size());
  }

  private void updateWeeklyBest(LocalDateTime today6am) {
    LocalDateTime lastMonday = today6am.minusWeeks(1).with(java.time.temporal.TemporalAdjusters.previousOrSame(
        java.time.DayOfWeek.MONDAY));
    List<Post> weeklyCandidates = new ArrayList<>();

    for (int i = 0; i < 7; i++) {
      LocalDateTime date = lastMonday.plusDays(i);
      String dailyRedisKey = dailyKey + ":" + date.toLocalDate();
      Object dailyPostsObj = redisTemplate.opsForValue().get(dailyRedisKey);
      List<Post> dailyPosts = dailyPostsObj instanceof List ? (List<Post>) dailyPostsObj : null;
      if (dailyPosts != null) {
        weeklyCandidates.addAll(dailyPosts);
      }
    }

    List<Post> topWeeklyPosts = weeklyCandidates.stream()
        .sorted(Comparator.comparing(Post::getLikeCount).reversed())
        .distinct()
        .limit(30)
        .collect(Collectors.toList());

    assignRanks(topWeeklyPosts, "weekly");
    String weeklyRedisKey = weeklyKey + ":" + today6am.toLocalDate();
    redisTemplate.opsForValue().set(weeklyRedisKey, topWeeklyPosts);
    redisTemplate.opsForValue().set(weeklyKey, topWeeklyPosts);
    log.info("주간 인기 게시물 갱신 완료: postCount={}", topWeeklyPosts.size());
  }

  private void updateMonthlyBest(LocalDateTime today6am) {
    LocalDateTime oneMonthAgo = today6am.minusMonths(1);
    List<Post> monthlyCandidates = new ArrayList<>();

    for (int i = 0; i < 4; i++) {
      LocalDateTime monday = today6am.minusWeeks(i).with(java.time.temporal.TemporalAdjusters.previousOrSame(
          java.time.DayOfWeek.MONDAY));
      String weeklyRedisKey = weeklyKey + ":" + monday.toLocalDate();
      Object weeklyPostsObj = redisTemplate.opsForValue().get(weeklyRedisKey);
      List<Post> weeklyPosts = weeklyPostsObj instanceof List ? (List<Post>) weeklyPostsObj : null;
      if (weeklyPosts != null) {
        monthlyCandidates.addAll(weeklyPosts);
      }
    }

    List<Post> topMonthlyPosts = monthlyCandidates.stream()
        .sorted(Comparator.comparing(Post::getLikeCount).reversed())
        .distinct()
        .limit(30)
        .collect(Collectors.toList());

    assignRanks(topMonthlyPosts, "monthly");
    redisTemplate.opsForValue().set(monthlyKey, topMonthlyPosts);
    log.info("월간 인기 게시물 갱신 완료: postCount={}", topMonthlyPosts.size());
  }

  @Transactional(readOnly = true)
  public List<BestPostResponse> getBestPosts(BestPostRequest request) {
    String filter = request.getFilter().name().toLowerCase();
    LocalDateTime startDate = request.getStartDate();
    return getBestPosts(filter, startDate);
  }

  @Transactional(readOnly = true)
  public List<BestPostResponse> getBestPosts(String filter, LocalDateTime startDate) {
    LocalDateTime endDate = LocalDateTime.now().withHour(6).withMinute(0).withSecond(0);
    Object postsObj = redisTemplate.opsForValue().get(getRedisKey(filter));
    List<Post> posts = postsObj instanceof List ? (List<Post>) postsObj : null;

    if (posts == null) {
      switch (filter.toLowerCase()) {
        case "daily":
          posts = bestPostRepository.findTop30ByCreatedAtBetweenOrderByLikeCountDescDaily(startDate, endDate);
          assignRanks(posts, "daily");
          break;
        case "weekly":
          posts = calculateWeeklyBestFromDaily(startDate, endDate);
          assignRanks(posts, "weekly");
          break;
        case "monthly":
          posts = calculateMonthlyBestFromWeekly(startDate, endDate);
          assignRanks(posts, "monthly");
          break;
        default:
          throw new CustomException(ErrorCode.INVALID_FILTER);
      }
      redisTemplate.opsForValue().set(getRedisKey(filter), posts);
    }

    return posts.stream()
        .map(entityMapper::toBestPostResponse)
        .collect(Collectors.toList());
  }

  private List<Post> calculateWeeklyBestFromDaily(LocalDateTime startDate, LocalDateTime endDate) {
    List<Post> weeklyCandidates = new ArrayList<>();
    LocalDateTime adjustedStart = startDate.with(java.time.temporal.TemporalAdjusters.previousOrSame(
        java.time.DayOfWeek.MONDAY));
    for (int i = 0; i < 7 && adjustedStart.plusDays(i).isBefore(endDate); i++) {
      String dailyRedisKey = dailyKey + ":" + adjustedStart.plusDays(i).toLocalDate();
      Object dailyPostsObj = redisTemplate.opsForValue().get(dailyRedisKey);
      List<Post> dailyPosts = dailyPostsObj instanceof List ? (List<Post>) dailyPostsObj : null;
      if (dailyPosts != null) {
        weeklyCandidates.addAll(dailyPosts);
      }
    }
    return weeklyCandidates.stream()
        .sorted(Comparator.comparing(Post::getLikeCount).reversed())
        .distinct()
        .limit(30)
        .collect(Collectors.toList());
  }

  private List<Post> calculateMonthlyBestFromWeekly(LocalDateTime startDate, LocalDateTime endDate) {
    List<Post> monthlyCandidates = new ArrayList<>();
    LocalDateTime currentMonday = startDate.with(java.time.temporal.TemporalAdjusters.previousOrSame(
        java.time.DayOfWeek.MONDAY));
    while (currentMonday.isBefore(endDate)) {
      String weeklyRedisKey = weeklyKey + ":" + currentMonday.toLocalDate();
      Object weeklyPostsObj = redisTemplate.opsForValue().get(weeklyRedisKey);
      List<Post> weeklyPosts = weeklyPostsObj instanceof List ? (List<Post>) weeklyPostsObj : null;
      if (weeklyPosts != null) {
        monthlyCandidates.addAll(weeklyPosts);
      }
      currentMonday = currentMonday.plusWeeks(1);
    }
    return monthlyCandidates.stream()
        .sorted(Comparator.comparing(Post::getLikeCount).reversed())
        .distinct()
        .limit(30)
        .collect(Collectors.toList());
  }

  private String getRedisKey(String filter) {
    switch (filter.toLowerCase()) {
      case "daily":
        return dailyKey;
      case "weekly":
        return weeklyKey;
      case "monthly":
        return monthlyKey;
      default:
        throw new CustomException(ErrorCode.INVALID_FILTER);
    }
  }

  private void assignRanks(List<Post> posts, String rankType) {
    for (int i = 0; i < posts.size(); i++) {
      Post post = posts.get(i);
      Post updatedPost = Post.builder()
          .postId(post.getPostId())
          .member(post.getMember())
          .keyword(post.getKeyword())
          .dailyBest(post.getDailyBest())
          .weeklyBest(post.getWeeklyBest())
          .monthlyBest(post.getMonthlyBest())
          .dailyRank("daily".equals(rankType) ? i + 1 : post.getDailyRank())
          .weeklyRank("weekly".equals(rankType) ? i + 1 : post.getWeeklyRank())
          .monthlyRank("monthly".equals(rankType) ? i + 1 : post.getMonthlyRank())
          .content(post.getContent())
          .likeCount(post.getLikeCount())
          .location(post.getLocation())
          .build();
      bestPostRepository.save(updatedPost);
    }
  }
}
