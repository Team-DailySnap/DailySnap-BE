package onepiece.dailysnapbackend.controller;

import lombok.AllArgsConstructor;
import onepiece.dailysnapbackend.object.dto.RoleType;
import onepiece.dailysnapbackend.object.postgres.Member;
import onepiece.dailysnapbackend.repository.MemberRepository;
import onepiece.dailysnapbackend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

  @Autowired
  private final AuthenticationManager authenticationManager;
  @Autowired
  private final JwtUtil jwtUtil;
  @Autowired
  private final MemberRepository memberRepository;
  @Autowired
  private final BCryptPasswordEncoder passwordEncoder;

  @PostMapping("/register")
  public String register(@RequestParam String username, @RequestParam String password, @RequestParam String nickname) {
    if (memberRepository.findByUsername(username) != null) {
      return "이미 존재하는 사용자입니다.";
    }

    String encodedPassword = passwordEncoder.encode(password);

    Member newMember = Member.builder()
        .username(username)
        .password(encodedPassword)  // 암호화된 비밀번호 저장
        .nickname(nickname)
        .role(RoleType.USER.getValue()) // 기본 USER 역할 부여
        .build();

    memberRepository.save(newMember);
    return "회원가입 성공";
  }

  @PostMapping("/login")
  public String login(@RequestParam String username, @RequestParam String password) {
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(username, password)
    );

    // String 으로 토큰을 리턴해버리면 -> 인증 정보가 그대로 네트워크 타고 유출되는거.
    // f12하면 노출 -> 보안 이슈
    // 일반적으로 HTTPS 의 Secure Cookie (return -> fe local storage 에 담기)에 백에서 담아서 전달
    // 프론트에서는 쿠키에 있는 토큰을 Authorization Header 에 Bearer {JWT} 헤더에 정보담아서
    // 요청 보낸다 -> Spring Security 가 항상 동작함
    // -> 우리가 등록한 JWTFilter 에서 유효한 토큰인지 검사
    //    -> 1. 유효하면 SecurityContext 에 Authentication 객체 넣어주고 필터 통과
    //    -> 2. 유요하지 않으면 401 UnAuthorized (인증 오류 발생)
    return jwtUtil.createJwt(username, RoleType.USER.getValue());
  }
}

