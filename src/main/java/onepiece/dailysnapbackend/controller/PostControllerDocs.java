package onepiece.dailysnapbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import onepiece.dailysnapbackend.object.dto.CustomUserDetails;
import onepiece.dailysnapbackend.object.dto.PostFilteredRequest;
import onepiece.dailysnapbackend.object.dto.PostFilteredResponse;
import onepiece.dailysnapbackend.object.dto.PostRequest;
import onepiece.dailysnapbackend.object.dto.PostResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

public interface PostControllerDocs {

  @Operation(
      summary = "사진 업로드",
      description = """
          
          이 API는 인증이 필요합니다
          
          ### 요청 파라미터
          - **keywordId** (UUID): 키워드 id
          - **images** (List<MultipartFile>): 이미지
          - **content** (String): 사진 설명 (필수X)
          - **location** (String): 위치 (필수X)
          
          ### 반환값
          - **keyword** (Keyword) : 키워드
          - **images** (List<Image>) : 이미지
          - **content** (String): 사진 설명
          - **location** (String) : 위치
          
          """
  )
  ResponseEntity<PostResponse> uploadPost
      (CustomUserDetails userDetails, PostRequest request);

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
          - **likeCount** (Integer) : 좋아요수
          - **location** (String) : 위치
          
          """
  )
  ResponseEntity<Page<PostFilteredResponse>> filteredPosts
      (CustomUserDetails userDetails, PostFilteredRequest request);
}