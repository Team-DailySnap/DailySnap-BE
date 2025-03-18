package onepiece.dailysnapbackend.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import onepiece.dailysnapbackend.object.constants.MimeType;
import onepiece.dailysnapbackend.util.exception.CustomException;
import onepiece.dailysnapbackend.util.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3UploadService {

  @Value("${cloud.aws.s3.bucket}")
  private String bucketName;
  private final S3Client s3Client;

  public List<String> upload(List<MultipartFile> files) {
    List<String> uploadFileNames = new ArrayList<>();
    for (MultipartFile file : files) {
      uploadFileNames.add(upload(file));
    }

    return uploadFileNames;
  }

  public String upload(MultipartFile file) {
    String fileType = file.getContentType();
    // 파일 형식 제한 검사
    if (fileType == null || !MimeType.isAllowed(fileType)) {
      log.error("허용되지 않은 파일 형식: {}", fileType);
      throw new CustomException(ErrorCode.INVALID_FILE_TYPE);
    }

    try {
      // Webp 로 변환 시도
      ConvertedFile convertedFile = convertToWebp(file);
      String fileUrl = uploadToS3(convertedFile.fileName, MimeType.WEBP.getMimeType(), convertedFile.bytes);

      log.info("WebP 이미지 업로드 완료: imageUrl={}", fileUrl);
      return fileUrl;
    } catch (Exception e) {
      log.warn("WebP 변환 실패, 원본 형식으로 업로드 시도: {}", e.getMessage());
      try {
        return uploadToS3(generateFileName(file.getOriginalFilename()), fileType, file.getBytes());
      } catch (IOException ex) {
        throw new CustomException(ErrorCode.FILE_UPLOAD_FAILED);
      }
    }
  }

  private String uploadToS3(String fileName, String contentType, byte[] bytes) {
    try {
      s3Client.putObject(
          PutObjectRequest.builder()
              .bucket(bucketName)
              .key(fileName)
              .contentType(contentType)
              .build(),
          RequestBody.fromBytes(bytes)
      );
      return "https://" + bucketName + ".s3.amazonaws.com/" + fileName;
    } catch (Exception e) {
      log.error("S3 업로드 실패: fileName={}, error={}", fileName, e.getMessage(), e);
      throw new CustomException(ErrorCode.FILE_UPLOAD_FAILED);
    }
  }

  private static class ConvertedFile {

    String fileName;
    byte[] bytes;

    ConvertedFile(String fileName, byte[] bytes) {
      this.fileName = fileName;
      this.bytes = bytes;
    }
  }

  // Webp 로 변환 (파일명도 내부에서 처리)
  private ConvertedFile convertToWebp(MultipartFile file) throws IOException {
    if ("image/webp".equals(file.getContentType())) {
      String fileName = generateFileName(file.getOriginalFilename());
      return new ConvertedFile(fileName, file.getBytes());
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
    return new ConvertedFile(webpFileName, baos.toByteArray());
  }

  // 파일명 생성
  private String generateFileName(String originalFileName) {
    return originalFileName != null && !originalFileName.isBlank()
        ? UUID.randomUUID() + "_" + originalFileName
        : UUID.randomUUID() + "_" + System.currentTimeMillis();
  }
}
