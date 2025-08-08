package onepiece.dailysnapbackend.object.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import onepiece.dailysnapbackend.object.constants.KeywordCategory;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KeywordRequest {

  @NotNull(message = "키워드 카테고리를 입력하세요")
  private KeywordCategory category;

  @NotBlank(message = "키워드(한국어)를 입력하세요")
  private String koreanKeyword;

  @NotBlank(message = "키워드(영어)를 입력하세요")
  private String englishKeyword;

  @NotNull(message = "키워드를 제공할 날짜를 입력하세요")
  private LocalDate specifiedDate;

}