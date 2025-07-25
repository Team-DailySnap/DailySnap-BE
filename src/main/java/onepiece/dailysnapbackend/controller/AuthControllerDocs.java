package onepiece.dailysnapbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import onepiece.dailysnapbackend.object.dto.SignInRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface AuthControllerDocs {

  @Operation(
      summary = "소셜 로그인",
      description = """
        클라이언트에서 받은 accessToken을 이용하여 소셜 로그인 처리 후 JWT 토큰을 발급합니다.
        
        ### 요청 형식
        - Content-Type: application/json

        ### 요청 바디 예시
        ```json
        {
          "provider": "KAKAO",
          "username": "example@naver.com",
          "birth": "2004-01-01",
          "nickname": "daily_snap_user"
        }
        ```

        ### 응답
        - `200 OK`: 로그인 또는 회원가입 성공

        ### 응답 형식
        - `Authorization` 헤더에 accessToken 포함
        - 응답 바디에 refreshToken 포함

        ### 응답 예시
        #### 헤더:
        - `Authorization: Bearer your-access-token`
        
        #### 바디:
        ```json
        {
          "refreshToken": "your-refresh-token"
        }
        ```
        """
  )
  ResponseEntity<Void> signIn(@Valid @RequestBody SignInRequest request, HttpServletResponse response);

  @Operation(
      summary = "accessToken 재발급 요청",
      description = """
          
          이 API는 인증이 필요하지 않습니다.
          요청 쿠키에 포함된 RefreshToken만으로 새로운 AccessToken을 발급할 수 있습니다.
          
          ### 요청 파라미터
          - **Cookie**: JSON 형태의 요청 바디에 포함된 리프레시 토큰
              - **Name**: `refresh_token`
              - **Value**: `리프레시 토큰 값`

          ### 반환값
          - 새로운 액세스 토큰은 **JSON 응답 바디**에 포함되어 반환됩니다.
          
          **반환 헤더 예시:**
          ```
          json
          {
            "accessToken": "your-new-access-token"
          }
          ```
          
          ### 유의사항
          - 이 API는 리프레시 토큰의 유효성을 검증한 후 새로운 액세스 토큰을 발급합니다.
          - 리프레시 토큰이 유효하지 않거나 만료되었을 경우, 재로그인이 필요합니다.
          
          **응답 코드:**
          - **200 OK**: 새로운 액세스 토큰 발급 성공 (헤더에 포함됨)
          - **401 Unauthorized**: 리프레시 토큰이 유효하지 않거나 만료됨
          - **400 Bad Request**: 요청에 쿠키가 없거나 리프레시 토큰이 없음
          
          **추가 설명:**
          - 이 API는 `HttpServletRequest`의 요청 쿠키에서 `refreshToken`을 추출하여 처리합니다.
          - 클라이언트는 `application/json` 형식으로 요청해야 합니다.
          """
  )
  ResponseEntity<Void> reissue(HttpServletRequest request, HttpServletResponse response);
}
