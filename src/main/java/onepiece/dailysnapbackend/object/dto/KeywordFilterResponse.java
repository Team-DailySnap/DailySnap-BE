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
public class KeywordFilterResponse {

  private String keyword; // 키워드 텍스트 필터
  private KeywordCategory category; // 카테고리 필터
  private LocalDate providedDate; // 날짜 필터
}