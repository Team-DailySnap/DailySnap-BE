package onepiece.dailysnapbackend.object.mongo;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import onepiece.dailysnapbackend.object.constants.ReportCategory;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
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
  @Indexed
  private UUID reporterId;

  // 신고된 사진 게시물 ID
  @Indexed
  private UUID reportedPostId;

  // 신고 카테고리
  private ReportCategory reportCategory;

  // 신고 내용
  private String reportContent;
}
