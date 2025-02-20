package onepiece.dailysnapbackend.service;

import jakarta.transaction.Transactional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import onepiece.dailysnapbackend.object.dto.PhotoPostRequest;
import onepiece.dailysnapbackend.object.postgres.Keyword;
import onepiece.dailysnapbackend.object.postgres.Member;
import onepiece.dailysnapbackend.object.postgres.Photo;
import onepiece.dailysnapbackend.repository.postgres.KeywordRepository;
import onepiece.dailysnapbackend.repository.postgres.PhotoRepository;
import onepiece.dailysnapbackend.util.exception.CustomException;
import onepiece.dailysnapbackend.util.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class PhotoService {

  private final S3UploadService s3UploadService;
  private final PhotoRepository photoRepository;
  private final KeywordRepository keywordRepository;
  private final long MAX_FILE_SIZE = 200 * 1024 * 1024; // 200MB 제한

  @Transactional
  public UUID uploadPhoto(PhotoPostRequest request, Member member) {
    MultipartFile file = request.getImageUrl();

    // 파일 크기 제한 검사
    if (file.getSize() > MAX_FILE_SIZE) {
      log.error("파일 크기가 200MB를 초과했습니다: fileSize={}", file.getSize());
      throw new CustomException(ErrorCode.FILE_SIZE_EXCEED);
    }

    // S3에 파일 업로드 후 URL 받기
    String imageUrl = s3UploadService.upload(file);

    // 키워드 엔티티 조회
    Keyword keyword = keywordRepository.findById(request.getKeywordId())
        .orElseThrow(() -> new CustomException(ErrorCode.KEYWORD_NOT_FOUND));

    // DB에 사진 정보 저장
    Photo photo = Photo.builder()
        .member(member)
        .keyword(keyword)
        .imageUrl(imageUrl)
        .content(request.getContent())
        .likeCount(0)
        .location(request.getLocation())
        .build();

    photoRepository.save(photo);

    log.info("사진 업로드 성공: photoId={}", photo.getPhotoId());
    return photo.getPhotoId();
  }
}
