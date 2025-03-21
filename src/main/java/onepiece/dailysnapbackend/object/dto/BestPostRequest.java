package onepiece.dailysnapbackend.object.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import onepiece.dailysnapbackend.object.constants.BestPostFilter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class BestPostRequest {

  public BestPostRequest() {
    this.filter = BestPostFilter.DAILY;
    this.startDate = LocalDateTime.now();
  }

  @Schema(description = "게시물 필터 유형 (일간, 주간, 월간)", defaultValue = "daily")
  private BestPostFilter filter;

  @Schema(description = "게시물 조회 시작 날짜", defaultValue = "2025-03-01")
  private LocalDateTime startDate;
}
