package onepiece.dailysnapbackend.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import onepiece.dailysnapbackend.object.dto.ApiResponse;
import onepiece.dailysnapbackend.object.dto.SignUpRequest;
import onepiece.dailysnapbackend.object.postgres.Member;
import onepiece.dailysnapbackend.repository.postgres.MemberRepository;
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

  // 회원가입
  @Transactional
  public ApiResponse<Void> signUp(SignUpRequest member) {

    // 아이디 중복 체크
    if (memberRepository.existsByUsername(member.getUsername()).isPresent()) {
      log.error("이미 가입된 회원입니다: {}", member.getUsername());
      throw new CustomException(ErrorCode.DUPLICATE_USERNAME);
    }

    memberRepository.save(Member.builder()
        .username(member.getUsername())
        .password(bCryptPasswordEncoder.encode(member.getPassword()))
        .nickname(member.getNickname())
        .build()
    );
    log.info("회원가입 성공");

    return ApiResponse.success(null);
  }
}
