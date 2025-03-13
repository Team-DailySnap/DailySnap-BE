package onepiece.dailysnapbackend.service;

import jakarta.transaction.Transactional;
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

  private static final List<String> ALLOWED_FILE_TYPES = List.of(
      "image/jpeg", "image/png", "image/jpg"
  );
  private static final String WEBP_CONTENT_TYPE = "image/webp";

  @Transactional
  public List<String> upload(List<MultipartFile> files) {
    List<String> uploadFileNames = new ArrayList<>();
    for (MultipartFile file : files) {
      uploadFileNames.add(upload(file));
    }

    return uploadFileNames;
  }

  @Transactional
  public String upload(MultipartFile file) {
    String fileType = file.getContentType();
    log.info("파일 형식: {}", fileType);

    // 파일 형식 제한 검사
    if (fileType == null || !ALLOWED_FILE_TYPES.contains(fileType)) {
      log.error("허용되지 않은 파일 형식: {}", fileType);
      throw new CustomException(ErrorCode.INVALID_FILE_TYPE);
    }

    String originalFileName = file.getOriginalFilename();
    String fileNameBase = UUID.randomUUID() + "_" +
                          (originalFileName != null ? originalFileName.replaceAll("\\..*$", "") : "image");

    try {
      // Webp 로 변환 시도
      String webpFileName = fileNameBase + ".webp";
      byte[] webpBytes = convertToWebp(file);
      String fileUrl = uploadToS3(webpFileName, WEBP_CONTENT_TYPE, webpBytes);
      log.info("WebP 이미지 업로드 완료: imageUrl={}", fileUrl);
      return fileUrl;
    } catch (Exception e) {
      log.warn("WebP 변환 실패, 원본 형식으로 업로드 시도: {}", e.getMessage());
      String originalFileNameFull = fileNameBase +
                                    (originalFileName != null && originalFileName.contains(".")
                                        ? originalFileName.substring(originalFileName.lastIndexOf("."))
                                        : ".jpg");
      try {
        String fileUrl = uploadToS3(originalFileNameFull, fileType, file.getBytes());
        log.info("원본 이미지 업로드 완료: imageUrl={}", fileUrl);
        return fileUrl;
      } catch (IOException ex) {
        log.error("파일 업로드 실패: {}", ex.getMessage());
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
      log.error("S3 업로드 실패: fileName={}, error={}", fileName, e.getMessage());
      throw new CustomException(ErrorCode.FILE_UPLOAD_FAILED);
    }
  }

  private byte[] convertToWebp(MultipartFile file) throws IOException {
    BufferedImage image = ImageIO.read(new ByteArrayInputStream(file.getBytes()));
    if (image == null) {
      throw new CustomException(ErrorCode.FILE_UPLOAD_FAILED);
    }

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    boolean success = ImageIO.write(image, "webp", baos);
    if (!success) {
      throw new IOException("WebP 형식으로 변환 실패");
    }
    return baos.toByteArray();
  }
}