package onepiece.dailysnapbackend.object.postgres;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
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
public class MonthlyBest extends BasePostgresEntity{

  // 월간 우수작 ID
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(columnDefinition = "uuid DEFAULT uuid_generate_v4()", updatable = false, nullable = false)
  private UUID monthlyBestId;

  // 주간 우수작
  @ManyToOne(fetch = FetchType.LAZY)
  private WeeklyBest weeklyBest;

  // 해당 월의 시작 날짜
  @Column(nullable = false)
  private LocalDate monthStartDate;

  // 해당 월의 종료 날짜
  @Column(nullable = false)
  private LocalDate monthEndDate;
}
