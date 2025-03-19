package onepiece.dailysnapbackend.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;
import javax.imageio.ImageIO;
import lombok.extern.slf4j.Slf4j;
import onepiece.dailysnapbackend.object.constants.MimeType;
import onepiece.dailysnapbackend.object.dto.FileResponse;
import onepiece.dailysnapbackend.util.exception.CustomException;
import onepiece.dailysnapbackend.util.exception.ErrorCode;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
public class FileUtil {

  // Webp로 변환
  public static FileResponse convertToWebp(MultipartFile file) throws IOException {
    if (MimeType.WEBP.getMimeType().equals(file.getContentType())) {
      String fileName = generateFileName(file.getOriginalFilename());
      return new FileResponse(fileName, file.getBytes());
    }

    BufferedImage image = ImageIO.read(new ByteArrayInputStream(file.getBytes()));
    if (image == null) {
      log.error("파일이 유효하지 않음: fileName={}", file.getOriginalFilename());
      throw new CustomException(ErrorCode.FILE_UPLOAD_FAILED);
    }

    String fileName = generateFileName(file.getOriginalFilename());
    String webpFileName = fileName.replaceAll("\\.[^.]+$", ".webp");

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    boolean success = ImageIO.write(image, "webp", baos);
    if (!success) {
      throw new IOException("WebP 형식으로 변환 실패");
    }
    return new FileResponse(webpFileName, baos.toByteArray());
  }

  // 파일명 생성
  public static String generateFileName(String originalFileName) {
    return StringUtils.hasText(originalFileName)
        ? UUID.randomUUID() + "_" + originalFileName
        : UUID.randomUUID() + "_" + System.currentTimeMillis();
  }
}
