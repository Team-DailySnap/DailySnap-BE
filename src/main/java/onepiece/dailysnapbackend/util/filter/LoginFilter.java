package onepiece.dailysnapbackend.util.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import onepiece.dailysnapbackend.object.dto.CustomUserDetails;
import onepiece.dailysnapbackend.object.mongo.RefreshToken;
import onepiece.dailysnapbackend.repository.MemberRepository;
import onepiece.dailysnapbackend.repository.RefreshTokenRepository;
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
  private final MemberRepository memberRepository;
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
    Long memberId = customUserDetails.getMember().getMemberId();    String accessToken = jwtUtil.createAccessToken(customUserDetails);
    String refreshToken = jwtUtil.createRefreshToken(customUserDetails);

    log.info("로그인 성공: Access Token 및 Refresh Token 생성 완료");
    log.info("accessToken = {}", accessToken);
    log.info("refreshToken = {}", refreshToken);


    refreshTokenRepository.deleteByMemberId(memberId);
    refreshTokenRepository.save(
        RefreshToken.builder()
            .refreshTokenId(memberId)
            .token(refreshToken)
            .memberId(memberId)
            .build()
    );
    // 헤더에 accessToken 추가
    response.setHeader("Authorization", "Bearer " + accessToken);

    // 쿠키에 refreshToken 추가
    Cookie cookie = new Cookie("refreshToken", refreshToken);
    cookie.setHttpOnly(true); // HttpOnly 설정
    cookie.setSecure(false); // FIXME: HTTPS 환경에서는 secure 속성 true로 설정 (현재는 HTTP)
    cookie.setPath("/");
    cookie.setMaxAge((int) (jwtUtil.getRefreshExpirationTime() / 1000)); // 쿠키 maxAge는 초 단위 이므로, 밀리초를 1000으로 나눔
    response.addCookie(cookie);

    memberRepository.save(customUserDetails.getMember());
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
