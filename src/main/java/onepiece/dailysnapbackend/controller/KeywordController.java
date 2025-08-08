package onepiece.dailysnapbackend.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import onepiece.dailysnapbackend.object.dto.CustomOAuth2User;
import onepiece.dailysnapbackend.object.dto.KeywordFilterRequest;
import onepiece.dailysnapbackend.object.dto.KeywordResponse;
import onepiece.dailysnapbackend.service.keyword.KeywordService;
import onepiece.dailysnapbackend.util.log.LogMonitoringInvocation;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
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
  @GetMapping
  @LogMonitoringInvocation
  public ResponseEntity<Page<KeywordResponse>> filteredKeywords(
      @AuthenticationPrincipal CustomOAuth2User customOAuth2User,
      @ParameterObject KeywordFilterRequest request) {
    return ResponseEntity.ok(keywordService.filteredKeywords(request));
  }
}
