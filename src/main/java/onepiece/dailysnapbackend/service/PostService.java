package onepiece.dailysnapbackend.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import onepiece.dailysnapbackend.object.constants.UploadType;
import onepiece.dailysnapbackend.object.dto.PostFilteredRequest;
import onepiece.dailysnapbackend.object.dto.PostFilteredResponse;
import onepiece.dailysnapbackend.object.dto.PostRequest;
import onepiece.dailysnapbackend.object.dto.PostResponse;
import onepiece.dailysnapbackend.object.postgres.Keyword;
import onepiece.dailysnapbackend.object.postgres.Member;
import onepiece.dailysnapbackend.object.postgres.Post;
import onepiece.dailysnapbackend.repository.postgres.ImageRepository;
import onepiece.dailysnapbackend.repository.postgres.KeywordRepository;
import onepiece.dailysnapbackend.repository.postgres.PostRepository;
import onepiece.dailysnapbackend.service.keyword.KeywordService;
import onepiece.dailysnapbackend.util.CommonUtil;
import onepiece.dailysnapbackend.util.FileUtil;
import onepiece.dailysnapbackend.util.exception.CustomException;
import onepiece.dailysnapbackend.util.exception.ErrorCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

  private final PostRepository postRepository;
  private final KeywordService keywordService;
  private final StorageService storageService;

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

  /**
   * 게시글 필터링 정렬 조건 : created_date, like_count
   *
   * @param request nickname 닉네임 (null 또는 빈 값이면 전체 게시글 조회)
   */
  @Transactional(readOnly = true)
  public Page<PostFilteredResponse> getFilteredPosts(PostFilteredRequest request) {
    // null 이거나 created_date/like_count 가 아닐 경우 created_date 를 기본값으로 설정
    String sortField = CommonUtil.nvl(request.getSortField(), "created_date");
    if (!sortField.matches("created_date|like_count")) {
      sortField = "created_date";
    }
    // "ASC" 를 제외한 모든 값이 들어오면 "DESC"로 설정
    String direction = CommonUtil.nvl(request.getSortDirection(), "DESC");
    Sort.Direction sortDirection;
    if ("ASC".equalsIgnoreCase(direction)) {
      sortDirection = Sort.Direction.ASC;
    } else {
      sortDirection = Sort.Direction.DESC;
    }

    Sort sort = Sort.by(sortDirection, sortField);

    // 페이징 설정
    Pageable pageable = PageRequest.of(
        request.getPageNumber(),
        request.getPageSize(),
        sort
    );
    Page<Post> posts = postRepository.filterPosts(request.getNickname(), pageable);

    log.info("게시물 필터링 성공: totalElements={}", posts.getTotalElements());
    return posts.map(post -> PostFilteredResponse.builder()
        .member(post.getMember())
        .keyword(post.getKeyword())
        .images(imageRepository.findByPost(post))
        .content(post.getDescription())
        .viewCount(post.getViewCount())
        .likeCount(post.getLikeCount())
        .location(post.getLocation())
        .build());
  }

  // 게시물 상세 조회
  @Transactional
  public PostResponse getPostDetails(UUID postId) {
    String lockKey = "post_lock:" + postId;

    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

    Integer updatedViewCount = redisLockService.executeWithLock(lockKey, () -> {
      post.setViewCount(post.getViewCount() + 1);
      postRepository.save(post);
      log.info("{} 게시물 조회수 증가: viewCount={}", postId, post.getViewCount());
      return post.getViewCount();
    });

    List<Image> images = imageRepository.findByPost(post);

    return PostResponse.builder()
        .keyword(post.getKeyword())
        .images(images)
        .content(post.getDescription())
        .viewCount(updatedViewCount)
        .likeCount(post.getLikeCount())
        .location(post.getLocation())
        .build();
  }
}