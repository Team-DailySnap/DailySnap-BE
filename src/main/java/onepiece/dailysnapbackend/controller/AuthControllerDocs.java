package onepiece.dailysnapbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import onepiece.dailysnapbackend.object.dto.SignInRequest;
import onepiece.dailysnapbackend.object.dto.SignUpRequest;
import org.springframework.http.ResponseEntity;

public interface AuthControllerDocs {

  @Operation(
      summary = "회원가입",
      description = """
                    
                    이 API는 인증이 필요하지 않습니다.

                    ### 요청 파라미터
                    - **username** (String): 사용자 이메일 (중복 불가)
                    - **password** (String): 사용자 비밀번호
                    - **nickname** (String): 사용자 닉네임 (중복 불가)
                    
                    ### 유의사항
                    - `username`과 `nickname`은 고유해야 합니다.

                    """
  )
  ResponseEntity<Void> signUp(SignUpRequest request);

  @Operation(
      summary = "로그인",
      description = """
                    
                    이 API는 인증이 필요하지 않습니다.

                    ### 요청 파라미터
                    - **username** (String): 사용자 이메일
                    - **password** (String): 사용자 비밀번호
                    
                    """
  )
  ResponseEntity<Void> signIn(SignInRequest request);

  @Operation(
      summary = "accessToken 재발급 요청",
      description = """
                  
                  이 API는 인증이 필요하지 않습니다.
                  요청 바디에 포함된 RefreshToken만으로 새로운 AccessToken을 발급할 수 있습니다.

                  ### 요청 파라미터
                  - **Request Body**: JSON 형태의 요청 바디에 포함된 리프레시 토큰
                      - **Key**: `refreshToken`
                      - **Value**: `리프레시 토큰 값`
                  
                  **요청 예시:**
                  ```json
                  {
                    "refreshToken": "your-refresh-token-value"
                  }
                  ```
                  
                  ### 반환값
                  - 새로운 액세스 토큰은 **HTTP 헤더 (`Authorization`)**에 포함되어 반환됩니다.
                  
                  **반환 헤더 예시:**
                  ```
                  Authorization: Bearer your-new-access-token
                  ```
                  
                  ### 유의사항
                  - 이 API는 리프레시 토큰의 유효성을 검증한 후 새로운 액세스 토큰을 발급합니다.
                  - 리프레시 토큰이 유효하지 않거나 만료되었을 경우, 재로그인이 필요합니다.
                  
                  **응답 코드:**
                  - **200 OK**: 새로운 액세스 토큰 발급 성공 (헤더에 포함됨)
                  - **401 Unauthorized**: 리프레시 토큰이 유효하지 않거나 만료됨
                  - **400 Bad Request**: 요청 바디에 리프레시 토큰이 없음
                  
                  **추가 설명:**
                  - 이 API는 `HttpServletRequest`의 요청 바디에서 `refreshToken`을 추출하여 처리합니다.
                  - 클라이언트는 `application/json` 형식으로 요청해야 합니다.
                  """
  )
  ResponseEntity<Void> reissue(HttpServletRequest request, HttpServletResponse response);

}