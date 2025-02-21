package onepiece.dailysnapbackend.service;

import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
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
  private static final long MAX_FILE_SIZE = 200 * 1024 * 1024; // 200MB 제한


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

    // 파일 크기 제한 검사
    if (file.getSize() > MAX_FILE_SIZE) {
      log.error("파일 크기가 200MB를 초과했습니다: fileSize={}", file.getSize());
      throw new CustomException(ErrorCode.FILE_SIZE_EXCEED);
    }

    // 파일 형식 제한 검사
    if (fileType == null || !ALLOWED_FILE_TYPES.contains(fileType)) {
      log.error("허용되지 않은 파일 형식: {}", fileType);
      throw new CustomException(ErrorCode.INVALID_FILE_TYPE);
    }

    String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

    try {
      s3Client.putObject(
          PutObjectRequest.builder()
              .bucket(bucketName)
              .key(fileName)
              .contentType(file.getContentType())
              .build(),
          RequestBody.fromBytes(file.getBytes())
      );
      log.info("이미지 업로드 완료: imageUrl={}", fileName);
      return "https://" + bucketName + ".s3.amazonaws.com/" + fileName;

    } catch (Exception e) {
      log.error("파일 업로드 실패: {}", e.getMessage());
      throw new CustomException(ErrorCode.FILE_UPLOAD_FAILED);
    }
  }
}
