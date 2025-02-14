package onepiece.dailysnapbackend.object.postgres;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import java.util.UUID;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberInfo extends BasePostgresEntity{

  // 회원 ID
  @Id
  private UUID memberId;

  // 회원 엔티티와의 매핑
  @OneToOne(fetch = FetchType.LAZY)
  @MapsId
  private Member member;

  // 총 업로드 사진 수
  @Column(nullable = false)
  private int totalUploadCount;

  // 한줄 소개
  private String introduction;

  // 받은 좋아요 개수
  @Column(nullable = false)
  private Integer totalLikeCount;

  // 상위 퍼센트
  @Column(nullable = false)
  private double percent;

  // 어제 우수작 다시보기 여부
  @Column(nullable = false)
  private boolean isViewableBest;
}