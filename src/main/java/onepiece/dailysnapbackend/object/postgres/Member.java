package onepiece.dailysnapbackend.object.postgres;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import onepiece.dailysnapbackend.object.constants.AccountStatus;
import onepiece.dailysnapbackend.object.constants.Role;
import onepiece.dailysnapbackend.object.constants.SocialPlatform;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member extends BasePostgresEntity{

  // 회원 ID
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(columnDefinition = "uuid DEFAULT uuid_generate_v4()", updatable = false, nullable = false)
  private UUID memberId;

  // 이메일
  @Column(unique = true, nullable = false)
  private String username;

  // 소셜 제공자
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private SocialPlatform socialPlatform;

  // 닉네임
  @Column(unique = true, nullable = true)
  private String nickname;

  // 생년월일
  @Column(nullable = true)
  private String birth;

  // 프로필 사진 URL
  private String profileImageUrl;

  // 권한 (유저, 관리자)
  @Enumerated(EnumType.STRING)
  private Role role;

  @Enumerated(EnumType.STRING)
  @Builder.Default
  private AccountStatus accountStatus = AccountStatus.ACTIVE_ACCOUNT;

  // 일일 최대 업로드 수
  @Column(nullable = false)
  private Integer dailyUploadCount;

  // 첫 로그인 여부
  @Builder.Default
  private Boolean isFirstLogin = true;

  // 과금 여부
  @Column(nullable = false)
  private boolean isPaid;
 }

