package onepiece.dailysnapbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import onepiece.dailysnapbackend.object.dto.CustomUserDetails;
import onepiece.dailysnapbackend.object.dto.PostDetailRequest;
import onepiece.dailysnapbackend.object.dto.PostResponse;
import org.springframework.http.ResponseEntity;

public interface LikeControllerDocs {

  @Operation(
      summary = "게시글 좋아요",
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
  ResponseEntity<PostResponse> postLike(CustomUserDetails userDetails, PostDetailRequest request);
}
