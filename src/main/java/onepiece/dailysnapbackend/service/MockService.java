package onepiece.dailysnapbackend.service;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import onepiece.dailysnapbackend.object.constants.AccountStatus;
import onepiece.dailysnapbackend.object.constants.Role;
import onepiece.dailysnapbackend.object.constants.SocialPlatform;
import onepiece.dailysnapbackend.object.dto.CustomOAuth2User;
import onepiece.dailysnapbackend.object.dto.LoginResponse;
import onepiece.dailysnapbackend.object.dto.MockLoginRequest;
import onepiece.dailysnapbackend.object.postgres.Keyword;
import onepiece.dailysnapbackend.object.postgres.Member;
import onepiece.dailysnapbackend.object.postgres.Post;
import onepiece.dailysnapbackend.repository.postgres.KeywordRepository;
import onepiece.dailysnapbackend.repository.postgres.MemberRepository;
import onepiece.dailysnapbackend.repository.postgres.PostRepository;
import onepiece.dailysnapbackend.util.JwtUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MockService {

  private final MemberRepository memberRepository;
  private final MockMemberFactory mockMemberFactory;
  private final Faker koFaker;
  private final Faker enFaker;
  private final JwtUtil jwtUtil;
  private final KeywordRepository keywordRepository;
  private final MockPostFactory mockPostFactory;
  private final PostRepository postRepository;

  @Transactional
  public LoginResponse mockLogin(MockLoginRequest request) {
    String username = request.getUsername().isBlank()
        ? enFaker.internet().emailAddress() + koFaker.random().nextInt(1000)
        : request.getUsername() + koFaker.random().nextInt(1000);
    String nickname = request.getNickname().isBlank()
        ? koFaker.name().name() + koFaker.random().nextInt(1000)
        : request.getNickname() + koFaker.random().nextInt(1000);
    Role role = request.getRole() == null ?
        Role.ROLE_USER : request.getRole();

    Member member = Member.builder()
        .username(username)
        .socialPlatform(SocialPlatform.KAKAO)
        .nickname(nickname)
        .profileImageUrl(koFaker.internet().image())
        .role(role)
        .accountStatus(AccountStatus.ACTIVE_ACCOUNT)
        .dailyUploadCount(0)
        .firstLogin(true)
        .paid(false)
        .build();
    memberRepository.save(member);
    CustomOAuth2User customOAuth2User = new CustomOAuth2User(member, null);
    String accessToken = jwtUtil.createAccessToken(customOAuth2User);
    String refreshToken = jwtUtil.createRefreshToken(customOAuth2User);

    return new LoginResponse(accessToken, refreshToken);
  }

  @Transactional
  public void createMockMember(int count) {
    List<Member> members = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      members.add(mockMemberFactory.generate());
    }
    memberRepository.saveAll(members);
  }

  @Transactional
  public void createMockPost(int count) {
    List<Keyword> keywords = keywordRepository.findAll();
    List<Member> members = memberRepository.findAll();
    List<Post> posts = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      posts.add(mockPostFactory.generate(members.get(koFaker.random().nextInt(members.size())), keywords.get(koFaker.random().nextInt(keywords.size()))));
    }
    postRepository.saveAll(posts);
  }
}
