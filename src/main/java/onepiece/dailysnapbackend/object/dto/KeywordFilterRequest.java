package onepiece.dailysnapbackend.object.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import onepiece.dailysnapbackend.object.constants.KeywordCategory;

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

  private String keyword;  // 조회할 키워드 텍스트 (선택 사항)

  private KeywordCategory category;  // 조회할 키워드 카테고리 (선택 사항)

  private LocalDate providedDate;  // 제공일 (선택 사항)

  @Schema(defaultValue = "0")
  @Min(value = 0, message = "페이지 번호는 0 이상이어야 합니다.")
  @Max(value = Integer.MAX_VALUE, message = "페이지 번호가 정수 최대값을 초과할 수 없습니다.")
  private Integer pageNumber;  // 페이지 번호

  @Schema(defaultValue = "30")
  @Min(value = 1, message = "페이지 사이즈는 1 이상이어야 합니다.")
  @Max(value = 100, message = "페이지 사이즈는 100을 초과할 수 없습니다.")
  private Integer pageSize;  // 페이지 사이즈

  @Schema(defaultValue = "created_date")
  @Pattern(regexp = "^(created_date|provided_date|keyword)$",
      message = "정렬 필드는 'created_date', 'provided_date', 'keyword' 중 하나여야 합니다.")
  private String sortField;  // 정렬 조건

  @Schema(defaultValue = "DESC")
  @Pattern(regexp = "^(ASC|DESC)$", message = "정렬 방향은 'ASC' 또는 'DESC'만 입력 가능합니다.")
  private String sortDirection;  // 정렬 방향
}
