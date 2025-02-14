package onepiece.dailysnapbackend.object.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class SignInRequest {

  // 이메일
  @NotBlank(message = "이메일을 입력하세요")
  @Schema(defaultValue = "example@naver.com")
  private String username;

  // 비밀번호
  @NotBlank(message = "비밀번호를 입력하세요")
  @Schema(defaultValue = "pw12345")
  private String password;

}
