package onepiece.dailysnapbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import java.util.UUID;
import onepiece.dailysnapbackend.object.constants.KeywordCategory;
import onepiece.dailysnapbackend.object.dto.CustomOAuth2User;
import onepiece.dailysnapbackend.object.dto.KeywordRequest;
import onepiece.dailysnapbackend.object.dto.KeywordResponse;
import org.springframework.http.ResponseEntity;

public interface AdminKeywordControllerDocs {

  @Operation(
      summary = "특정 날짜에 제공할 키워드 추가 (관리자 지정)",
      description = """
          ### 요청 파라미터
          - `category` (KeywordCategory, required): 키워드 카테고리 (예: ADMIN_SET)
          - `koreanKeyword` (String, required): 한국어 키워드
          - `englishKeyword` (String, required): 영어 키워드
          - `specifiedDate` (LocalDate, required): 키워드를 제공할 날짜 (YYYY-MM-DD)
          
          ### 응답 데이터
          - `keywordId` (UUID): 생성된 키워드 ID
          - `koreanKeyword` (String): 등록된 한국어 키워드
          - `englishKeyword` (String): 등록된 영어 키워드
          - `keywordCategory` (KeywordCategory): 키워드 카테고리
          - `providedDate` (LocalDate): 제공 날짜 (YYYY-MM-DD)
          - `used` (boolean): 사용 여부 (기본 `false`)
          
          ### 사용 방법
          1. 관리자 권한을 가진 클라이언트에서 Authorization 헤더에 `Bearer {accessToken}`을 포함합니다.  
          2. 아래 JSON 예시처럼 `/api/...` 엔드포인트로 POST 요청을 보냅니다:
             ```json
             {
               "category": "ADMIN_SET",
               "koreanKeyword": "주제",
               "englishKeyword": "topic",
               "specifiedDate": "2025-08-09"
             }
             ```
          3. 서버가 키워드를 저장하고, 생성된 키워드 정보를 반환합니다.
          
          ### 유의 사항
          - `specifiedDate`는 오늘 이후 날짜여야 합니다.
          - 동일한 `koreanKeyword`가 이미 존재할 수 없습니다.
          """
  )
  ResponseEntity<KeywordResponse> addKeyword(
      CustomOAuth2User customOAuth2User,
      KeywordRequest request
  );

  @Operation(
      summary = "특정 키워드 삭제",
      description = """
          ### 요청 파라미터
          - `keyword-id` (UUID, required, path): 삭제할 키워드의 고유 ID
          
          ### 응답 데이터
          - 없음 (빈 본문)
          
          ### 사용 방법
          1. 관리자 권한을 가진 클라이언트에서 Authorization 헤더에 `Bearer {accessToken}`을 포함합니다.  
          2. 아래와 같이 DELETE 요청을 보냅니다:
             ```
             DELETE /admin/keyword/{keyword-id}
             ```
          
          ### 유의 사항
          - 관리자 권한이 필요합니다.
          - 성공 시 HTTP 200 OK 응답이 반환됩니다.
          """
  )
  ResponseEntity<Void> deleteKeyword(
      CustomOAuth2User customOAuth2User,
      UUID keywordId
  );

  @Operation(
      summary = "키워드 자동 생성 및 저장 (OpenAI 연동)",
      description = """
          ### 요청 파라미터
          - `category` (KeywordCategory, required, query): 생성할 키워드 카테고리  
            가능한 값: `SPRING`, `SUMMER`, `AUTUMN`, `WINTER`, `TRAVEL`, `DAILY`, `ABSTRACT`, `RANDOM`
          
          ### 응답 데이터
          - 없음 (빈 본문, HTTP 200 OK)
          
          ### 사용 방법
          1. 관리자 권한을 가진 클라이언트에서 Authorization 헤더에 `Bearer {accessToken}`을 포함합니다.  
          2. 아래와 같이 카테고리 쿼리 파라미터를 포함하여 POST 요청을 보냅니다:
             ```
             POST /api/keyword/list?category=SUMMER
             Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
             ```
          3. 서버가 지정된 카테고리에 대해 OpenAI API를 호출하여  
             - 한국어 키워드 및 대응 영어 번역 키워드를 생성  
             - 중복 및 제공일 순서에 맞춰 DB에 저장  
             작업을 수행한 후 200 OK를 반환합니다.
          
          ### 유의 사항
          - 관리자 권한이 필요합니다.  
          - `category` 값은 `KeywordCategory` enum에 정의된 값만 허용됩니다.  
          - OpenAI API 호출로 인해 지연이 발생할 수 있습니다.  
          - 기존 DB에 이미 존재하는 한글 키워드는 저장에서 제외됩니다.  
          """
  )
  ResponseEntity<Void> createKeywordList(
      CustomOAuth2User customOAuth2User,
      KeywordCategory category
  );
}
