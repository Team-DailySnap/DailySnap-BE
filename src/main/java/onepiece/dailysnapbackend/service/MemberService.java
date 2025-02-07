package onepiece.dailysnapbackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import onepiece.dailysnapbackend.object.constants.Role;
import onepiece.dailysnapbackend.object.dto.ApiResponse;
import onepiece.dailysnapbackend.object.postgres.Member;
import onepiece.dailysnapbackend.repository.MemberRepository;
import onepiece.dailysnapbackend.util.exception.CustomException;
import onepiece.dailysnapbackend.util.exception.ErrorCode;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

  private final MemberRepository memberRepository;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;

  /**
   * 회원가입
   *
   * @param username 회원 이메일
   * @param password 비밀번호
   * @param nickname 닉네임
   * @return 없음
   */

  @Transactional
  public ApiResponse<Void> signUp(String username, String password, String nickname) {

    // 사용자 이메일 검증 (중복 이메일 사용 불가)
    if (memberRepository.existsByUsername(username)) {
      log.error("이미 가입된 이메일 주소입니다: {}", username);
      throw new CustomException(ErrorCode.DUPLICATE_USERNAME);
    }

    memberRepository.save(Member.builder()
        .username(username)
        .password(bCryptPasswordEncoder.encode(password))
        .nickname(nickname)
        .role(Role.ROLE_USER)
        .build()
    );
    log.debug("회원가입 성공: username={}", username);

    return ApiResponse.success(null);
  }


}

