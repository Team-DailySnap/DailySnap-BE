package onepiece.dailysnapbackend.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import onepiece.dailysnapbackend.object.dto.CustomOAuth2User;
import onepiece.dailysnapbackend.object.dto.PostDetailRequest;
import onepiece.dailysnapbackend.object.postgres.Member;
import onepiece.dailysnapbackend.service.LikeService;
import onepiece.dailysnapbackend.util.log.LogMonitoringInvocation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/like")
@Tag(
    name = "게시물 좋아요 API",
    description = "게시물 좋아요 관련 API 제공"
)
public class LikeController implements LikeControllerDocs {

  private final LikeService likeService;

  @Override
  @PostMapping
  @LogMonitoringInvocation
  public ResponseEntity<Integer> postLike(
      @AuthenticationPrincipal CustomOAuth2User userDetails,
      @RequestBody PostDetailRequest request) {
    Member member = userDetails.getMember();
    return ResponseEntity.ok(likeService.increaseLikes(request, member));
  }
}
