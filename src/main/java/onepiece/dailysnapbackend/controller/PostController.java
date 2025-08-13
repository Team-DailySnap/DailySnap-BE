package onepiece.dailysnapbackend.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import onepiece.dailysnapbackend.object.dto.CustomOAuth2User;
import onepiece.dailysnapbackend.object.dto.PostFilteredRequest;
import onepiece.dailysnapbackend.object.dto.PostRequest;
import onepiece.dailysnapbackend.object.dto.PostResponse;
import onepiece.dailysnapbackend.service.PostService;
import onepiece.dailysnapbackend.util.log.LogMonitoringInvocation;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
  @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @LogMonitoringInvocation
  public ResponseEntity<Void> uploadPost(
      @AuthenticationPrincipal CustomOAuth2User customOAuth2User,
      @Valid @ModelAttribute PostRequest request) {
    postService.uploadPost(customOAuth2User.getMember(), request);
    return ResponseEntity.ok().build();
  }

  @Override
  @GetMapping("/{post-id}")
  @LogMonitoringInvocation
  public ResponseEntity<PostResponse> getPost(
      @AuthenticationPrincipal CustomOAuth2User userDetails,
      @PathVariable(name = "post-id") UUID postId) {
    return ResponseEntity.ok(postService.getPost(userDetails.getMember(), postId));
  }

  @GetMapping("")
  @LogMonitoringInvocation
  public ResponseEntity<Page<PostResponse>> filteredPost(
      @ParameterObject PostFilteredRequest request
  ) {
    return ResponseEntity.ok(postService.filteredPost(request));
  }

  @Override
  @GetMapping("/home")
  @LogMonitoringInvocation
  public ResponseEntity<List<PostResponse>> get7DaysRandomPost() {
    return ResponseEntity.ok(postService.get7DaysRandomPost());
  }
}
