package onepiece.dailysnapbackend.object.postgres;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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
public class DailyKeyword extends BasePostgresEntity{

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long dailyKeywordId;

  // 키워드 이름
  @Column(nullable = false, unique = true)
  private String keyword;

  // 날짜
  @Column(nullable = false)
  private LocalDate date;

  // 우수작 게시물 Id
  @OneToOne
  @JoinColumn(name = "best_photo_post_id")
  private PhotoPost bestPhotoPost;

  // 사진 게시물 리스트 (1:N 관계)
  @OneToMany(mappedBy = "dailyKeyword", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<PhotoPost> photoPosts = new ArrayList<>();
}