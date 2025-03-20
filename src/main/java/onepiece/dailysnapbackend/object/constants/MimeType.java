package onepiece.dailysnapbackend.object.constants;

import static onepiece.dailysnapbackend.object.constants.MediaType.IMAGE;
import static onepiece.dailysnapbackend.object.constants.MediaType.VIDEO;

import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MimeType {

  JPEG("image/jpeg", IMAGE),
  PNG("image/png", IMAGE),
  JPG("image/jpeg", IMAGE),
  WEBP("image/webp", IMAGE),

  MP4("video/mp4", VIDEO),
  WEBM("video/webm", VIDEO),
  MOV("video/quicktime", VIDEO);

  private final String mimeType;
  private final MediaType mediaType;

  // 이미지 MIME 타입 검증
  public static boolean isAllowedImageType(String mimeType) {
    return Arrays.stream(values())
        .filter(type -> type.getMediaType() == IMAGE)
        .anyMatch(type -> type.getMimeType().equalsIgnoreCase(mimeType));
  }

  // 비디오 MIME 타입 검증
  public static boolean isAllowedVideoType(String mimeType) {
    return Arrays.stream(values())
        .filter(type -> type.getMediaType() == VIDEO)
        .anyMatch(type -> type.getMimeType().equalsIgnoreCase(mimeType));
  }

  // 전체 MIME 타입 검증
  public static boolean isAllowedMimeType(String mimeType) {
    return Arrays.stream(values())
        .anyMatch(type -> type.getMimeType().equalsIgnoreCase(mimeType));
  }
}
