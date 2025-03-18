package onepiece.dailysnapbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import java.time.LocalDate;
import java.util.List;
import onepiece.dailysnapbackend.object.dto.CustomUserDetails;
import onepiece.dailysnapbackend.object.postgres.Post;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

public interface BestPostControllerDocs {

  @Operation(
      summary = "인기 게시물 조회",
      description = """
                인기 게시물을 조회합니다.
                
                ### 요청 파라미터
                - **filter** (String) : 필터 기준 (daily, weekly, monthly)
                - **startDate** (LocalDate) : 시작 날짜 (YYYY-MM-DD)
                
                ### 반환값
                - **List<Post>** : 인기 게시물 리스트
            """
  )
  @GetMapping
  ResponseEntity<List<Post>> getBestPosts(CustomUserDetails customUserDetails,
      @RequestParam String filter,
      @RequestParam LocalDate startDate);
}
