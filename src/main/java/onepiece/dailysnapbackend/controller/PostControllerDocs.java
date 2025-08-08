package onepiece.dailysnapbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import java.util.UUID;
import onepiece.dailysnapbackend.object.dto.CustomOAuth2User;
import onepiece.dailysnapbackend.object.dto.PostFilteredRequest;
import onepiece.dailysnapbackend.object.dto.PostFilteredResponse;
import onepiece.dailysnapbackend.object.dto.PostRequest;
import onepiece.dailysnapbackend.object.dto.PostResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

public interface PostControllerDocs {

  @Operation(
      summary = "게시물 업로드",
      description = """
          ### 요청 파라미터
          - `image` (file, required): 업로드할 이미지 파일 (MultipartFile)
          - `description` (String, optional): 게시물 설명
          
          ### 응답 데이터
          - 없음 (빈 본문)
          
          ### 사용 방법
          1. 클라이언트에서 Authorization 헤더에 `Bearer {accessToken}`을 포함합니다.  
          2. `Content-Type: multipart/form-data` 로 아래와 같이 폼 데이터 요청을 보냅니다:
             ```
             POST /api/auth
             Content-Type: multipart/form-data
             Authorization: Bearer eyJhbGciOiJI...
          
             --boundary
             Content-Disposition: form-data; name="image"; filename="photo.jpg"
             Content-Type: image/jpeg
          
             (파일 바이너리)
             --boundary
             Content-Disposition: form-data; name="description"
          
             오늘의 키워드 사진입니다.
             --boundary--
             ```
          3. 서버가 이미지를 저장하고 200 OK 응답을 반환합니다.
          
          ### 유의 사항
          - `image` 파일은 반드시 전송해야 합니다.  
          - `description`은 최대 길이 제한이 없으나, 필요 시 클라이언트에서 적절히 검증해 주세요.  
          - 파일 업로드 실패 시 4xx/5xx 에러가 발생할 수 있습니다.  
          """
  )
  ResponseEntity<Void> uploadPost(
      CustomOAuth2User userDetails,
      PostRequest request
  );

  @Operation(
      summary = "게시글 필터링 (페이징 및 정렬 지원)",
      description = """
          
          이 API는 인증이 필요합니다.
          
          ### 요청 파라미터
          - **nickname** (String): 닉네임으로 게시글 필터링 (선택, 빈 값일 경우 전체 조회)
          - **pageNumber** (int): 페이지 번호 (0부터 시작, 기본값: 0)
          - **pageSize** (int): 페이지당 게시글 개수 (기본값: 30)
          - **sortField** (String): 정렬 기준 (`created_date`, `like_count` 중 선택, 기본값: `created_date`)
          - **sortDirection** (String): 정렬 방향 (`ASC`, `DESC` 중 선택, 기본값: `DESC`)
          
          ### 반환값
          - **member** (Member) : 회원
          - **keyword** (Keyword) : 키워드
          - **images** (List<Image>) : 이미지
          - **content** (String): 사진 설명
          - **viewCount** (Integer): 조회수
          - **likeCount** (Integer) : 좋아요수
          - **location** (String) : 위치
          
          """
  )
  ResponseEntity<Page<PostFilteredResponse>> filteredPosts
      (CustomOAuth2User userDetails, PostFilteredRequest request);

  @Operation(
      summary = "게시글 상세 조회",
      description = """
          
          이 API는 인증이 필요합니다.
          
          ### 요청 파라미터
          - **postId** (Long): 좋아요를 누를 게시글 ID (필수)
          
          ### 반환값
          - **keyword** (String): 게시글 키워드
          - **images** (List<Image>): 게시글 이미지 목록
          - **content** (String): 게시글 내용
          - **viewCount** (int): 조회수
          - **likeCount** (int): 좋아요 수
          - **location** (String): 게시글 위치 정보
          
          """
  )
  ResponseEntity<PostResponse> detailPost(UUID postId);
}
