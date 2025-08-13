package onepiece.dailysnapbackend.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import onepiece.dailysnapbackend.object.constants.UploadType;
import onepiece.dailysnapbackend.object.dto.PostFilteredRequest;
import onepiece.dailysnapbackend.object.dto.PostRequest;
import onepiece.dailysnapbackend.object.dto.PostResponse;
import onepiece.dailysnapbackend.object.postgres.Keyword;
import onepiece.dailysnapbackend.object.postgres.Member;
import onepiece.dailysnapbackend.object.postgres.Post;
import onepiece.dailysnapbackend.repository.postgres.KeywordRepository;
import onepiece.dailysnapbackend.repository.postgres.PostQueryDslRepository;
import onepiece.dailysnapbackend.repository.postgres.PostRepository;
import onepiece.dailysnapbackend.service.keyword.KeywordService;
import onepiece.dailysnapbackend.util.FileUtil;
import onepiece.dailysnapbackend.util.exception.CustomException;
import onepiece.dailysnapbackend.util.exception.ErrorCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

  private final PostRepository postRepository;
  private final KeywordService keywordService;
  private final StorageService storageService;
  private final PostQueryDslRepository postQueryDslRepository;
  private final KeywordRepository keywordRepository;

  // 이미지 업로드
  @Transactional
  public void uploadPost(Member member, PostRequest request) {

    Keyword keyword = keywordService.findKeywordByProvidedDate(LocalDate.now(ZoneId.of("Asia/Seoul")));
    FileUtil.isNullOrEmpty(request.getImage());

    // Post 엔티티 생성 및 저장
    Post post = Post.builder()
        .member(member)
        .keyword(keyword)
        .imageUrl(storageService.uploadFile(request.getImage(), UploadType.POST))
        .description(request.getDescription())
        .likeCount(0)
        .build();
    Post savedPost = postRepository.save(post);
    log.info("게시물 업로드 성공: postId={}", savedPost.getPostId());
  }

  @Transactional(readOnly = true)
  public Page<PostResponse> filteredPost(PostFilteredRequest request) {
    Page<Post> postPage = postQueryDslRepository.filteredPost(request);
    return postPage.map(post -> PostResponse.from(post, post.getMember(), post.getKeyword()));
  }

  // 특정 글 조회
  @Transactional(readOnly = true)
  public PostResponse getPost(Member member, UUID postId) {
    Post post = findPostById(postId);
    return PostResponse.from(post, member, post.getKeyword());
  }

  public Post findPostById(UUID postId) {
    return postRepository.findById(postId)
        .orElseThrow(() -> {
          log.error("요청 PK: {}에 해당하는 게시글을 찾을 수 없습니다", postId);
          return new CustomException(ErrorCode.POST_NOT_FOUND);
        });
  }

  @Transactional(readOnly = true)
  public List<PostResponse> get7DaysRandomPost() {
    List<Keyword> latest7Keywords = keywordRepository.findTop7ByProvidedDateAfterOrderByProvidedDate(LocalDate.now().minusDays(1));
    List<PostResponse> postResponses = new ArrayList<>();
    for (Keyword keyword : latest7Keywords) {
      List<Post> randomOne = postRepository.findRandomOneWithMemberByKeywordId(keyword.getKeywordId(), PageRequest.of(0, 1));
      if (randomOne.isEmpty()) {
        continue;
      }
      Post post = randomOne.get(0);
      Member member = post.getMember();

      postResponses.add(PostResponse.from(post, member, keyword));
    }
    return postResponses;
  }
}