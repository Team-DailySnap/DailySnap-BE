package onepiece.dailysnapbackend.object.mongo;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

@ToString
@SuperBuilder
@Document
@Getter
@Setter
@NoArgsConstructor
public abstract class BaseMongoEntity {

  // 생성일
  @CreatedDate
  private LocalDateTime createdDate;

  // 수정일
  @LastModifiedDate
  private LocalDateTime updatedDate;

  // 수정 여부
  private boolean isEdited = false;

  // 삭제 여부
  private boolean isDeleted = false;
}