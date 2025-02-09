package onepiece.dailysnapbackend.object.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@AllArgsConstructor
@Getter
@Setter
@Builder
public class SignUpRequest {

  @NotBlank(message = "이메일을 입력하세요")
  @Email(message = "이메일 형식이 아닙니다")
  @Schema(defaultValue = "example@naver.com")
  private String username; // 이메일

  @NotBlank(message = "비밀번호를 입력하세요")
  @Schema(defaultValue = "pw12345")
  private String password; // 비밀번호

  @NotBlank(message = "닉네임을 입력하세요")
  @Schema(defaultValue = "nickname123")
  private String nickname; // 닉네임

}
