package onepiece.dailysnapbackend.service;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import onepiece.dailysnapbackend.object.dto.CustomOAuth2User;
import onepiece.dailysnapbackend.object.postgres.Member;
import onepiece.dailysnapbackend.repository.postgres.MemberRepository;
import onepiece.dailysnapbackend.util.exception.CustomException;
import onepiece.dailysnapbackend.util.exception.ErrorCode;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService {

  private final MemberRepository memberRepository;

  public CustomOAuth2User loadUserByUsername(String username) {
    Member member = memberRepository.findByUsername(username).orElseThrow(() -> {
      log.error("회원을 찾을 수 없습니다. 회원 Username: {}", username);
      return new CustomException(ErrorCode.MEMBER_NOT_FOUND);
    });
    return new CustomOAuth2User(member, Map.of());
  }
}
