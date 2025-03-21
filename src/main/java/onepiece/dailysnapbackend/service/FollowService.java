package onepiece.dailysnapbackend.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import onepiece.dailysnapbackend.mapper.EntityMapper;
import onepiece.dailysnapbackend.object.dto.MemberResponse;
import onepiece.dailysnapbackend.object.postgres.Follow;
import onepiece.dailysnapbackend.object.postgres.Member;
import onepiece.dailysnapbackend.repository.postgres.FollowRepository;
import onepiece.dailysnapbackend.repository.postgres.MemberRepository;
import onepiece.dailysnapbackend.util.exception.CustomException;
import onepiece.dailysnapbackend.util.exception.ErrorCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FollowService {

  private final FollowRepository followRepository;
  private final MemberRepository memberRepository;
  private final EntityMapper entityMapper;

  // 사용자 팔로우
  @Transactional
  public void followMember(Member follower, UUID followeeId) {
    Member followee = memberRepository.findById(followeeId)
            .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

    validateFollowRequest(follower, followee);

    Follow follow = Follow.builder()
        .follower(follower)
        .followee(followee)
        .build();
    followRepository.save(follow);
    log.info("팔로우 성공: followerId={}, followeeId={}", follower.getMemberId(), followee.getMemberId());
  }

  // 사용자 언팔로우
  @Transactional
  public void unfollowMember(Member follower, UUID followeeId) {
    Member followee = memberRepository.findById(followeeId)
        .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

    validateFollowRequest(follower, followee);

    Follow follow = followRepository.findByFollowerAndFollowee(follower, followee)
        .orElseThrow(() -> {
          log.error("팔로우 관계가 존재하지 않습니다. follower={}, followee={}", follower, followee);
          return new CustomException(ErrorCode.FOLLOW_RELATIONSHIP_NOT_FOUND);
        });

    followRepository.delete(follow);
    log.info("언팔로우 성공: followerId={}, followeeId={}", follower.getMemberId(), followee.getMemberId());
  }

  @Transactional(readOnly = true)
  public Page<MemberResponse> getFollowerList(Member member, Pageable pageable) {
    Page<Follow> followersPage = followRepository.findByFollowee(member, pageable);
    return followersPage.map(follow -> entityMapper.toMemberResponse(follow.getFollower()));
  }

  // 팔로우 요청 검증
  private void validateFollowRequest(Member follower, Member followee) {
    if (follower == null || followee == null) {
      log.error("요청에 null 값이 포함되었습니다. follower={}, followee={}", follower, followee);
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
      log.error("자기 자신을 팔로우/언팔로우할 수 없습니다. member={}", follower);
      throw new CustomException(ErrorCode.INVALID_REQUEST);
    }
  }
}
