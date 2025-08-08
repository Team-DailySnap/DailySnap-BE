package onepiece.dailysnapbackend.service;

import jakarta.transaction.Transactional;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import onepiece.dailysnapbackend.object.constants.AccountStatus;
import onepiece.dailysnapbackend.object.constants.Role;
import onepiece.dailysnapbackend.object.dto.CustomOAuth2User;
import onepiece.dailysnapbackend.object.dto.LoginRequest;
import onepiece.dailysnapbackend.object.dto.LoginResponse;
import onepiece.dailysnapbackend.object.dto.ReissueRequest;
import onepiece.dailysnapbackend.object.postgres.Member;
import onepiece.dailysnapbackend.repository.postgres.MemberRepository;
import onepiece.dailysnapbackend.util.JwtUtil;
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

  // 리프레시 토큰을 통해 액세스 & 리프레시 토큰 재발급
  @Transactional
  public LoginResponse reissue(ReissueRequest request) {

    // 만료 여부 확인
    jwtUtil.validateToken(request.getRefreshToken());

    // 새 액세스 토큰 발급
    CustomOAuth2User customOAuth2User = (CustomOAuth2User) jwtUtil.getAuthentication(request.getRefreshToken()).getPrincipal();
    String newAccessToken = jwtUtil.createAccessToken(customOAuth2User);
    String newRefreshToken = jwtUtil.createRefreshToken(customOAuth2User);

    // JSON 응답 바디로 액세스 토큰 반환
    return new LoginResponse(newAccessToken, newRefreshToken);
  }
}
