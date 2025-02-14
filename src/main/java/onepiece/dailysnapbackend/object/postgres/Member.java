package onepiece.dailysnapbackend.object.postgres;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import onepiece.dailysnapbackend.object.constants.AccountStatus;
import onepiece.dailysnapbackend.object.constants.Role;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member extends BasePostgresEntity{

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

  // 생년월일
  @Column(nullable = false)
  private String birth;

  // 프로필 사진 URL
  private String profileImageUrl;

  // 권한 (유저, 관리자)
  @Enumerated(EnumType.STRING)
  private Role role;

  // 계정 상태 (활성, 삭제)
  @Enumerated(EnumType.STRING)
  @Builder.Default
  private AccountStatus accountStatus = AccountStatus.ACTIVE_ACCOUNT;

  // 총 업로드 수
  @Builder.Default
  private int uploadCount=0;

  // 총 좋아요 수
  @Builder.Default
  private int likeCount=0;

  // 내가 팔로우한 회원 목록
  @OneToMany(mappedBy = "following", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Follow> following = new ArrayList<>();

  // 나를 팔로우한 회원 목록
  @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Follow> followers = new ArrayList<>();
}

