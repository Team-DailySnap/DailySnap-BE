package onepiece.dailysnapbackend.object.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MediaType {
  IMAGE("image"),
  VIDEO("video");

  private final String type;
}
