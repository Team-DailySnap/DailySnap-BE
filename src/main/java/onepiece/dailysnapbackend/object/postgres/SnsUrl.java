package onepiece.dailysnapbackend.object.postgres;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.util.UUID;
import onepiece.dailysnapbackend.object.constants.SnsType;

public class SnsUrl extends BasePostgresEntity {

  // 소셜 하이퍼링크 ID
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(updatable = false, nullable = false)
  private UUID snsUrlId;

  // 회원과의 관계
  @ManyToOne(fetch = FetchType.LAZY)
  private Member member;

  // 소셜 URL
  @Column(nullable = false)
  private String snsUrl;

  // 소셜 종류
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private SnsType snsType;
}
