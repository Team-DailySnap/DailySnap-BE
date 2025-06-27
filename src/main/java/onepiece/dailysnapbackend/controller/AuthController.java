package onepiece.dailysnapbackend.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import onepiece.dailysnapbackend.object.dto.SignInRequest;
import onepiece.dailysnapbackend.service.MemberService;
import onepiece.dailysnapbackend.util.log.LogMonitoringInvocation;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(
    name = "인증 API",
    description = "회원 인증 API 제공"
)
public class AuthController implements AuthControllerDocs{

  private final MemberService memberService;

  // ===========================
  // 인증 관련 API
  // ===========================

  // 로그인
  @Override
  @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
  @LogMonitoringInvocation
  public ResponseEntity<Void> signIn(SignInRequest request, HttpServletResponse response) {
    memberService.socialSignIn(request, response);
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
}
