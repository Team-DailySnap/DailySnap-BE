package onepiece.dailysnapbackend.controller.keyword;

import io.swagger.v3.oas.annotations.Operation;
import onepiece.dailysnapbackend.object.dto.CustomUserDetails;
import onepiece.dailysnapbackend.object.dto.DailyKeywordResponse;
import onepiece.dailysnapbackend.object.dto.KeywordFilterRequest;
import onepiece.dailysnapbackend.object.dto.KeywordFilterResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface KeywordControllerDocs {

  @Operation(
      summary = "키워드 필터링",
      description = """
      
          이 API는 인증이 필요합니다.

          ### 요청 파라미터
          - **keyword** (String) : 키워드 텍스트 (필수X)
          - **category** (KeywordCategory) : 키워드 카테고리 (필수X)
          - **providedDate** (LocalDate) : 제공된 날짜 (YYYY-MM-DD) (필수X)
          - **isUsed** (Boolean) : 사용 여부 (필수X)
          - **pageNumber** (int, 기본값: 0) : 페이지 번호
          - **pageSize** (int, 기본값: 100, 최대 100) : 페이지당 키워드 개수
          - **sortField** (String, 기본값: created_date) : 정렬 기준 (created_date, provided_date, keyword)
          - **sortDirection** (String, 기본값: DESC) : 정렬 방향 (ASC, DESC)

          ### 반환값
          - **keyword** (String) : 키워드 텍스트
          - **category** (String) : 키워드 카테고리
          - **providedDate** (LocalDate) : 제공된 날짜
          
          """
  )
  @PostMapping
  ResponseEntity<Page<KeywordFilterResponse>> filteredKeywords(
      CustomUserDetails userDetails,
      @RequestBody KeywordFilterRequest request
  );

  @Operation(
      summary = "오늘의 키워드",
      description = """
          
          이 API는 인증이 필요합니다.
          
          ### 요청 파라미터
          - 없음
          
          ### 반환값
          - **keyword** (String) : 키워드
          - **category** (KeywordCategory) : 키워드 카테고리
          - **providedDate** (LocalDate) : 제공한 날짜
          
          """
  )
  ResponseEntity<DailyKeywordResponse> getDailyKeyword();
}
