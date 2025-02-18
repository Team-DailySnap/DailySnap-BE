package onepiece.dailysnapbackend.object.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ReportCategory {
  SPAM("스팸/광고"),
  INAPPROPRIATE_CONTENT("부적절한 콘텐츠"),
  HARASSMENT("괴롭힘/폭력"),
  COPYRIGHT_VIOLATION("저작권 침해"),
  OTHER("기타");

  private final String description;
}