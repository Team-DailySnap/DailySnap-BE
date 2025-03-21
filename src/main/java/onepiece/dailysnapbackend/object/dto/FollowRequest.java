package onepiece.dailysnapbackend.object.dto;

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
public class FollowRequest {

  public FollowRequest() {
    this.pageNumber = 0;
    this.pageSize = 30;
    this.sortField = "created_date";
    this.sortDirection = "DESC";
  }

  @Schema(defaultValue = "0")
  @Min(value = 0, message = "페이지 번호는 0 이상이어야 합니다.")
  @Max(value = Integer.MAX_VALUE, message = "페이지 번호가 정수 최대값을 초과할 수 없습니다.")
  private Integer pageNumber;

  @Schema(defaultValue = "30")
  @Min(value = 1, message = "페이지 사이즈는 1 이상이어야 합니다.")
  @Max(value = 100, message = "페이지 사이즈는 100을 초과할 수 없습니다.")
  private Integer pageSize;

  @Schema(defaultValue = "createdDate")
  @Pattern(regexp = "^(createdDate)")
  private String sortField; // 정렬 조건 (생성일)

  @Schema(defaultValue = "DESC")
  @Pattern(regexp = "^(ASC|DESC)$", message = "sortDirection 에는 'ASC', 'DESC' 만 입력 가능합니다.")
  private String sortDirection; // ASC, DESC
}
