package onepiece.dailysnapbackend.controller.keyword;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import onepiece.dailysnapbackend.object.constants.KeywordCategory;
import onepiece.dailysnapbackend.object.dto.CustomUserDetails;
import onepiece.dailysnapbackend.object.dto.KeywordRequest;
import onepiece.dailysnapbackend.service.KeywordSelectionService;
import onepiece.dailysnapbackend.service.KeywordService;
import onepiece.dailysnapbackend.util.log.LogMonitoringInvocation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/keywords")  // 🔹 기본 URL
@Tag(
    name = "관리자 키워드 API",
    description = "관리자가 키워드를 관리하는 API"
)
public class AdminKeywordController implements AdminKeywordControllerDocs{

  private final KeywordService keywordService;
  private final KeywordSelectionService keywordSelectionService;

  /**
   * 🔹 특정 카테고리의 키워드가 부족할 경우, OpenAI API를 사용하여 자동 생성
   */
  @PostMapping("/generate")
  @LogMonitoringInvocation
  public ResponseEntity<Void> generateKeywords(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @RequestParam KeywordCategory category) {
    keywordService.generateKeywords(category);
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
    keywordService.addAdminKeyword(request);
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
    keywordService.deleteKeyword(id);
    return ResponseEntity.ok().build();
  }
}
