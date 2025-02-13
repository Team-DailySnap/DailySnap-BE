package onepiece.dailysnapbackend.object.postgres;

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

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Follow extends BasePostgresEntity{

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long followId; // 팔로우 고유 Id

  // 팔로잉 회원(팔로우를 하는 회원)
  @ManyToOne(fetch= FetchType.LAZY)
  @JoinColumn(name="following_id",nullable = false)
  private Member following;

  // 팔로워 회원(팔로우를 받는 회원)
  @ManyToOne(fetch=FetchType.LAZY)
  @JoinColumn(name="follower_id",nullable=false)
  private Member follower;
}
