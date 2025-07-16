package onepiece.dailysnapbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import onepiece.dailysnapbackend.object.dto.CustomOAuth2User;
import onepiece.dailysnapbackend.object.dto.KeywordRequest;
import org.springframework.http.ResponseEntity;

public interface AdminControllerDocs {

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
  ResponseEntity<Void> addKeyword(CustomOAuth2User userDetails, KeywordRequest request);

  @Operation(
      summary = "특정 키워드 삭제 (관리자 전용)",
      description = """
            특정 키워드를 삭제합니다. **관리자 권한이 필요합니다.**
            
            ### 요청 파라미터
            - `keyword` (String) → 삭제할 키워드의 ID
            
            ### 반환값
            - `200 OK` → 성공
            """
  )
  ResponseEntity<Void> deleteKeyword(CustomOAuth2User userDetails, String keyword);
}
