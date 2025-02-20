package onepiece.dailysnapbackend.service;

import jakarta.transaction.Transactional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import onepiece.dailysnapbackend.object.dto.PostRequest;
import onepiece.dailysnapbackend.object.postgres.Keyword;
import onepiece.dailysnapbackend.object.postgres.Member;
import onepiece.dailysnapbackend.object.postgres.Post;
import onepiece.dailysnapbackend.repository.postgres.KeywordRepository;
import onepiece.dailysnapbackend.repository.postgres.PostRepository;
import onepiece.dailysnapbackend.util.exception.CustomException;
import onepiece.dailysnapbackend.util.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

  private final S3UploadService s3UploadService;
  private final PostRepository postRepository;
  private final KeywordRepository keywordRepository;
  private final long MAX_FILE_SIZE = 200 * 1024 * 1024; // 200MB 제한

  @Transactional
  public UUID uploadPost(PostRequest request, Member member) {
    MultipartFile image = request.getImage();

    // 파일 크기 제한 검사
    if (image.getSize() > MAX_FILE_SIZE) {
      log.error("파일 크기가 200MB를 초과했습니다: fileSize={}", image.getSize());
      throw new CustomException(ErrorCode.FILE_SIZE_EXCEED);
    }

    // S3에 파일 업로드 후 URL 받기
    String imageUrl = s3UploadService.upload(image);

    // 키워드 엔티티 조회
    Keyword keyword = keywordRepository.findById(request.getKeywordId())
        .orElseThrow(() -> new CustomException(ErrorCode.KEYWORD_NOT_FOUND));

    // DB에 사진 정보 저장
    Post post = Post.builder()
        .member(member)
        .keyword(keyword)
        .imageUrl(imageUrl)
        .content(request.getContent())
        .likeCount(0)
        .location(request.getLocation())
        .build();

    postRepository.save(post);

    log.info("사진 업로드 성공: photoId={}", post.getPostId());
    return post.getPostId();
  }
}
