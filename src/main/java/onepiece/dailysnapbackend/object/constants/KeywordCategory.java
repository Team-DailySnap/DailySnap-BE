package onepiece.dailysnapbackend.object.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum KeywordCategory {
  ADMIN_SET("관리자 지정"),   // 특정 날짜에 지정한 키워드
  SPRING("봄"),
  SUMMER("여름"),
  AUTUMN("가을"),
  WINTER("겨울"),
  TRAVEL("여행"),       // 여행 관련
  DAILY("일상"),        // 일상
  ABSTRACT("추상"),    // 감성적인 표현
  RANDOM("랜덤");      // 기타 키워드

  private final String prompt;
}