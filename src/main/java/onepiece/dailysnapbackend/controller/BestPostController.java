package onepiece.dailysnapbackend.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import onepiece.dailysnapbackend.object.dto.BestPostRequest;
import onepiece.dailysnapbackend.object.dto.BestPostResponse;
import onepiece.dailysnapbackend.object.dto.CustomUserDetails;
import onepiece.dailysnapbackend.service.BestPostService;
import onepiece.dailysnapbackend.util.log.LogMonitoringInvocation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/best-post")
@RequiredArgsConstructor
@Slf4j
@Tag(
    name = "인기 게시물 API",
    description = "일간, 주간, 월간 인기 게시물 API 제공"
)
public class BestPostController implements BestPostControllerDocs {

  private final BestPostService bestPostService;

  @Override
  @GetMapping
  @LogMonitoringInvocation
  public ResponseEntity<List<BestPostResponse>> getBestPosts(
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      @Valid @ModelAttribute BestPostRequest request) {
    return ResponseEntity.ok(bestPostService.getBestPosts(request));
  }
}
