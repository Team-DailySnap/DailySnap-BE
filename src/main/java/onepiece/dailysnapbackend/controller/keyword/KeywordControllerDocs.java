package onepiece.dailysnapbackend.controller.keyword;

import io.swagger.v3.oas.annotations.Operation;
import onepiece.dailysnapbackend.object.dto.CustomUserDetails;
import onepiece.dailysnapbackend.object.dto.KeywordFilterRequest;
import onepiece.dailysnapbackend.object.dto.KeywordFilterResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;

public interface KeywordControllerDocs {

  @Operation(
      summary = "키워드 필터링 (페이징 및 정렬 지원)",
      description = """
          이 API는 인증이 필요합니다.
          
          ### 요청 파라미터 (Query Parameters)
          - **keyword** (String, 선택): 키워드 텍스트 필터링 (빈 값일 경우 전체 조회)
          - **category** (KeywordCategory, 선택): 키워드 카테고리
          - **providedDate** (LocalDate, 선택): 제공된 날짜
          - **isUsed** (Boolean, 선택): 사용 여부
          - **pageNumber** (int, 기본값: 0): 페이지 번호 (0부터 시작)
          - **pageSize** (int, 기본값: 30, 최대 100): 페이지당 키워드 개수
          - **sortField** (String, 기본값: `created_date`): 정렬 기준 (`created_date`, `provided_date`, `keyword`)
          - **sortDirection** (String, 기본값: `DESC`): 정렬 방향 (`ASC`, `DESC`)
          
          ### 반환값
          - **keyword** (String): 키워드 텍스트
          - **category** (KeywordCategory): 키워드 카테고리
          - **providedDate** (LocalDate): 제공된 날짜
          - **isUsed** (Boolean, 선택): 사용 여부
          """
  )
  ResponseEntity<Page<KeywordFilterResponse>> filteredKeywords(
      CustomUserDetails userDetails,
      @ModelAttribute KeywordFilterRequest request
  );
}
