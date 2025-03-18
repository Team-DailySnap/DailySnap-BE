package onepiece.dailysnapbackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import onepiece.dailysnapbackend.object.postgres.Follow;
import onepiece.dailysnapbackend.object.postgres.Member;
import onepiece.dailysnapbackend.repository.postgres.FollowRepository;
import onepiece.dailysnapbackend.repository.postgres.MemberRepository;
import onepiece.dailysnapbackend.util.exception.CustomException;
import onepiece.dailysnapbackend.util.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FollowService {

  private final FollowRepository followRepository;
  private final MemberRepository memberRepository;

  // 사용자 팔로우
  @Transactional
  public void followMember(Member follower, Member followee) {
    if (follower == null || followee == null) {
      log.error("팔로우 요청에 null 값이 포함되었습니다. follower={}, followee={}", follower, followee);
      throw new CustomException(ErrorCode.INVALID_REQUEST);
    }
    if (!memberRepository.existsById(follower.getMemberId())) {
      log.error("존재하지 않는 사용자입니다. followerId={}", follower.getMemberId());
      throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);
    }
    if (!memberRepository.existsById(followee.getMemberId())) {
      log.error("존재하지 않는 사용자입니다. followeeId={}", followee.getMemberId());
      throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);
    }
    if (follower.equals(followee)) {
      log.error("자기 자신을 팔로우할 수 없습니다. member={}", follower);
      throw new CustomException(ErrorCode.INVALID_REQUEST);
    }
    if (followRepository.existsByFollowerAndFollowee(follower, followee)) {
      log.error("이미 팔로우한 사용자입니다. follower={}, followee={}", follower, followee);
      throw new CustomException(ErrorCode.ALREADY_FOLLOWED);
    }

    Follow follow = Follow.builder()
        .follower(follower)
        .followee(followee).build();
    followRepository.save(follow);
    log.info("팔로우 성공: followerId={}, followeeId={}", follower.getMemberId(), followee.getMemberId());
  }

  // 사용자 언팔로우
  @Transactional
  public void unfollowMember(Member follower, Member followee) {
    if (follower == null || followee == null) {
      log.error("언팔로우 요청에 null 값이 포함되었습니다. follower={}, followee={}", follower, followee);
      throw new CustomException(ErrorCode.INVALID_REQUEST);
    }
    if (!memberRepository.existsById(follower.getMemberId())) {
      log.error("존재하지 않는 사용자입니다. followerId={}", follower.getMemberId());
      throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);
    }
    if (!memberRepository.existsById(followee.getMemberId())) {
      log.error("존재하지 않는 사용자입니다. followeeId={}", followee.getMemberId());
      throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);
    }
    Follow follow = followRepository.findByFollowerAndFollowee(follower, followee)
        .orElseThrow(() -> {
          log.error("팔로우 관계가 존재하지 않습니다. follower={}, followee={}", follower, followee);
          return new CustomException(ErrorCode.FOLLOW_RELATIONSHIP_NOT_FOUND);
        });

    followRepository.delete(follow);
    log.info("언팔로우 성공: followerId={}, followeeId={}", follower.getMemberId(), followee.getMemberId());
  }
}
