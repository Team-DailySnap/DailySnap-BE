package onepiece.dailysnapbackend.object.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import onepiece.dailysnapbackend.object.constants.SocialPlatform;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class LoginRequest {

  @NotBlank(message = "소셜 로그인 플렛폼을 입력하세요 (예: KAKAO, GOOGLE)")
  private SocialPlatform socialPlatform;

  @NotBlank(message = "이메일을 입력하세요")
  private String username;

  private String birth;

  private String nickname;
}
