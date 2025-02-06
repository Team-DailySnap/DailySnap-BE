package onepiece.dailysnapbackend.object.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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

  // 아이디
  @NotBlank(message = "아이디는 필수 입력 값입니다.")
  @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "아이디는 영문자, 숫자, 밑줄(_)만 허용됩니다.")
  @Schema(defaultValue = "id123")
  private String username;

  // 비밀번호
  @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
  @Schema(defaultValue = "pw123")
  @Pattern(
      regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*?&]+$",
      message = "비밀번호는 최소 하나의 영문자와 숫자를 포함해야 합니다."
  )
  private String password;

  // 닉네임
  @NotBlank(message = "닉네임은 필수 입력 값입니다.")
  @Schema(defaultValue = "nickname")
  @Pattern(regexp = "^[a-zA-Z0-9가-힣]+$", message = "닉네임은 영문, 숫자, 한글만 허용됩니다.")
  private String nickname;

}
