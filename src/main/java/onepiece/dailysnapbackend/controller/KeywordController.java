package onepiece.dailysnapbackend.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import onepiece.dailysnapbackend.object.dto.CustomOAuth2User;
import onepiece.dailysnapbackend.object.dto.DailyKeywordResponse;
import onepiece.dailysnapbackend.object.dto.KeywordFilterRequest;
import onepiece.dailysnapbackend.object.dto.KeywordFilterResponse;
import onepiece.dailysnapbackend.service.keyword.KeywordService;
import onepiece.dailysnapbackend.util.log.LogMonitoringInvocation;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/keyword")
@Slf4j
@Tag(
    name = "키워드 관리 API",
    description = "키워드 자동 생성 및 관리 API 제공"
)
public class KeywordController implements KeywordControllerDocs {

  private final KeywordService keywordService;

  /**
   * 필터링 조건(키워드 텍스트, 카테고리, 날짜)을 사용하여 키워드 목록을 Page로 반환.
   */
  @Override
  @PostMapping
  @LogMonitoringInvocation
  public ResponseEntity<Page<KeywordFilterResponse>> filteredKeywords(
      @AuthenticationPrincipal CustomOAuth2User userDetails,
      @Valid @RequestBody KeywordFilterRequest request) {
    return ResponseEntity.ok(keywordService.filteredKeywords (request));
  }

  @Override
  @GetMapping("/daily")
  @LogMonitoringInvocation
  public ResponseEntity<DailyKeywordResponse> getDailyKeyword() {
    return ResponseEntity.ok(keywordService.getDailyKeyword());
  }
}
