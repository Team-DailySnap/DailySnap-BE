package onepiece.dailysnapbackend.controller.keyword;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import onepiece.dailysnapbackend.object.constants.KeywordCategory;
import onepiece.dailysnapbackend.object.dto.KeywordRequest;
import onepiece.dailysnapbackend.service.KeywordService;
import onepiece.dailysnapbackend.util.log.LogMonitoringInvocation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/keywords")
@Tag(
    name = "키워드 관리 API",
    description = "키워드 자동 생성 및 관리 API 제공"
)
public class KeywordController implements KeywordControllerDocs {

  private final KeywordService keywordService;

  /**
   * 🔹 특정 카테고리의 키워드 목록 조회 (모든 사용자 가능)
   */
  @Override
  @GetMapping
  @LogMonitoringInvocation
  public ResponseEntity<List<KeywordRequest>> getKeywordsByCategory(@RequestParam KeywordCategory category) {
    return ResponseEntity.ok(keywordService.getKeywordsByCategory(category));
  }

  /**
   * 🔹 특정 날짜의 제공된 키워드 조회 (오늘 포함한 이전 날짜 가능) (모든 사용자 가능)
   */
  @Override
  @GetMapping("/history/{date}")
  @LogMonitoringInvocation
  public ResponseEntity<List<KeywordRequest>> getKeywordsByDate(@PathVariable LocalDate date) {
    return ResponseEntity.ok(keywordService.getKeywordsByDate(date));
  }
}
