package onepiece.dailysnapbackend.controller.keyword;

import io.swagger.v3.oas.annotations.Operation;
import java.time.LocalDate;
import java.util.List;
import onepiece.dailysnapbackend.object.constants.KeywordCategory;
import onepiece.dailysnapbackend.object.dto.KeywordRequest;
import org.springframework.http.ResponseEntity;

public interface KeywordControllerDocs {

  @Operation(
      summary = "특정 카테고리 키워드 목록 조회",
      description = """
          
          특정 카테고리에 해당하는 키워드 목록을 조회합니다.
          
          ### 요청 파라미터
          - `category` (KeywordCategory) → 조회할 키워드 카테고리
          
          ### 반환값
          - `200 OK` → `List<KeywordRequest>` (해당 카테고리의 키워드 목록)
          
          """
  )
  ResponseEntity<List<KeywordRequest>> getKeywordsByCategory(KeywordCategory category);

  @Operation(
      summary = "특정 날짜의 키워드 조회",
      description = """
          
          특정 날짜(오늘 제외)에 제공된 키워드 목록을 조회합니다.
          
          ### 요청 파라미터
          - `date` (LocalDate) → 조회할 날짜
          
          ### 반환값
          - `200 OK` → `List<KeywordRequest>` (해당 날짜에 제공된 키워드 목록)
          
          """
  )
  ResponseEntity<List<KeywordRequest>> getKeywordsByDate(LocalDate date);
}
