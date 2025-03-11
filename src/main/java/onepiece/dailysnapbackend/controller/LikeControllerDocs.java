package onepiece.dailysnapbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import onepiece.dailysnapbackend.object.dto.CustomUserDetails;
import onepiece.dailysnapbackend.object.dto.PostDetailRequest;
import org.springframework.http.ResponseEntity;

public interface LikeControllerDocs {

  @Operation(
      summary = "게시글 좋아요",
      description = """
        
        이 API는 인증이 필요합니다.
        
        ### 요청 파라미터
        - **postId** (Long): 좋아요를 누를 게시글 ID (필수)
        
        ### 반환값
        - **likeCount** (int): 좋아요 수
        
        """
  )
  ResponseEntity<Integer> postLike(CustomUserDetails userDetails, PostDetailRequest request);
}
