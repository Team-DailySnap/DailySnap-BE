package onepiece.dailysnapbackend.object.constants;

import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MimeType {

  JPEG("image/jpeg"),
  PNG("image/png"),
  JPG("image/jpg"),
  WEBP("image/webp");

  private final String mimeType;

  // MIME 타입 검증
  public static boolean isAllowed(String mimeType) {
    return Arrays.stream(values())
        .anyMatch(type -> type.getMimeType().equalsIgnoreCase(mimeType));
  }
}
