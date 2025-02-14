package onepiece.dailysnapbackend.object.mongo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report {

  // 신고 ID
  @Id
  private String reportId;

  // 신고자 회원 ID
  private String reporterId;

  // 신고된 사진 게시물 ID
  private String reportedPhotoId;

  // 신고 카테고리
  private String reportCategory;

  // 신고 내용
  private String reportContent;
}
