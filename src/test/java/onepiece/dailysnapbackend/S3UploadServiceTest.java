package onepiece.dailysnapbackend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import onepiece.dailysnapbackend.object.dto.FileResponse;
import onepiece.dailysnapbackend.service.S3UploadService;
import onepiece.dailysnapbackend.util.FileUtil;
import onepiece.dailysnapbackend.util.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

@ExtendWith(MockitoExtension.class)
class S3UploadServiceTest {

  @Mock
  private S3Client s3Client;

  @InjectMocks
  private S3UploadService s3UploadService;

  private final String bucketName = "test-bucket";

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(s3UploadService, "bucketName", bucketName);
  }

  @Test
  void uploadSingleFile_success_webpConversion() throws IOException {
    try (MockedStatic<FileUtil> mockedFileUtil = mockStatic(FileUtil.class)) {
      // Given
      MockMultipartFile mockFile = new MockMultipartFile(
          "file", "test.jpg", "image/jpeg", "test content".getBytes()
      );
      FileResponse fileResponse = FileResponse.builder()
          .fileName("converted.webp")
          .bytes(new byte[]{1, 2, 3})
          .build();
      mockedFileUtil.when(() -> FileUtil.convertToWebp(mockFile)).thenReturn(fileResponse);
      when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
          .thenReturn(PutObjectResponse.builder().build());

      // When
      String result = s3UploadService.upload(mockFile);

      // Then
      assertNotNull(result);
      assertTrue(result.startsWith("https://" + bucketName + ".s3.amazonaws.com/"));
      verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }
  }

  @Test
  void uploadSingleFile_success_fallbackToOriginal() throws IOException {
    try (MockedStatic<FileUtil> mockedFileUtil = mockStatic(FileUtil.class)) {
      // Given
      MockMultipartFile mockFile = new MockMultipartFile(
          "file", "test.jpg", "image/jpeg", "test content".getBytes()
      );
      mockedFileUtil.when(() -> FileUtil.convertToWebp(mockFile))
          .thenThrow(new IOException("Conversion failed"));
      mockedFileUtil.when(() -> FileUtil.generateFileName(mockFile.getOriginalFilename()))
          .thenReturn("generated-test.jpg");
      when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
          .thenReturn(PutObjectResponse.builder().build());

      // When
      String result = s3UploadService.upload(mockFile);

      // Then
      assertNotNull(result);
      assertTrue(result.contains("generated-test.jpg"));
      verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }
  }

  @Test
  void uploadSingleFile_invalidMimeType_throwsException() {
    // Given
    MockMultipartFile mockFile = new MockMultipartFile(
        "file", "test.exe", "application/octet-stream", "test content".getBytes()
    );

    // When & Then
    CustomException exception = assertThrows(CustomException.class,
        () -> s3UploadService.upload(mockFile));
    assertEquals("INVALID_FILE_TYPE", exception.getErrorCode().name());
    verify(s3Client, never()).putObject(any(PutObjectRequest.class), any(RequestBody.class));
  }

  @Test
  void uploadMultipleFiles_success() throws IOException {
    try (MockedStatic<FileUtil> mockedFileUtil = mockStatic(FileUtil.class)) {
      // Given
      MockMultipartFile mockFile = new MockMultipartFile(
          "file", "test.jpg", "image/jpeg", "test content".getBytes()
      );
      FileResponse fileResponse = FileResponse.builder()
          .fileName("converted.webp")
          .bytes(new byte[]{1, 2, 3})
          .build();
      mockedFileUtil.when(() -> FileUtil.convertToWebp(mockFile)).thenReturn(fileResponse);
      when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
          .thenReturn(PutObjectResponse.builder().build());

      // When
      List<String> results = s3UploadService.upload(Collections.singletonList(mockFile));

      // Then
      assertEquals(1, results.size());
      assertTrue(results.get(0).startsWith("https://" + bucketName + ".s3.amazonaws.com/"));
      verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }
  }

  @Test
  void uploadToS3_failure_throwsException() throws IOException {
    try (MockedStatic<FileUtil> mockedFileUtil = mockStatic(FileUtil.class)) {
      // Given
      MockMultipartFile mockFile = new MockMultipartFile(
          "file", "test.jpg", "image/jpeg", "test content".getBytes()
      );
      FileResponse fileResponse = FileResponse.builder()
          .fileName("converted.webp")
          .bytes(new byte[]{1, 2, 3})
          .build();
      mockedFileUtil.when(() -> FileUtil.convertToWebp(mockFile)).thenReturn(fileResponse);
      when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
          .thenThrow(new RuntimeException("S3 Error"));

      // When & Then
      CustomException exception = assertThrows(CustomException.class,
          () -> s3UploadService.upload(mockFile));
      assertEquals("FILE_UPLOAD_FAILED", exception.getErrorCode().name());
    }
  }
}
