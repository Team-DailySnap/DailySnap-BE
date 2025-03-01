package onepiece.dailysnapbackend.util.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import onepiece.dailysnapbackend.object.dto.CustomUserDetails;
import onepiece.dailysnapbackend.util.JwtUtil;
import onepiece.dailysnapbackend.util.exception.CustomException;
import onepiece.dailysnapbackend.util.exception.ErrorCode;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomLogoutHandler implements LogoutHandler {

  private final JwtUtil jwtUtil;
  private final RedisTemplate<String, String> redisTemplate;
  private final ObjectMapper objectMapper;

  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
    try {
      BufferedReader reader = request.getReader();
      StringBuilder stringBuilder = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
        stringBuilder.append(line);
      }

      String requestBody = stringBuilder.toString();
      log.info("로그아웃 요청 바디: {}", requestBody);

      JsonNode jsonNode = objectMapper.readTree(requestBody);
      String accessToken = jsonNode.path("accessToken").asText();
      String refreshToken = jsonNode.path("refreshToken").asText();

      if (authentication == null) {
        log.warn("Authentication 객체가 null입니다. 헤더에서 토큰 검증이 실패했을 수 있습니다.");
        // 헤더에서 토큰을 직접 가져와 처리하는 대체 로직 (임시)
        String authHeader = request.getHeader("Authorization");
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
          accessToken = authHeader.substring(7);
        }
      }

      CustomUserDetails userDetails = authentication != null
          ? (CustomUserDetails) authentication.getPrincipal()
          : null;
      String memberId = userDetails != null ? userDetails.getMemberId() : "unknown";
      String redisKey = "refreshToken:" + memberId;

      // access token 블랙리스트 추가
      if (StringUtils.hasText(accessToken) && jwtUtil.validateToken(accessToken)) {
        jwtUtil.addAccessTokenToBlacklist(accessToken);
      }
      // refresh token 삭제
      if (StringUtils.hasText(refreshToken)) {
        redisTemplate.delete(redisKey);
      }

      log.info("로그아웃 성공: userId={}", memberId);
    } catch (IOException e) {
      log.error("요청 바디 읽기 오류: {}", e.getMessage());
      throw new CustomException(ErrorCode.INVALID_REQUEST);
    }
  }
}
