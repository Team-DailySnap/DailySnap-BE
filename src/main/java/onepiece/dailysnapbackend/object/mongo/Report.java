package onepiece.dailysnapbackend.object.mongo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "reports") // 몽고DB 컬렉션 이름
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report {

  @Id
  private String id;

  // 신고자 회원 Id
  private Long reporterId;

  // 신고된 사진 게시물 Id
  private Long reportedPhotoPostId;
}
