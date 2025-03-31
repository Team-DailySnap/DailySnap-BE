package onepiece.dailysnapbackend.object.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import onepiece.dailysnapbackend.object.constants.AccountStatus;
import onepiece.dailysnapbackend.object.constants.Role;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberResponse {

  private UUID memberId;
  private String username;
  private String nickname;
  private String birth;
  private String profileImageUrl;
  private Role role;
  private AccountStatus accountStatus;
  private Integer dailyUploadCount;
  private boolean isPaid;
}
