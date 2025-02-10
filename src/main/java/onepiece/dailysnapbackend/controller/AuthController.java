package onepiece.dailysnapbackend.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import onepiece.dailysnapbackend.object.dto.SignUpRequest;
import onepiece.dailysnapbackend.service.MemberService;
import onepiece.dailysnapbackend.util.log.LogMonitoringInvocation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(
    name = "인증 관련 API",
    description = "회원 인증 관련 API 제공"
)
public class AuthController implements AuthControllerDocs{

  private final MemberService memberService;

  // 회원가입
  @Override
  @PostMapping("/sign-up")
  @LogMonitoringInvocation
  public ResponseEntity<Void> signUp(@Valid @RequestBody SignUpRequest request) {
    memberService.signUp(request);
    return ResponseEntity.ok().build();
  }
}
