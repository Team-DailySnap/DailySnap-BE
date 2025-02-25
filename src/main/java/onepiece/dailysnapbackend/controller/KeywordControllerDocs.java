package onepiece.dailysnapbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import onepiece.dailysnapbackend.object.dto.DailyKeywordResponse;
import org.springframework.http.ResponseEntity;

public interface KeywordControllerDocs {

  @Operation(
      summary = "오늘의 키워드",
      description = """
          
          이 API는 인증이 필요합니다.
          
          ### 요청 파라미터
          - 없음
          
          ### 반환값
          - **keyword** (Keyword) : 키워드
          - **date** (LocalDate) : 날짜
          
          """
  )
  ResponseEntity<DailyKeywordResponse> getDailyKeyword();
}
