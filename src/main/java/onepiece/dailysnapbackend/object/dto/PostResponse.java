package onepiece.dailysnapbackend.object.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import onepiece.dailysnapbackend.object.postgres.Image;
import onepiece.dailysnapbackend.object.postgres.Keyword;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostResponse {

  private Keyword keyword;
  private List<Image> images;
  private String content;
  private String location;
}
