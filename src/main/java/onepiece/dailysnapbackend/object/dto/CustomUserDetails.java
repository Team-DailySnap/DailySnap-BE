package onepiece.dailysnapbackend.object.dto;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import onepiece.dailysnapbackend.object.postgres.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

  private final Member member;
  private Map<String, Object> attributes;

  public CustomUserDetails(Member member, Map<String, Object> attributes) {
    this.member = member;
    this.attributes = attributes;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.singletonList(new SimpleGrantedAuthority(member.getRole().name()));
  }

  @Override
  public String getPassword() {return member.getPassword();}

  @Override
  public String getUsername() {return member.getUsername();}

  @Override
  public boolean isCredentialsNonExpired() {return true;}

}
