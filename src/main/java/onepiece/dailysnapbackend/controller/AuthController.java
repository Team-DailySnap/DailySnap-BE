package onepiece.dailysnapbackend.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import onepiece.dailysnapbackend.object.dto.ApiResponse;
import onepiece.dailysnapbackend.object.dto.SignUpRequest;
import onepiece.dailysnapbackend.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(
    name = "회원 관련 API",
    description = ""
)
public class AuthController {

  private final MemberService memberService;

  @PostMapping("/signup")
  public ResponseEntity<ApiResponse<Void>> signUp(@Valid @RequestBody SignUpRequest request) {
    return ResponseEntity.ok(memberService.signUp(request));
  }
}
