package onepiece.dailysnapbackend.object.constants;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import onepiece.dailysnapbackend.util.CommonUtil;
import onepiece.dailysnapbackend.util.SortField;

@Getter
@AllArgsConstructor
public enum PostSortField implements SortField {

  CREATED_DATE("createdDate"),

  LIKE_COUNT("likeCount"),

  ;

  private final String property;

  /**
   * Jackson이 JSON -> Java 객체로 역직렬화 (deserialization)할 때 자동 호출
   * 컨트롤러에서 들어온 {"sortField": "TICKET_OPEN_DATE"} 같은 문자열을 변환
   * 만약 {"sortField": "ticketOpenDate"}와 같이 카멜케이스로 들어와도 f.property와 비교하여 자동 매칭
   */
  @JsonCreator
  public static PostSortField from(String value) {
    return CommonUtil.stringToSortField(PostSortField.class, value);
  }
}
