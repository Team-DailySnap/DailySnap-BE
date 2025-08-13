package onepiece.dailysnapbackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import onepiece.dailysnapbackend.object.postgres.Keyword;
import onepiece.dailysnapbackend.object.postgres.Member;
import onepiece.dailysnapbackend.object.postgres.Post;
import onepiece.dailysnapbackend.repository.postgres.KeywordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MockPostFactory {

  private final Faker koFaker;
  private final Faker enFaker;
  private final KeywordRepository keywordRepository;

  @Transactional
  public Post generate(Member member, Keyword keyword) {
    return Post.builder()
        .member(member)
        .keyword(keyword)
        .imageUrl(koFaker.internet().image())
        .description(koFaker.lorem().sentence())
        .likeCount(koFaker.random().nextInt(300))
        .build();
  }
}
