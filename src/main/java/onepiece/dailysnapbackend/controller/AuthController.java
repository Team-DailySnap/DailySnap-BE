package onepiece.dailysnapbackend.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import onepiece.dailysnapbackend.object.dto.ApiResponse;
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
public class AuthController implements AuthControllerDocs {

  private final MemberService memberService;


  @Override
  @PostMapping("/signup")
  @LogMonitoringInvocation
  public ResponseEntity<ApiResponse<Void>> signUp(@Valid @RequestBody SignUpRequest request) {
    return ResponseEntity.ok(memberService.signUp(request));
  }
}
