package onepiece.dailysnapbackend.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import onepiece.dailysnapbackend.object.dto.CustomUserDetails;
import onepiece.dailysnapbackend.object.postgres.Member;
import onepiece.dailysnapbackend.service.FollowService;
import onepiece.dailysnapbackend.util.log.LogMonitoringInvocation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
@Tag(
    name = "사용자 팔로우 API",
    description = "사용자 팔로우 관련 API 제공"
)
public class FollowController {

  private final FollowService followService;

  @PostMapping("/follow")
  @LogMonitoringInvocation
  public ResponseEntity<Void> followMember(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @RequestParam UUID followeeId) {
    Member member = userDetails.getMember();
    followService.followMember(member, followeeId);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/unfollow")
  @LogMonitoringInvocation
  public ResponseEntity<Void> unfollowMember(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @RequestParam UUID followeeId) {
    Member member = userDetails.getMember();
    followService.unfollowMember(member, followeeId);
    return ResponseEntity.ok().build();
  }
}
