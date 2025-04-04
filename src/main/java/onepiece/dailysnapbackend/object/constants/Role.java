package onepiece.dailysnapbackend.object.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Role {
  ROLE_ADMIN("관리자"),
  ROLE_USER("일반 회원");

  private final String description;
}
