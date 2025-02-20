package onepiece.dailysnapbackend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import java.io.BufferedReader;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import onepiece.dailysnapbackend.object.constants.AccountStatus;
import onepiece.dailysnapbackend.object.constants.Role;
import onepiece.dailysnapbackend.object.dto.CustomUserDetails;
import onepiece.dailysnapbackend.object.dto.SignUpRequest;
import onepiece.dailysnapbackend.object.postgres.Member;
import onepiece.dailysnapbackend.repository.postgres.MemberRepository;
import onepiece.dailysnapbackend.util.JwtUtil;
import onepiece.dailysnapbackend.util.exception.CustomException;
import onepiece.dailysnapbackend.util.exception.ErrorCode;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

  private final MemberRepository memberRepository;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;
  private final JwtUtil jwtUtil;

  // 회원가입
  @Transactional
  public void signUp(SignUpRequest request) {

    // 이메일 중복 체크
    if (memberRepository.existsByUsername(request.getUsername())) {
      log.error("이미 가입된 이메일입니다: {}", request.getUsername());
      throw new CustomException(ErrorCode.DUPLICATE_USERNAME);
    }

    memberRepository.save(Member.builder()
        .username(request.getUsername())
        .password(bCryptPasswordEncoder.encode(request.getPassword()))
        .nickname(request.getNickname())
        .birth(request.getBirth())
        .role(Role.ROLE_USER)
        .accountStatus(AccountStatus.ACTIVE_ACCOUNT)
        .dailyUploadCount(0)
        .build()
    );
    log.info("회원가입 성공: username={}", request.getUsername());
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
    CustomUserDetails customUserDetails = (CustomUserDetails) jwtUtil.getAuthentication(refresh).getPrincipal();
    String newAccess = jwtUtil.createAccessToken(customUserDetails);

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
    try {
      BufferedReader reader = request.getReader();
      StringBuilder stringBuilder = new StringBuilder();

      String line;
      while ((line = reader.readLine()) != null) {
        stringBuilder.append(line);
      }

      String requestBody = stringBuilder.toString();
      log.info("Request Body: {}", requestBody);

      // JSON 파싱
      ObjectMapper objectMapper = new ObjectMapper();
      JsonNode jsonNode = objectMapper.readTree(requestBody);

      // "refreshToken" 키 추출
      String refreshToken = jsonNode.path("refreshToken").asText(null);

      // 리프레시 토큰이 없는 경우
      if (refreshToken == null || refreshToken.isBlank()) {
        log.error("요청 바디에 refresh token 이 없습니다.");
        throw new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
      }

      log.info("refresh token 추출 성공: {}", refreshToken);
      return refreshToken;

    } catch (IOException e) {
      log.error("요청 바디를 읽는 중 오류 발생: {}", e.getMessage());
      throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
    }
  }
}