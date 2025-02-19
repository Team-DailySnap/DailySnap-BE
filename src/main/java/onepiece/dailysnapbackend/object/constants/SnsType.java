package onepiece.dailysnapbackend.object.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SnsType {
  // 많이 사용하는 소셜 플랫폼 탑 10
  YOUTUBE("유튜브"),
  INSTAGRAM("인스타그램"),
  FACEBOOK("페이스북"),
  TWITTER("트위터"),
  KAKAOTALK("카카오톡"),
  NAVER_BLOG("네이버 블로그"),
  TIKTOK("틱톡"),
  LINKEDIN("링크드인"),
  BAND("밴드"),
  PINTEREST("핀터레스트");

  private final String description;
}
