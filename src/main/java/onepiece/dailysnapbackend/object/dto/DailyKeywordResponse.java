package onepiece.dailysnapbackend.object.dto;

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
@AllArgsConstructor
@NoArgsConstructor
public class DailyKeywordResponse {

  private String keyword;
  private KeywordCategory category;
  private LocalDate providedDate;
}