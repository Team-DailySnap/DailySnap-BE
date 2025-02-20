package onepiece.dailysnapbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import java.util.UUID;
import onepiece.dailysnapbackend.object.dto.CustomUserDetails;
import onepiece.dailysnapbackend.object.dto.PhotoPostRequest;
import org.springframework.http.ResponseEntity;

public interface PhotoControllerDocs {

  @Operation(
      summary = "사진 업로드",
      description = """
          
          이 API는 인증이 필요합니다
          
          ### 요청 파라미터
          - **keywordId** (UUID): 키워드 id
          - **imageUrl** (MultipartFile): 이미지 url
          - **content** (String): 사진 설명 (필수X)
          - **location** (String): 위치 (필수X)
          
          ### 반환값
          - 업로드된 사진의 id 반환
          
          """
  )
  ResponseEntity<UUID> uploadPhoto
      (CustomUserDetails userDetails, PhotoPostRequest request);
}