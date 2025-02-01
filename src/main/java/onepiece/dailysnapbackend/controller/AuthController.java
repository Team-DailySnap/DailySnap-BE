package onepiece.dailysnapbackend.controller;

import lombok.AllArgsConstructor;
import onepiece.dailysnapbackend.object.dto.RoleType;
import onepiece.dailysnapbackend.object.postgres.Member;
import onepiece.dailysnapbackend.repository.MemberRepository;
import onepiece.dailysnapbackend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
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
    try {
      if (memberRepository.findByUsername(username) != null) {
        return "이미 존재하는 사용자입니다.";
      }

      String encodedPassword = passwordEncoder.encode(password);

      Member newMember = Member.builder()
          .username(username)
          .password(encodedPassword)
          .nickname(nickname)
          .role(RoleType.USER.getValue())
          .build();

      memberRepository.save(newMember);
      System.out.println("회원가입 성공: " + username);
      return "회원가입 성공";

    } catch (Exception e) {
      System.out.println("회원가입 중 오류 발생: " + e.getMessage());
      return "회원가입 실패: " + e.getMessage();
    }
  }


  @PostMapping("/login")
  public String login(@RequestParam String username, @RequestParam String password) {
    Member member = memberRepository.findByUsername(username);

    if (member == null) {
      return "사용자를 찾을 수 없습니다.";
    }

    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    if (!passwordEncoder.matches(password, member.getPassword())) {
      return "비밀번호가 일치하지 않습니다.";
    }

    // JWT 토큰 생성 및 반환
    String token = jwtUtil.createJwt(username, RoleType.USER.getValue());
    return "로그인 성공! JWT: " + token;
  }
}

