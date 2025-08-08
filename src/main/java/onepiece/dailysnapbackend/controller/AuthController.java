package onepiece.dailysnapbackend.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import onepiece.dailysnapbackend.object.dto.LoginResponse;
import onepiece.dailysnapbackend.object.dto.LoginRequest;
import onepiece.dailysnapbackend.service.MemberService;
import onepiece.dailysnapbackend.util.log.LogMonitoringInvocation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(
    name = "회원 API",
    description = "회원 API 제공"
)
@RequestMapping("/api/auth")
public class AuthController implements AuthControllerDocs {

  private final MemberService memberService;

  // 로그인
  @Override
  @PostMapping(value = "/login")
  @LogMonitoringInvocation
  public ResponseEntity<LoginResponse> login(LoginRequest request) {
    return ResponseEntity.ok(memberService.socialSignIn(request));
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
