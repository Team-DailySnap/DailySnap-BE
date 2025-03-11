package onepiece.dailysnapbackend.util.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import onepiece.dailysnapbackend.object.dto.CustomUserDetails;
import onepiece.dailysnapbackend.util.JwtUtil;
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
      JsonNode jsonNode = objectMapper.readTree(request.getInputStream());
      String accessToken = jsonNode.path("accessToken").asText();
      String refreshToken = jsonNode.path("refreshToken").asText();

      log.info("로그아웃 요청 바디: accessToken={}, refreshToken={}", accessToken, refreshToken);

      CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
      String memberId = userDetails.getMemberId();
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
    } catch (Exception e) {
      log.error("요청 바디 읽기 오류: {}", e.getMessage());
      throw new RuntimeException("로그아웃 요청 처리 중 오류 발생");
    }
  }
}
