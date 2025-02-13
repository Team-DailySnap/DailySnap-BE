package onepiece.dailysnapbackend.object.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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

  @NotBlank(message = "생년월일을 입력하세요")
  @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "생년월일은 'YYYY-MM-DD' 형식이어야 합니다.")
  @Schema(defaultValue = "2000-10-30")
  private String birth; // 생년월일 (YYYY-MM-DD)
}
