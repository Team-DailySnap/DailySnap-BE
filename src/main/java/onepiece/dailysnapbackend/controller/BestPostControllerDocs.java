package onepiece.dailysnapbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import onepiece.dailysnapbackend.object.dto.BestPostRequest;
import onepiece.dailysnapbackend.object.dto.BestPostResponse;
import onepiece.dailysnapbackend.object.dto.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;

public interface BestPostControllerDocs {

  @Operation(
      summary = "인기 게시물 조회",
      description = """
          이 API는 인기 게시물 목록을 조회하는 API입니다.

          ### 요청 파라미터
          - **filter** (String) : 게시물 필터 유형 (일간, 주간, 월간)
          - **startDate** (LocalDate) : 게시물 조회 시작 날짜 (YYYY-MM-DD)
          
          ### 반환값
          - **postId** (UUID) : 게시물 ID
          - **viewCount** (Integer) : 조회수
          - **likesCount** (Integer) : 좋아요 수
          - **createdDate** (LocalDate) : 생성일
          """
  )
  ResponseEntity<List<BestPostResponse>> getBestPosts(
      CustomUserDetails customUserDetails,
      @ModelAttribute BestPostRequest request
  );
}
