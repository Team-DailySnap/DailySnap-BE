package onepiece.dailysnapbackend.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import onepiece.dailysnapbackend.object.dto.PostFilteredRequest;
import onepiece.dailysnapbackend.object.dto.PostFilteredResponse;
import onepiece.dailysnapbackend.object.dto.PostRequest;
import onepiece.dailysnapbackend.object.dto.PostResponse;
import onepiece.dailysnapbackend.object.postgres.Image;
import onepiece.dailysnapbackend.object.postgres.Keyword;
import onepiece.dailysnapbackend.object.postgres.Member;
import onepiece.dailysnapbackend.object.postgres.Post;
import onepiece.dailysnapbackend.repository.postgres.ImageRepository;
import onepiece.dailysnapbackend.repository.postgres.KeywordRepository;
import onepiece.dailysnapbackend.repository.postgres.PostRepository;
import onepiece.dailysnapbackend.util.CommonUtil;
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

  private final S3UploadService s3UploadService;
  private final PostRepository postRepository;
  private final KeywordRepository keywordRepository;
  private final ImageRepository imageRepository;
  private final RedisLockService redisLockService;

  private static final int MAX_IMAGE_COUNT = 10;

  // 이미지 업로드
  @Transactional
  public PostResponse uploadPost(PostRequest request, Member member) {
    List<MultipartFile> images = request.getImages();

    // 이미지 개수 검사
    if (images.size() > MAX_IMAGE_COUNT) {
      throw new CustomException(ErrorCode.FILE_COUNT_EXCEED);
    }

    // 키워드 엔티티 조회
    Keyword keyword = keywordRepository.findById(request.getKeywordId())
        .orElseThrow(() -> new CustomException(ErrorCode.KEYWORD_NOT_FOUND));

    // Post 엔티티 생성 및 저장
    Post post = Post.builder()
        .member(member)
        .keyword(keyword)
        .content(request.getContent())
        .viewCount(0)
        .likeCount(0)
        .location(request.getLocation())
        .build();

    postRepository.save(post);

    // S3에 파일 업로드 후 URL 리스트 받기
    List<String> imageUrls = s3UploadService.upload(images);

    // Image 엔티티 생성 및 저장
    List<Image> imageEntities = imageUrls.stream()
        .map(url -> Image.builder()
            .imageUrl(url)
            .post(post)
            .build())
        .collect(Collectors.toList());

    imageRepository.saveAll(imageEntities);
    log.info("게시물 업로드 성공: postId={}", post.getPostId());

    return PostResponse.builder()
        .keyword(post.getKeyword())
        .images(imageEntities)
        .content(post.getContent())
        .viewCount(post.getViewCount())
        .likeCount(post.getLikeCount())
        .location(post.getLocation())
        .build();
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
        .content(post.getContent())
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
        .content(post.getContent())
        .viewCount(updatedViewCount)
        .likeCount(post.getLikeCount())
        .location(post.getLocation())
        .build();
  }
}