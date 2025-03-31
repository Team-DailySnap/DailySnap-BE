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
public class PostFilteredRequest {

  public PostFilteredRequest() {
    this.pageNumber = 0;
    this.pageSize = 30;
    this.sortField = "created_date";
    this.sortDirection = "DESC";
  }

  private String nickname;

  @Schema(defaultValue = "0")
  @Min(value = 0, message = "페이지 번호 인덱스에 음수는 입력될 수 없습니다.")
  @Max(value = Integer.MAX_VALUE, message = "정수 최대 범위를 넘을 수 없습니다.")
  private Integer pageNumber; // 페이지 번호

  @Schema(defaultValue = "100")
  @Min(value = 0, message = "페이지 사이즈에 음수는 입력될 수 없습니다.")
  @Max(value = Integer.MAX_VALUE, message = "정수 최대 범위를 넘을 수 없습니다.")
  private Integer pageSize; // 페이지 사이즈

  @Schema(defaultValue = "created_date")
  @Pattern(regexp = "^(created_date|like_count)")
  private String sortField; // 정렬 조건 (생성일, 좋아요)

  @Schema(defaultValue = "DESC")
  @Pattern(regexp = "^(ASC|DESC)$", message = "sortDirection에는 'ASC', 'DESC' 만 입력 가능합니다.")
  private String sortDirection; // ASC, DESC
}
