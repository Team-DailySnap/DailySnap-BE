package onepiece.dailysnapbackend.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import onepiece.dailysnapbackend.object.dto.SignInRequest;
import onepiece.dailysnapbackend.object.dto.SignUpRequest;
import onepiece.dailysnapbackend.service.MemberService;
import onepiece.dailysnapbackend.util.log.LogMonitoringInvocation;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(
    name = "인증 관련 API",
    description = "회원 인증 관련 API 제공"
)
public class AuthController implements AuthControllerDocs{

  private final MemberService memberService;

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
}
