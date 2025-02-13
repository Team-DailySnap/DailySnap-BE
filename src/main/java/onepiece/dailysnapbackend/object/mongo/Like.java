package onepiece.dailysnapbackend.object.mongo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "likes") // 몽고DB 컬렉션 이름
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Like {

  @Id
  private String likeId;

  // 좋아요가 눌린 사진 게시물 Id
  private Long photoPostId;

  // 좋아요를 누른 회원 Id
  private Long memberId;
}

