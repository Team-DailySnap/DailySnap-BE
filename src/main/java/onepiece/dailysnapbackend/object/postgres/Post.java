  package onepiece.dailysnapbackend.object.postgres;

  import jakarta.persistence.Column;
  import jakarta.persistence.Entity;
  import jakarta.persistence.FetchType;
  import jakarta.persistence.GeneratedValue;
  import jakarta.persistence.GenerationType;
  import jakarta.persistence.Id;
  import jakarta.persistence.ManyToOne;
  import java.util.UUID;
  import lombok.AllArgsConstructor;
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
  public class Post extends BasePostgresEntity{

    // 게시물 ID
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "uuid DEFAULT uuid_generate_v4()", updatable = false, nullable = false)
    private UUID postId;

    // 작성자 회원
    @ManyToOne(fetch= FetchType.LAZY)
    private Member member;

    // 키워드
    @ManyToOne(fetch = FetchType.LAZY)
    private Keyword keyword;

    // 일간 우수작
    @ManyToOne(fetch = FetchType.LAZY)
    private DailyBest dailyBest;

    // 사진 URL
    @Column(nullable = false)
    private String imageUrl;

    // 사진 설명
    @Column(nullable = true)
    private String content;

    // 좋아요 수
    @Column(nullable = false)
    private Integer likeCount;

    // 위치
    private String location;
  }
