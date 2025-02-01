package onepiece.dailysnapbackend.service;

import onepiece.dailysnapbackend.object.dto.CustomUserDetails;
import onepiece.dailysnapbackend.object.postgres.Member;
import onepiece.dailysnapbackend.repository.MemberRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

  private final MemberRepository memberRepository;

  public CustomUserDetailsService(MemberRepository memberRepository) {
    this.memberRepository = memberRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Member member = memberRepository.findByUsername(username);

    if (member == null) {
      throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username);
    }

    return new CustomUserDetails(member);
  }
}
