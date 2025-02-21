package onepiece.dailysnapbackend.controller.keyword;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import onepiece.dailysnapbackend.object.constants.KeywordCategory;
import onepiece.dailysnapbackend.object.dto.CustomUserDetails;
import onepiece.dailysnapbackend.object.dto.KeywordRequest;
import onepiece.dailysnapbackend.service.keyword.AdminKeywordService;
import onepiece.dailysnapbackend.service.keyword.KeywordSelectionService;
import onepiece.dailysnapbackend.util.log.LogMonitoringInvocation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/keywords")  // 🔹 기본 URL
@Tag(
    name = "관리자 키워드 API",
    description = "관리자가 키워드를 관리하는 API"
)
public class AdminKeywordController implements AdminKeywordControllerDocs{

  private final KeywordSelectionService keywordSelectionService;
  private final AdminKeywordService adminKeywordService;

  /**
   * 🔹 특정 카테고리의 키워드가 부족할 경우, OpenAI API를 사용하여 자동 생성
   */
  @PostMapping("/generate")
  @LogMonitoringInvocation
  public ResponseEntity<Void> generateKeywords(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @RequestParam KeywordCategory category) {
    adminKeywordService.generateKeywords(category);
    return ResponseEntity.ok().build();
  }

  /**
   * 🔹 특정 날짜에 제공할 키워드 추가
   */
  @PostMapping
  @LogMonitoringInvocation
  public ResponseEntity<Void> addAdminKeyword(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @Valid @RequestBody KeywordRequest request) {
    adminKeywordService.addAdminKeyword(request);
    return ResponseEntity.ok().build();
  }

  /**
   * 🔹 특정 키워드 삭제
   */
  @DeleteMapping("/{id}")
  @LogMonitoringInvocation
  public ResponseEntity<Void> deleteKeyword(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @PathVariable UUID id) {
    adminKeywordService.deleteKeyword(id);
    return ResponseEntity.ok().build();
  }
}
