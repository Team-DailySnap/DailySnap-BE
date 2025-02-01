package onepiece.dailysnapbackend.util.config;

import onepiece.dailysnapbackend.util.JwtUtil;
import onepiece.dailysnapbackend.util.filter.JwtFilter;
import onepiece.dailysnapbackend.util.filter.LoginFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

  private final JwtUtil jwtUtil;
  private final AuthenticationConfiguration authenticationConfiguration;

  public SecurityConfig(JwtUtil jwtUtil, AuthenticationConfiguration authenticationConfiguration) {
    this.jwtUtil = jwtUtil;
    this.authenticationConfiguration = authenticationConfiguration;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    // JWT => 우리는 우리가 발급한 토큰으로 접근하는 사용자만 서비스를 허락주겠다.
    // 최초에는 당연히 토큰 X => 풀어줘야함 => permitAll

    // JWT 발급 -> HTTPS Secure Cookie ->
    http
        .csrf(csrf -> csrf.disable()) // CSRF 비활성화
        .authorizeHttpRequests(auth ->
            auth.requestMatchers("/auth/register", "/auth/login").permitAll() // 회원가입, 로그인은 인증 없이 허용
                .anyRequest().authenticated() // 그 외 요청은 인증 필요
        )
        .addFilterBefore(new JwtFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(new LoginFilter(authenticationManager(), jwtUtil), UsernamePasswordAuthenticationFilter.class)
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    return http.build();
  }

  @Bean
  public AuthenticationManager authenticationManager() throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }

  @Bean
  public BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
