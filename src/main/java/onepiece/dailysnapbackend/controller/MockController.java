package onepiece.dailysnapbackend.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import onepiece.dailysnapbackend.object.dto.LoginResponse;
import onepiece.dailysnapbackend.object.dto.MockLoginRequest;
import onepiece.dailysnapbackend.service.MockService;
import onepiece.dailysnapbackend.util.log.LogMonitoringInvocation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mock")
@Tag(
    name = "개발자 편의 API",
    description = "개발 편의 관련 API 제공"
)
public class MockController implements MockControllerDocs {

  private final MockService mockService;

  @Override
  @PostMapping("/member")
  @LogMonitoringInvocation
  public ResponseEntity<LoginResponse> createMockMember(
      @RequestBody MockLoginRequest request) {
    return ResponseEntity.ok(mockService.mockLogin(request));
  }

  @PostMapping("/member/random")
  @LogMonitoringInvocation
  public ResponseEntity<Void> createMockMemberRandom(int count) {
    mockService.createMockMember(count);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/post")
  @LogMonitoringInvocation
  public ResponseEntity<Void> createMockPost(int count) {
    mockService.createMockPost(count);
    return ResponseEntity.ok().build();
  }
}
