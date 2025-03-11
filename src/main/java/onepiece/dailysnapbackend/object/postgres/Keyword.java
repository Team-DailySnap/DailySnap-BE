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
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(columnDefinition = "uuid DEFAULT uuid_generate_v4()", updatable = false, nullable = false)
  private UUID keywordId;

  // 키워드 이름
  @Column(nullable = false, unique = true)
  private String keyword;

  // 키워드 카테고리 (계절, 여행, 일상 등)
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private KeywordCategory category;

  // 특정 날짜에 제공할 키워드 (ADMIN_SET에서 사용)
  private LocalDate specifiedDate;

  // 제공한 키워드 날짜
  private LocalDate providedDate;

  // 사용 여부 (이미 제공된 키워드인지 여부)
  @Column(nullable = false)
  private boolean isUsed;
}