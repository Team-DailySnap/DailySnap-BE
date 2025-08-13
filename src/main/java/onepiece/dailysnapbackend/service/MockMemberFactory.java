package onepiece.dailysnapbackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import onepiece.dailysnapbackend.object.constants.AccountStatus;
import onepiece.dailysnapbackend.object.constants.Role;
import onepiece.dailysnapbackend.object.constants.SocialPlatform;
import onepiece.dailysnapbackend.object.postgres.Member;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MockMemberFactory {

  private final Faker koFaker;
  private final Faker enFaker;

  public Member generate() {
    return Member.builder()
        .username(enFaker.internet().emailAddress())
        .socialPlatform(koFaker.options().option(SocialPlatform.class))
        .nickname(koFaker.name().name() + koFaker.random().nextInt(1000))
        .profileImageUrl(koFaker.internet().image())
        .role(Role.ROLE_USER)
        .accountStatus(AccountStatus.ACTIVE_ACCOUNT)
        .dailyUploadCount(koFaker.random().nextInt(4))
        .firstLogin(true)
        .paid(false)
        .build();
  }
}
