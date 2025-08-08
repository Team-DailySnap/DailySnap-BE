package onepiece.dailysnapbackend.util.config;

import java.util.Arrays;
import java.util.List;

/**
 * Security 관련 URL 상수 관리
 */
public class SecurityUrls {

  /**
   * 인증을 생략할 URL 패턴 목록
   */
  public static final List<String> AUTH_WHITELIST = Arrays.asList(
      // API
      "/api/auth/sign-up", // 회원가입
      "/login", // 로그인
      "/api/auth/reissue", // 액세스 토큰 재발급
      "/",

      // Swagger
      "/docs/**", // Swagger UI
      "/v3/api-docs/**" // Swagger API 문서

  );

  /**
   * 관리자 권한이 필요한 URL 패턴 목록
   */
  public static final List<String> ADMIN_PATHS = Arrays.asList(

  );

}
