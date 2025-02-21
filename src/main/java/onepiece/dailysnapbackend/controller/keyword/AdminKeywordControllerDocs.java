package onepiece.dailysnapbackend.controller.keyword;

import io.swagger.v3.oas.annotations.Operation;
import java.util.UUID;
import onepiece.dailysnapbackend.object.constants.KeywordCategory;
import onepiece.dailysnapbackend.object.dto.CustomUserDetails;
import onepiece.dailysnapbackend.object.dto.KeywordRequest;
import org.springframework.http.ResponseEntity;

public interface AdminKeywordControllerDocs {

  @Operation(
      summary = "키워드 자동 생성 (관리자 전용)",
      description = """
          
          특정 카테고리의 키워드가 부족할 경우 OpenAI API를 사용하여 자동 생성합니다.  
          **관리자 권한이 필요합니다.**
          
          ### 요청 파라미터
          - `category` (KeywordCategory) → 키워드 카테고리 (예: 계절, 여행, 일상 등)
          
          ### 반환값
          - `200 OK` → 성공
          
          """
  )
  ResponseEntity<Void> generateKeywords(CustomUserDetails userDetails, KeywordCategory category);

  @Operation(
      summary = "특정 날짜에 제공할 키워드 추가 (관리자 전용)",
      description = """
          
          특정 날짜에 제공할 키워드를 추가합니다. **관리자 권한이 필요합니다.**
          
          ### 요청 파라미터
          - `request` (KeywordRequest) → 추가할 키워드 정보
          
          ### 반환값
          - `200 OK` → 성공
          
          """
  )
  ResponseEntity<Void> addAdminKeyword(CustomUserDetails userDetails, KeywordRequest request);

  @Operation(
      summary = "특정 키워드 삭제 (관리자 전용)",
      description = """
          
          특정 키워드를 삭제합니다. **관리자 권한이 필요합니다.**
          
          ### 요청 파라미터
          - `id` (UUID) → 삭제할 키워드의 ID
          
          ### 반환값
          - `200 OK` → 성공
          
          """
  )
  ResponseEntity<Void> deleteKeyword(CustomUserDetails userDetails, UUID id);
}
