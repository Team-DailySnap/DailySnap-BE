package onepiece.dailysnapbackend.util.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import onepiece.dailysnapbackend.object.dto.ApiResponse;
import onepiece.dailysnapbackend.object.dto.CustomUserDetails;
import onepiece.dailysnapbackend.object.mongo.RefreshToken;
import onepiece.dailysnapbackend.object.postgres.Member;
import onepiece.dailysnapbackend.repository.mongo.RefreshTokenRepository;
import onepiece.dailysnapbackend.util.JwtUtil;
import onepiece.dailysnapbackend.util.exception.CustomException;
import onepiece.dailysnapbackend.util.exception.ErrorCode;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

  private final JwtUtil jwtUtil;
  private final AuthenticationManager authenticationManager;
  private final RefreshTokenRepository refreshTokenRepository;

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {

    // 클라이언트 요청에서 username, password 추출
    String username = obtainUsername(request);
    String password = obtainPassword(request);

    UsernamePasswordAuthenticationToken authToken =
        new UsernamePasswordAuthenticationToken(username, password, null);

    return authenticationManager.authenticate(authToken);
  }

  // 로그인 성공 (JWT 발급)
  @Override
  protected void successfulAuthentication(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain,
      Authentication authentication) throws IOException {

    // CustomUserDetails
    CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
    Member member = customUserDetails.getMember();
    String accessToken = jwtUtil.createAccessToken(customUserDetails);
    String refreshToken = jwtUtil.createRefreshToken(customUserDetails);

    log.info("로그인 성공: 엑세스 토큰 및 리프레시 토큰 생성");
    log.info("accessToken = {}", accessToken);
    log.info("refreshToken = {}", refreshToken);

    // Refresh Token 저장
    refreshTokenRepository.save(RefreshToken.builder()
        .token(refreshToken)
        .memberId(member.getMemberId())
        .expiryDate(jwtUtil.getRefreshExpiryDate())
        .build());

    // JSON 응답
    Map<String, String> tokenMap = new HashMap<>();
    tokenMap.put("accessToken", accessToken);
    tokenMap.put("refreshToken", refreshToken);

    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    new ObjectMapper().writeValue(response.getWriter(), ApiResponse.success(tokenMap));
  }

  // 로그인 실패
  @Override
  protected void unsuccessfulAuthentication(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException failed) {
    log.error("로그인 실패");
    throw new CustomException(ErrorCode.UNAUTHORIZED);
  }
}
