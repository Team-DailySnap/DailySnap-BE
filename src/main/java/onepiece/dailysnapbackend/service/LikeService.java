package onepiece.dailysnapbackend.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import onepiece.dailysnapbackend.object.dto.PostDetailRequest;
import onepiece.dailysnapbackend.object.mongo.LikeHistory;
import onepiece.dailysnapbackend.object.postgres.Member;
import onepiece.dailysnapbackend.object.postgres.Post;
import onepiece.dailysnapbackend.repository.mongo.LikeHistoryRepository;
import onepiece.dailysnapbackend.repository.postgres.ImageRepository;
import onepiece.dailysnapbackend.repository.postgres.PostRepository;
import onepiece.dailysnapbackend.util.exception.CustomException;
import onepiece.dailysnapbackend.util.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeService {

  private final PostRepository postRepository;
  private final LikeHistoryRepository likeHistoryRepository;
  private final ImageRepository imageRepository;
  private final RedisLockService redisLockService;

  @Transactional
  public int increaseLikes(PostDetailRequest request, Member member) {
    UUID postId = request.getPostId();
    String lockKey = "like_lock:" + postId + ":" + member.getMemberId();

    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

    boolean alreadyLiked = likeHistoryRepository.existsByPostIdAndMemberId(post.getPostId(), member.getMemberId());
    if (alreadyLiked) {
      log.warn("이미 좋아요를 누른 사용자: postId={}, memberId={}", post.getPostId(), member.getMemberId());
      throw new CustomException(ErrorCode.ALREADY_LIKED);
    }

    return redisLockService.executeWithLock(lockKey, () -> {
      post.setLikeCount(post.getLikeCount() + 1);
      postRepository.save(post);

      try {
        LikeHistory likeHistory = LikeHistory.builder()
            .postId(post.getPostId())
            .memberId(member.getMemberId())
            .build();
        likeHistoryRepository.save(likeHistory);
      } catch (Exception e) {
        post.setLikeCount(post.getLikeCount() - 1);
        postRepository.save(post);
        log.error("좋아요 기록 저장 실패: postId={}, memberId={}", post.getPostId(), member.getMemberId(), e);
        throw new CustomException(ErrorCode.LIKE_HISTORY_SAVE_FAILED);
      }

      log.info("좋아요 수 증가: postId={}, likeCount={}", post.getPostId(), post.getLikeCount());
      return post.getLikeCount();
    });
  }
}