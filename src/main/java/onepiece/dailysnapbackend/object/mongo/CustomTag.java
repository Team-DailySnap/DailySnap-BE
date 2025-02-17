package onepiece.dailysnapbackend.object.mongo;

import jakarta.persistence.Id;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomTag extends BaseMongoEntity {

  // 커스텀 태그 ID
  @Id
  @Builder.Default
  private UUID customTagId = UUID.randomUUID();

  // 연결된 사진 게시물 ID
  private UUID photoId;

  // 태그 내용
  @Indexed
  private String customTag;
}