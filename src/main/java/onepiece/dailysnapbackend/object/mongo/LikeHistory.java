package onepiece.dailysnapbackend.object.mongo;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeHistory {

  // 좋아요 내역 ID
  @Id
  private String likeHistoryId;

  // 좋아요가 눌린 사진 게시물 ID
  @Indexed
  private UUID postId;

  // 좋아요를 누른 회원 ID
  @Indexed
  private UUID memberId;
}

