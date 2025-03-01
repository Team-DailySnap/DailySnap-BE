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
@NoArgsConstructor
@AllArgsConstructor
public class KeywordRequest {

  private KeywordCategory category;  // 카테고리 필터링

  private String keyword;  // 키워드 필터링

  private LocalDate specifiedDate;  // 특정 날짜에 제공될 키워드

}