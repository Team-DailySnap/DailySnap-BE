package onepiece.dailysnapbackend.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import onepiece.dailysnapbackend.object.dto.CustomUserDetails;
import onepiece.dailysnapbackend.object.dto.FollowRequest;
import onepiece.dailysnapbackend.object.dto.MemberResponse;
import onepiece.dailysnapbackend.object.postgres.Member;
import onepiece.dailysnapbackend.service.FollowService;
import onepiece.dailysnapbackend.util.log.LogMonitoringInvocation;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
@Tag(
    name = "팔로우 API",
    description = "사용자 팔로우 관련 API 제공"
)
public class FollowController implements FollowControllerDocs{

  private final FollowService followService;

  @Override
  @PostMapping("/follow")
  @LogMonitoringInvocation
  public ResponseEntity<Void> followMember(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @RequestParam UUID followeeId) {
    Member member = userDetails.getMember();
    followService.followMember(member, followeeId);
    return ResponseEntity.ok().build();
  }

  @Override
  @DeleteMapping("/follow")
  @LogMonitoringInvocation
  public ResponseEntity<Void> unfollowMember(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @RequestParam UUID followeeId) {
    Member member = userDetails.getMember();
    followService.unfollowMember(member, followeeId);
    return ResponseEntity.ok().build();
  }

  @Override
  @GetMapping("/followers")
  @LogMonitoringInvocation
  public Page<MemberResponse> getFollowers(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @Valid @ModelAttribute FollowRequest request) {
    Member member = userDetails.getMember();
    return followService.getFollowerList(member, request);
  }

  @Override
  @GetMapping("/followings")
  @LogMonitoringInvocation
  public Page<MemberResponse> getFollowings(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @Valid @ModelAttribute FollowRequest request) {
    Member member = userDetails.getMember();
    return followService.getFollowingList(member, request);
  }
}
