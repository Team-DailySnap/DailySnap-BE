package onepiece.dailysnapbackend.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import onepiece.dailysnapbackend.object.dto.CustomUserDetails;
import onepiece.dailysnapbackend.object.dto.PostFilteredRequest;
import onepiece.dailysnapbackend.object.dto.PostRequest;
import onepiece.dailysnapbackend.object.postgres.Member;
import onepiece.dailysnapbackend.object.postgres.Post;
import onepiece.dailysnapbackend.service.PostService;
import onepiece.dailysnapbackend.util.log.LogMonitoringInvocation;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post")
@Tag(
    name = "사진 게시물 관련 API",
    description = "사진 게시물 관련 API 제공"
)
public class PostController implements PostControllerDocs {

  private final PostService postService;

  @Override
  @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @LogMonitoringInvocation
  public ResponseEntity<UUID> uploadPost(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @Valid @ModelAttribute PostRequest request) {
    Member member = userDetails.getMember();
    return ResponseEntity.ok(postService.uploadPost(request, member));
  }

  @Override
  @GetMapping
  @LogMonitoringInvocation
  public ResponseEntity<Page<Post>> filteredPosts(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @Valid @RequestBody PostFilteredRequest request) {
    return ResponseEntity.ok(postService.getFilteredPosts(request));
  }
}
