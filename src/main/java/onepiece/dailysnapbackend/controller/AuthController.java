package onepiece.dailysnapbackend.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import onepiece.dailysnapbackend.object.constants.KeywordCategory;
import onepiece.dailysnapbackend.object.dto.CustomUserDetails;
import onepiece.dailysnapbackend.object.dto.KeywordRequest;
import onepiece.dailysnapbackend.object.dto.SignInRequest;
import onepiece.dailysnapbackend.object.dto.SignUpRequest;
import onepiece.dailysnapbackend.service.MemberService;
import onepiece.dailysnapbackend.service.keyword.AdminKeywordService;
import onepiece.dailysnapbackend.service.keyword.KeywordSelectionService;
import onepiece.dailysnapbackend.util.log.LogMonitoringInvocation;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(
    name = "인증 및 관리자 키워드 API",
    description = "회원 인증 및 관리자 키워드 관련 API 제공"
)
public class AuthController implements AuthControllerDocs{

  private final MemberService memberService;
  private final KeywordSelectionService keywordSelectionService;
  private final AdminKeywordService adminKeywordService;

  // ===========================
  // 인증 관련 API
  // ===========================

  // 회원가입
  @Override
  @PostMapping("/api/auth/sign-up")
  @LogMonitoringInvocation
  public ResponseEntity<Void> signUp(@Valid @RequestBody SignUpRequest request) {
    memberService.signUp(request);
    return ResponseEntity.ok().build();
  }

  // 로그인
  @Override
  @PostMapping(value = "/login", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @LogMonitoringInvocation
  public ResponseEntity<Void> signIn(SignInRequest request) {
    return ResponseEntity.ok().build();
  }

  // 액세스 토큰 재발급
  @Override
  @PostMapping("/api/auth/reissue")
  @LogMonitoringInvocation
  public ResponseEntity<Void> reissue(HttpServletRequest request, HttpServletResponse response) {
    memberService.reissue(request, response);
    return ResponseEntity.ok().build();
  }

  // ===========================
  // 관리자 키워드 API
  // ===========================
  /**
   * 특정 카테고리의 키워드가 부족할 경우, OpenAI API를 사용하여 자동 생성
   */
  @Override
  @PostMapping("/admin/keyword/generate")
  @LogMonitoringInvocation
  public ResponseEntity<Void> generateKeywords(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @RequestParam KeywordCategory category) {
    adminKeywordService.generateKeywords(category);
    return ResponseEntity.ok().build();
  }

  /**
   * 특정 날짜에 제공할 키워드 추가 (관리자 지정)
   */
  @Override
  @PostMapping("/admin/keyword")
  @LogMonitoringInvocation
  public ResponseEntity<Void> addKeyword(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @Valid @RequestBody KeywordRequest request) {
    adminKeywordService.addAdminKeyword(request);
    return ResponseEntity.ok().build();
  }

  /**
   * 특정 키워드 삭제
   */
  @Override
  @DeleteMapping("/admin/keyword/{keywordId}")
  @LogMonitoringInvocation
  public ResponseEntity<Void> deleteKeyword(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @PathVariable UUID keywordId) {
    adminKeywordService.deleteKeyword(keywordId);
    return ResponseEntity.ok().build();
  }
}
