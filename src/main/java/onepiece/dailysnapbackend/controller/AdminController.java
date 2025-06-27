package onepiece.dailysnapbackend.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import onepiece.dailysnapbackend.object.dto.CustomOAuth2User;
import onepiece.dailysnapbackend.object.dto.KeywordRequest;
import onepiece.dailysnapbackend.service.keyword.AdminKeywordService;
import onepiece.dailysnapbackend.util.log.LogMonitoringInvocation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/keyword")
@Tag(name = "관리자 키워드 API", description = "관리자가 키워드를 관리하는 API")
public class AdminController implements AdminControllerDocs {

  private final AdminKeywordService adminKeywordService;

  /**
   * 특정 날짜에 제공할 키워드 추가 (관리자 지정)
   */
  @Override
  @PostMapping
  @LogMonitoringInvocation
  public ResponseEntity<Void> addKeyword(
      @AuthenticationPrincipal CustomOAuth2User userDetails,
      @Valid @RequestBody KeywordRequest request) {
    adminKeywordService.addKeyword(request);
    log.info("[AdminController] 키워드 추가 완료");
    return ResponseEntity.ok().build();
  }

  /**
   * 특정 키워드 삭제
   */
  @Override
  @DeleteMapping("/{keyword}")
  @LogMonitoringInvocation
  public ResponseEntity<Void> deleteKeyword(
      @AuthenticationPrincipal CustomOAuth2User userDetails,
      @PathVariable String keyword) {
    adminKeywordService.deleteKeyword(keyword);
    return ResponseEntity.ok().build();
  }
}
