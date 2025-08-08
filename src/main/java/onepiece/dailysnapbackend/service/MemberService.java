package onepiece.dailysnapbackend.service;

import static onepiece.dailysnapbackend.util.exception.ErrorCode.COOKIES_NOT_FOUND;
import static onepiece.dailysnapbackend.util.exception.ErrorCode.REFRESH_TOKEN_EMPTY;
import static onepiece.dailysnapbackend.util.exception.ErrorCode.REFRESH_TOKEN_NOT_FOUND;

import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import onepiece.dailysnapbackend.object.constants.AccountStatus;
import onepiece.dailysnapbackend.object.constants.Role;
import onepiece.dailysnapbackend.object.dto.CustomOAuth2User;
import onepiece.dailysnapbackend.object.dto.LoginResponse;
import onepiece.dailysnapbackend.object.dto.LoginRequest;
import onepiece.dailysnapbackend.object.postgres.Member;
import onepiece.dailysnapbackend.repository.postgres.MemberRepository;
import onepiece.dailysnapbackend.util.JwtUtil;
import onepiece.dailysnapbackend.util.exception.CustomException;
import onepiece.dailysnapbackend.util.exception.ErrorCode;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

  private final MemberRepository memberRepository;
  private final JwtUtil jwtUtil;

  @Transactional
  public LoginResponse socialSignIn(LoginRequest request) {

    // DB에서 회원 조회
    Member member = memberRepository.findByUsername(request.getUsername())
        .orElseGet(() -> memberRepository.save(Member.builder()
            .username(request.getUsername())
            .socialPlatform(request.getSocialPlatform())
            .nickname(request.getNickname())
            .role(Role.ROLE_USER)
            .accountStatus(AccountStatus.ACTIVE_ACCOUNT)
            .dailyUploadCount(0)
            .firstLogin(true)
            .paid(false)
            .build()
        ));

    log.info("소셜 로그인 성공: username={}", request.getUsername());

    CustomOAuth2User userDetails = new CustomOAuth2User(member, Map.of());
    String accessToken = jwtUtil.createAccessToken(userDetails);
    String refreshToken = jwtUtil.createRefreshToken(userDetails);

    return new LoginResponse(accessToken, refreshToken);
  }

  // 리프레시 토큰을 통해 액세스 토큰 재발급
  @Transactional
  public void reissue(HttpServletRequest request, HttpServletResponse response) {

    // 리프레시 토큰 추출
    String refresh = extractRefreshTokenFromRequest(request);

    // 만료 여부 확인
    jwtUtil.validateToken(refresh);

    // 토큰이 유효한지 확인 (발급 시 페이로드에 명시)
    String category = jwtUtil.getCategory(refresh);
    if (!category.equals("refresh")) {
      log.error("유효하지 않은 토큰입니다. 요청된 토큰 카테고리: {}", category);
      throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
    }

    // 새 액세스 토큰 발급
    CustomOAuth2User customOAuth2User = (CustomOAuth2User) jwtUtil.getAuthentication(refresh).getPrincipal();
    String newAccess = jwtUtil.createAccessToken(customOAuth2User);

    // JSON 응답 바디로 액세스 토큰 반환
    response.setContentType("application/json");
    try {
      response.getWriter().write(String.format("{\"accessToken\": \"%s\"}", newAccess));
    } catch (IOException e) {
      log.error("액세스 토큰 응답 작성 중 오류 발생", e);
      throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    log.info("access token 재발급 성공: accessToken={}", newAccess);
  }

  // 리프레시 토큰 추출
  private String extractRefreshTokenFromRequest(HttpServletRequest request) {
    Cookie[] cookies = request.getCookies();
    if (cookies == null) {
      log.error("쿠키가 존재하지 않습니다.");
      throw new CustomException(COOKIES_NOT_FOUND);
    }

    for (Cookie cookie : cookies) {
      if ("refresh_token".equals(cookie.getName())) {
        String refreshToken = cookie.getValue();
        if (StringUtils.isBlank(refreshToken)) {
          log.error("리프레시 토큰이 비어있습니다.");
          throw new CustomException(REFRESH_TOKEN_EMPTY);
        }
        log.info("refresh token 추출 성공: {}", refreshToken);
        return refreshToken;
      }
    }

    log.error("요청 쿠키에 refresh token 이 없습니다.");
    throw new CustomException(REFRESH_TOKEN_NOT_FOUND);
  }
}
