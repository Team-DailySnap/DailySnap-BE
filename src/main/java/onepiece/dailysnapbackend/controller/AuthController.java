package onepiece.dailysnapbackend.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import onepiece.dailysnapbackend.object.dto.LoginRequest;
import onepiece.dailysnapbackend.object.dto.LoginResponse;
import onepiece.dailysnapbackend.object.dto.ReissueRequest;
import onepiece.dailysnapbackend.service.MemberService;
import onepiece.dailysnapbackend.util.log.LogMonitoringInvocation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
  public ResponseEntity<LoginResponse> login(
      @Valid @RequestBody LoginRequest request
  ) {
    return ResponseEntity.ok(memberService.socialSignIn(request));
  }


  // 액세스 토큰 재발급
  @Override
  @PostMapping("/reissue")
  @LogMonitoringInvocation
  public ResponseEntity<LoginResponse> reissue(
      @Valid @RequestBody ReissueRequest request
  ) {
    return ResponseEntity.ok(memberService.reissue(request));
  }
}
