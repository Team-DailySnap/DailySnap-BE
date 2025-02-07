package onepiece.dailysnapbackend.object.postgres;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import onepiece.dailysnapbackend.object.constants.Role;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long memberId;


  // 이메일
  @Column(unique = true, nullable = false)
  private String username;

  // 비밀번호
  @Column(nullable = false)
  private String password;

  // 닉네임
  @Column(unique = true, nullable = false)
  private String nickname;

  // 권한 (유저, 관리자)
  @Enumerated(EnumType.STRING)
  private Role role;
}

