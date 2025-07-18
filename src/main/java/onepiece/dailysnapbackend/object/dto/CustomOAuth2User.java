package onepiece.dailysnapbackend.object.dto;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import onepiece.dailysnapbackend.object.constants.AccountStatus;
import onepiece.dailysnapbackend.object.constants.SocialPlatform;
import onepiece.dailysnapbackend.object.postgres.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Getter
@RequiredArgsConstructor
public class CustomOAuth2User implements OAuth2User {

  private final Member member;
  private final Map<String, Object> attributes;

  @Override
  public Map<String, Object> getAttributes() {
    return attributes;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.singletonList(new SimpleGrantedAuthority(member.getRole().name()));
  }

  @Override
  public String getName() {
    return member.getUsername();
  }

  public SocialPlatform getSocialPlatform() {
    return member.getSocialPlatform();
  }

  public boolean isAccountNonExpired() {
    // AccountStatus가 DELETE_ACCOUNT 인 경우, 계정이 만료된 것으로 간주
    return member.getAccountStatus() != AccountStatus.DELETE_ACCOUNT;
  }

  public boolean isAccountNonLocked() {
    // AccountStatus가 DELETE_ACCOUNT 인 경우, 계정이 잠긴 것으로 간주
    return member.getAccountStatus() != AccountStatus.DELETE_ACCOUNT;
  }

  public boolean isCredentialsNonExpired() {
    return true; // 인증 정보 항상 유효
  }

  public boolean isEnabled() {
    // AccountStatus가 ACTIVE_ACCOUNT 인 경우, 계정이 활성화
    return member.getAccountStatus() != AccountStatus.DELETE_ACCOUNT;
  }

  public String getMemberId() {
    return member.getMemberId().toString();
  }
}
