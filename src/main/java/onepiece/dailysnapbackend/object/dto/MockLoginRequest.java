package onepiece.dailysnapbackend.object.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import onepiece.dailysnapbackend.object.constants.Role;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MockLoginRequest {

  private String username;

  private String nickname;

  private Role role;
}
