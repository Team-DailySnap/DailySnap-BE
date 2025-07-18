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

  @NotBlank(message = "소셜 로그인 플렛폼을 입력하세요 (예: KAKAO, GOOGLE)")
  @Schema(defaultValue = "KAKAO")
  private String provider;

  @NotBlank(message = "이메일을 입력하세요")
  @Schema(defaultValue = "example@naver.com")
  private String username;

  @Schema(description = "생년월일 (선택)", defaultValue = "2004-01-01")
  private String birth;

  @Schema(description = "닉네임 (선택)", defaultValue = "daily_snap_user")
  private String nickname;
}
