package onepiece.dailysnapbackend.object.postgres;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import onepiece.dailysnapbackend.object.constants.KeywordCategory;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Keyword extends BasePostgresEntity{

  // 키워드 ID
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(updatable = false, nullable = false)
  private UUID keywordId;

  // 키워드 이름 (한글)
  @Column(nullable = false, unique = true)
  private String koreanKeyword;

  @Column(nullable = false)
  private String englishKeyword;

  // 키워드 카테고리 (계절, 여행, 일상 등)
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private KeywordCategory keywordCategory;

  // 제공한 키워드 날짜
  private LocalDate providedDate;

  // 사용 여부 (이미 제공된 키워드인지 여부)
  private boolean used;
}