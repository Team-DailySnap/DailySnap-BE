package onepiece.dailysnapbackend.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import onepiece.dailysnapbackend.object.constants.MimeType;
import onepiece.dailysnapbackend.object.dto.FileResponse;
import onepiece.dailysnapbackend.util.FileUtil;
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
    if (fileType == null || !MimeType.isAllowedMimeType(fileType)) {
      log.error("허용되지 않은 파일 형식: {}", fileType);
      throw new CustomException(ErrorCode.INVALID_FILE_TYPE);
    }

    try {
      // Webp 로 변환 시도
      FileResponse fileResponse = FileUtil.convertToWebp(file);
      String fileUrl = uploadToS3(fileResponse.getFileName(), MimeType.WEBP.getMimeType(), fileResponse.getBytes());

      log.info("WebP 이미지 업로드 완료: imageUrl={}", fileUrl);
      return fileUrl;
    } catch (Exception e) {
      log.warn("WebP 변환 실패, 원본 형식으로 업로드 시도: {}", e.getMessage());
      try {
        return uploadToS3(FileUtil.generateFileName(file.getOriginalFilename()), fileType, file.getBytes());
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
}
