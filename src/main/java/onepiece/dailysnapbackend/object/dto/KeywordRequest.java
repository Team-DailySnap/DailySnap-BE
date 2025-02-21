package onepiece.dailysnapbackend.object.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KeywordRequest {

  @NotNull(message = "카테고리를 입력하세요")
  private String category;

  @NotNull(message = "키워드를 입력하세요")
  private String keyword;

  // 특정 날짜에 제공할 키워드
  private LocalDate specifiedDate;

  // 해당 키워드가 사용자에게 제공된 날짜
  private LocalDate providedDate;

}