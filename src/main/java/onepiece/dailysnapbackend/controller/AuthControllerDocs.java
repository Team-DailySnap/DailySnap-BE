package onepiece.dailysnapbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import onepiece.dailysnapbackend.object.dto.LoginRequest;
import onepiece.dailysnapbackend.object.dto.LoginResponse;
import onepiece.dailysnapbackend.object.dto.ReissueRequest;
import org.springframework.http.ResponseEntity;

public interface AuthControllerDocs {

  @Operation(
      summary = "소셜 로그인",
      description = """
          ### 요청 파라미터
          - `socialPlatform` (SocialPlatform, required): 소셜 플랫폼 종류 (KAKAO, GOOGLE)
          - `username` (String, required): 사용자 이메일 (unique)
          - `birth` (String, optional): 사용자 생년월일 (형식: YYYY-MM-DD)
          - `nickname` (String, optional): 사용자 닉네임 (unique)
          
          ### 응답 데이터
          - `accessToken` (String): 발급된 액세스 토큰 (JWT)
          - `refreshToken` (String): 발급된 리프레시 토큰 (JWT)
          
          ### 사용 방법
          1. 클라이언트에서 아래 JSON 예시처럼 서버로 POST 요청을 보냅니다.
             ```json
             {
               "socialPlatform": "KAKAO",
               "username": "user@example.com",
               "birth": "1990-01-01",
               "nickname": "daily_snap_user"
             }
             ```
          2. 서버가 회원 정보를 조회(또는 신규 저장) 후 JWT를 발급하여 반환합니다.
          
          ### 유의 사항
          - `socialPlatform`과 `username` 필드는 필수입니다.
          - `socialPlatform` 값은 `SocialPlatform` enum에 정의된 값만 허용됩니다.
          - 이미 가입된 이메일(`username`)로 요청할 경우 기존 계정으로 로그인 처리됩니다.
          - `birth`, `nickname`은 선택 필드이며, 미전달 시 기본값(빈 문자열 또는 null)으로 처리됩니다.
          """
  )
  ResponseEntity<LoginResponse> login(LoginRequest request);

  @Operation(
      summary = "액세스 토큰 재발급",
      description = """
          ### 요청 파라미터
          - `refreshToken` (String, required): 재발급에 사용할 리프레시 토큰 (JWT)
          
          ### 응답 데이터
          - `accessToken` (String): 새로 발급된 액세스 토큰 (JWT)
          - `refreshToken` (String): 새로 발급된 리프레시 토큰 (JWT)
          
          ### 사용 방법
          1. 클라이언트에서 아래 JSON 예시처럼 서버로 POST 요청을 보냅니다.
             ```json
             {
               "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
             }
             ```
          2. 서버가 전달된 리프레시 토큰의 유효성을 검사한 후, 새로운 액세스 토큰과 리프레시 토큰을 생성하여 반환합니다.
          
          ### 유의 사항
          - `refreshToken` 필드는 필수이며, 유효한 토큰이어야 합니다.
          - 클라이언트는 반환된 `accessToken`을 Authorization 헤더에 담아 API 호출 시 사용해야 합니다.
          """
  )
  ResponseEntity<LoginResponse> reissue(ReissueRequest request);
}
