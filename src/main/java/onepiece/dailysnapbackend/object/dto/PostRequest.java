package onepiece.dailysnapbackend.object.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostRequest {

  @NotNull(message = "키워드를 입력하세요")
  private UUID keywordId;

  @NotNull(message = "이미지를 업로드하세요")
  private MultipartFile image;

  private String content;

  private String location;
}
