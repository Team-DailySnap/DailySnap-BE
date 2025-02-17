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
  @Builder.Default
  private UUID reportId = UUID.randomUUID();

  // 신고자 회원 ID
  private UUID reporterId;

  // 신고된 사진 게시물 ID
  private UUID reportedPhotoId;

  // 신고 카테고리
  @Indexed
  private ReportCategory reportCategory;

  // 신고 내용
  private String reportContent;
}
