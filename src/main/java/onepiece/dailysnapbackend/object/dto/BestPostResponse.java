package onepiece.dailysnapbackend.object.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class BestPostResponse {

  private UUID postId;  // 게시물 ID
  private String keyword; // 키워드
  private Integer viewCount;  // 조회수
  private Integer likesCount; // 좋아요 수
  private LocalDateTime createdDate;  // 생성일
}
