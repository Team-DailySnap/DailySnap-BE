package onepiece.dailysnapbackend.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import onepiece.dailysnapbackend.object.constants.KeywordCategory;
import onepiece.dailysnapbackend.object.dto.CustomOAuth2User;
import onepiece.dailysnapbackend.object.dto.KeywordRequest;
import onepiece.dailysnapbackend.object.dto.KeywordResponse;
import onepiece.dailysnapbackend.service.keyword.AdminKeywordService;
import onepiece.dailysnapbackend.service.keyword.OpenAIKeywordService;
import onepiece.dailysnapbackend.util.log.LogMonitoringInvocation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/keyword")
@Tag(
    name = "관리자 키워드 API",
    description = "관리자가 키워드를 관리하는 API"
)
public class AdminKeywordController implements AdminKeywordControllerDocs {

  private final AdminKeywordService adminKeywordService;
  private final OpenAIKeywordService openAIKeywordService;

  /**
   * 특정 날짜에 제공할 키워드 추가 (관리자 지정)
   */
  @Override
  @PostMapping
  @LogMonitoringInvocation
  public ResponseEntity<KeywordResponse> addKeyword(
      @AuthenticationPrincipal CustomOAuth2User customOAuth2User,
      @Valid @RequestBody KeywordRequest request) {
    return ResponseEntity.ok(adminKeywordService.addKeyword(request));
  }

  /**
   * 특정 키워드 삭제
   */
  @Override
  @DeleteMapping("/{keyword-id}")
  @LogMonitoringInvocation
  public ResponseEntity<Void> deleteKeyword(
      @AuthenticationPrincipal CustomOAuth2User customOAuth2User,
      @PathVariable(value = "keyword-id") UUID keywordId) {
    adminKeywordService.deleteKeyword(keywordId);
    return ResponseEntity.ok().build();
  }

  @Override
  @PostMapping("/list")
  public ResponseEntity<Void> createKeywordList(
      @AuthenticationPrincipal CustomOAuth2User customOAuth2User,
      KeywordCategory category) {
    openAIKeywordService.generateKeywords(category);
    return ResponseEntity.ok().build();
  }
}
