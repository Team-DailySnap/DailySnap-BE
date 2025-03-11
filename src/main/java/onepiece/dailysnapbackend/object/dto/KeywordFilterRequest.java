package onepiece.dailysnapbackend.object.dto;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class KeywordFilterRequest {

  public KeywordFilterRequest() {
    this.pageNumber = 0;
    this.pageSize = 30;
    this.sortField = "created_date";
    this.sortDirection = "DESC";
  }

  @Schema(defaultValue="바다")
  private String keyword;

  @Schema(defaultValue="SUMMER")
  private String category;

  @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "'YYYY-MM-DD' 형식이어야 합니다.")
  @Schema(defaultValue = "2025-03-07")
  private String providedDate;

  @Builder.Default
  private Boolean isUsed = null;

  @Schema(defaultValue = "0")
  @Min(value = 0, message = "페이지 번호는 0 이상이어야 합니다.")
  @Max(value = Integer.MAX_VALUE, message = "페이지 번호가 정수 최대값을 초과할 수 없습니다.")
  @Parameter(description = "페이지 번호 (0부터 시작)", required = false)
  private Integer pageNumber;

  @Schema(defaultValue = "100")
  @Min(value = 1, message = "페이지 사이즈는 1 이상이어야 합니다.")
  @Max(value = 100, message = "페이지 사이즈는 100을 초과할 수 없습니다.")
  @Parameter(description = "페이지 크기", required = false)
  private Integer pageSize;

  @Schema(defaultValue = "created_date")
  @Pattern(regexp = "^(created_date|provided_date|keyword)$", message = "정렬 필드는 'created_date', 'provided_date', 'keyword' 중 하나여야 합니다.")
  @Parameter(description = "정렬 기준 (created_date, provided_date, keyword)", required = false)
  private String sortField;

  @Schema(defaultValue = "DESC")
  @Pattern(regexp = "^(ASC|DESC)$", message = "정렬 방향은 'ASC' 또는 'DESC'만 입력 가능합니다.")
  @Parameter(description = "정렬 방향 (ASC 또는 DESC)", required = false)
  private String sortDirection;
}
