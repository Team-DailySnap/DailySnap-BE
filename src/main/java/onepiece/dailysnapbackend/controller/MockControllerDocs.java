package onepiece.dailysnapbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import onepiece.dailysnapbackend.object.dto.LoginResponse;
import onepiece.dailysnapbackend.object.dto.MockLoginRequest;
import org.springframework.http.ResponseEntity;

public interface MockControllerDocs {

  @Operation(
      summary = "모의 회원 생성 및 로그인",
      description = """
          ### 요청 파라미터
          - `username` (String, optional): 생성할 회원 이메일. 미전달 또는 빈 문자열 시 랜덤 이메일 생성  
          - `nickname` (String, optional): 생성할 회원 닉네임. 미전달 또는 빈 문자열 시 랜덤 닉네임 생성  
          - `role` (Role, optional): 부여할 권한 (`ROLE_USER`, `ROLE_ADMIN`). 미전달 시 `ROLE_USER` 설정  
          
          ### 응답 데이터
          - `accessToken` (String): 발급된 액세스 토큰 (JWT)  
          - `refreshToken` (String): 발급된 리프레시 토큰 (JWT)  
          
          ### 사용 방법
          1. 인증 없이 사용할 수 있는 개발자 전용 엔드포인트입니다.  
          2. 클라이언트에서 아래 JSON 예시처럼 `/mock/member`로 POST 요청을 보냅니다:
             ```json
             {
               "username": "test@example.com",
               "nickname": "tester",
               "role": "ROLE_ADMIN"
             }
             ```
          3. 서버가 가상의 회원을 생성하여 JWT를 발급하고, `LoginResponse`를 반환합니다.
          
          ### 유의 사항
          - 실제 운영 환경에서는 사용 금지하며, 개발 및 테스트 목적으로만 사용해야 합니다.  
          - `username` 또는 `nickname`에 빈 문자열을 전달하면 Faker를 사용해 랜덤 값이 생성됩니다.  
          - `role`에 허용되지 않는 값 전달 시 서버 측에서 처리되지 않으므로, enum 값만 사용해야 합니다.  
          """
  )
  ResponseEntity<LoginResponse> createMockMember(
      MockLoginRequest request
  );

}
