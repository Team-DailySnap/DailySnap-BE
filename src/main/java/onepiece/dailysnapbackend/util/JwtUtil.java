package onepiece.dailysnapbackend.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import onepiece.dailysnapbackend.object.constants.SocialPlatform;
import onepiece.dailysnapbackend.object.dto.CustomOAuth2User;
import onepiece.dailysnapbackend.service.CustomOAuth2UserService;
import onepiece.dailysnapbackend.util.exception.CustomException;
import onepiece.dailysnapbackend.util.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtUtil {

  private final CustomOAuth2UserService customOAuth2UserService;
  private final RedisTemplate<String, String> redisTemplate;

  @Value("${jwt.secret-key}")
  private String secretKey;

  @Value("${jwt.access-exp-time}")
  private Long accessTokenExpTime; // AccessToken 만료 시간

  @Value("${jwt.refresh-exp-time}")
  private Long refreshTokenExpTime; // RefreshToken 만료 시간

  @Value("${jwt.issuer}")
  private String issuer; // JWT 발급자

  private static final String ACCESS_CATEGORY = "access";
  private static final String REFRESH_CATEGORY = "refresh";
  private static final String BLACKLIST_KEY = "BL:";
  private static final String BLACKLIST_VALUE = "blacklist";

  // 토큰에서 username 파싱
  public String getUsername(String token) {
    return Jwts.parser()
        .verifyWith(getSignKey())
        .build()
        .parseSignedClaims(token)
        .getPayload()
        .get("username", String.class);
  }

  // 토큰에서 role 파싱
  public String getRole(String token) {
    return Jwts.parser()
        .verifyWith(getSignKey())
        .build()
        .parseSignedClaims(token)
        .getPayload()
        .get("role", String.class);
  }

  // 토큰에서 provider 파싱
  public String getProvider(String token) {
    return Jwts.parser()
        .verifyWith(getSignKey())
        .build()
        .parseSignedClaims(token)
        .getPayload()
        .get("provider", String.class);
  }

  // 토큰 만료 여부 확인
  public Boolean isExpired(String token) {
    return Jwts.parser()
        .verifyWith(getSignKey())
        .build()
        .parseSignedClaims(token)
        .getPayload()
        .getExpiration()
        .before(new Date());
  }

  // Access/Refresh 토큰 여부
  public String getCategory(String token) {
    return Jwts.parser()
        .verifyWith(getSignKey())
        .build()
        .parseSignedClaims(token)
        .getPayload()
        .get("category", String.class);
  }

  /**
   * AccessToken 생성
   *
   * @param customOAuth2User
   * @return
   */
  public String createAccessToken(CustomOAuth2User customOAuth2User) {
    log.info("엑세스 토큰 생성 중: 회원: {}", customOAuth2User.getUsername());
    return createToken(ACCESS_CATEGORY, customOAuth2User, accessTokenExpTime);
  }

  /**
   * RefreshToken 생성
   *
   * @param customOAuth2User
   * @return
   */
  public String createRefreshToken(CustomOAuth2User customOAuth2User) {
    log.info("리프래시 토큰 생성 중: 회원: {}", customOAuth2User.getUsername());
    return createToken(REFRESH_CATEGORY, customOAuth2User, refreshTokenExpTime);
  }

  /**
   * JWT 토큰 생성 메서드
   *
   * @param customOAuth2User 회원 상세 정보
   * @param expiredAt        만료 시간
   * @return 생성된 JWT 토큰
   */
  private String createToken(String category, CustomOAuth2User customOAuth2User, Long expiredAt) {

    return Jwts.builder()
        .subject(customOAuth2User.getUsername())
        .claim("category", category)
        .claim("username", customOAuth2User.getUsername())
        .claim("role", customOAuth2User.getMember().getRole())
        .claim("provider", customOAuth2User.getMember().getSocialPlatform())
        .issuer(issuer)
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + expiredAt))
        .signWith(getSignKey())
        .compact();
  }

  /**
   * JWT 토큰 유효성 검사
   *
   * @param token 검증할 JWT 토큰
   * @return 유효 여부
   */
  public boolean validateToken(String token) throws ExpiredJwtException {
    try {
      Jwts.parser()
          .verifyWith(getSignKey())
          .build()
          .parseSignedClaims(token);
      if (isTokenBlacklisted(token)) {
        log.error("액세스 토큰이 블랙리스트에 등록되어 있습니다. accessToken={}", token);
        throw new CustomException(ErrorCode.TOKEN_BLACKLISTED);
      }
      log.info("JWT 토큰이 유효합니다.");
      return true;
    } catch (ExpiredJwtException e) {
      log.warn("JWT 토큰이 만료되었습니다: {}", e.getMessage());
      throw e; // 만료된 토큰 예외를 호출한 쪽으로 전달
    } catch (UnsupportedJwtException e) {
      log.warn("지원되지 않는 JWT 토큰입니다: {}", e.getMessage());
    } catch (MalformedJwtException e) {
      log.warn("형식이 잘못된 JWT 토큰입니다: {}", e.getMessage());
    } catch (SignatureException e) {
      log.warn("JWT 서명이 유효하지 않습니다: {}", e.getMessage());
    } catch (IllegalArgumentException e) {
      log.warn("JWT 토큰이 비어있거나 null입니다: {}", e.getMessage());
    }
    return false;
  }

  /**
   * JWT 서명에 사용할 키 생성
   *
   * @return SecretKey 객체
   */
  private SecretKey getSignKey() {
    try {
      // Base64 문자열로부터 SecretKey를 생성
      byte[] keyBytes = Decoders.BASE64.decode(secretKey);
      return Keys.hmacShaKeyFor(keyBytes);
    } catch (IllegalArgumentException e) {
      log.error("비밀 키 생성 실패: {}", e.getMessage());
      throw e; // 예외 재발생
    }
  }

  /**
   * JWT 토큰에서 클레임 (Claims) 추출
   *
   * @param token JWT 토큰
   * @return 추출된 클레임
   */
  public Claims getClaims(String token) {
    return Jwts.parser()
        .verifyWith(getSignKey())
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }

  /**
   * 액세스 토큰의 남은 만료 시간 반환
   *
   * @param token JWT 액세스 토큰
   * @return 남은 만료 시간 (밀리초)
   */
  public long getAccessTokenExpirationTime(String token) {
    Claims claims = getClaims(token);
    Date expirationDate = claims.getExpiration();
    long remainingTime = expirationDate.getTime() - System.currentTimeMillis();
    return Math.max(remainingTime, 0);
  }

  /**
   * 리프레시 토큰 만료 시간 반환
   *
   * @return 리프레시 토큰 만료 시간 (밀리초 단위)
   */
  public long getRefreshExpirationTime() {
    return refreshTokenExpTime;
  }

  /**
   * 리프레시 토큰 만료 날짜 반환
   *
   * @return 리프레시 토큰 만료 날짜
   */
  public LocalDateTime getRefreshExpiryDate() {
    return LocalDateTime.now().plusSeconds(refreshTokenExpTime / 1000);
  }

  /**
   * JWT 토큰에서 Authentication 객체 생성
   *
   * @param token JWT 토큰
   * @return Authentication 객체
   */
  public Authentication getAuthentication(String token) {
    Claims claims = getClaims(token);
    String username = claims.getSubject();
    String provider = claims.get("provider", String.class);
    SocialPlatform socialPlatform = SocialPlatform.valueOf(provider);
    log.info("JWT에서 인증정보 파싱: username={}, socialPlatform={}", username, socialPlatform);
    CustomOAuth2User userDetails = customOAuth2UserService.loadUserByUsernameAndSocialPlatform(username,
        socialPlatform);
    return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
  }

  // access token 블랙리스트에 등록
  public void addAccessTokenToBlacklist(String accessToken) {
    long expiration = getAccessTokenExpirationTime(accessToken);
    redisTemplate.opsForValue().set(
        BLACKLIST_KEY + accessToken,
        BLACKLIST_VALUE,
        expiration,
        TimeUnit.MILLISECONDS
    );
    log.info("블랙리스트에 액세스 토큰을 추가했습니다. accessToken={}", accessToken);
  }

  // 해당 토큰이 블랙리스트에 있는지 여부
  public Boolean isTokenBlacklisted(String token) {
    String key = BLACKLIST_KEY + token;
    return redisTemplate.hasKey(key);
  }
}
