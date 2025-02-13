package onepiece.dailysnapbackend.object.postgres;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PhotoPost extends BasePostgresEntity{
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long photoPostId; // 사진 게시물 Id

  // 작성자 회원
  @ManyToOne(fetch= FetchType.LAZY)
  @JoinColumn(name="member_id",nullable=false)
  private Member member;

  // 키워드
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "daily_keyword_id", nullable = false)
  private DailyKeyword dailyKeyword;

  // 사진 URL
  @Column(nullable = false)
  private String imageUrl;

  // 사진 설명
  @Column(nullable = true)
  private String content;

  // 좋아요 수
  @Column(nullable=false)
  @Builder.Default
  private int likeCount=0;
}
