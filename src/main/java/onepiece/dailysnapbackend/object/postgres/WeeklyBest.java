package onepiece.dailysnapbackend.object.postgres;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import java.util.UUID;
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
public class WeeklyBest extends BasePostgresEntity{

  // 주간 우수작 ID
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(columnDefinition = "uuid DEFAULT uuid_generate_v4()", updatable = false, nullable = false)
  private UUID weeklyBestId;

  // 일간 우수작
  @ManyToOne(fetch = FetchType.LAZY)
  private DailyBest dailyBest;

  // 해당 주의 시작 날짜
  @Column(nullable = false)
  private LocalDate weekStartDate;

  // 해당 주의 종료 날짜
  @Column(nullable = false)
  private LocalDate weekEndDate;
}
