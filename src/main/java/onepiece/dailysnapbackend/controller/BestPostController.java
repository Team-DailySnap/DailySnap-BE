package onepiece.dailysnapbackend.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import onepiece.dailysnapbackend.object.dto.CustomUserDetails;
import onepiece.dailysnapbackend.object.postgres.Post;
import onepiece.dailysnapbackend.service.BestPostService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/best-post")
@RequiredArgsConstructor
@Slf4j
@Tag(
    name = "인기 게시물 API",
    description = "일간, 주간, 월간 인기 게시물 API 제공"
)
public class BestPostController implements BestPostControllerDocs{
  private final BestPostService bestPostService;

  @Override
  @GetMapping
  public ResponseEntity<List<Post>> getBestPosts(
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      @Valid @RequestParam String filter,
      @Valid @RequestParam LocalDate startDate) {

    List<Post> bestPosts = bestPostService.getBestPosts(filter, startDate);
    return ResponseEntity.ok(bestPosts);
  }
}
