package onepiece.dailysnapbackend.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import onepiece.dailysnapbackend.object.dto.DailyKeywordResponse;
import onepiece.dailysnapbackend.service.KeywordService;
import onepiece.dailysnapbackend.util.log.LogMonitoringInvocation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/keyword")
@Tag(
    name = "키워드 관리 API",
    description = "키워드 자동 생성 및 관리 API 제공"
)
public class KeywordController implements KeywordControllerDocs{

  private final KeywordService keywordService;

  @Override
  @GetMapping("/daily")
  @LogMonitoringInvocation
  public ResponseEntity<DailyKeywordResponse> getDailyKeyword() {
    return ResponseEntity.ok(keywordService.getDailyKeyword());
  }
}