package onepiece.dailysnapbackend.service.keyword;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import onepiece.dailysnapbackend.object.constants.KeywordCategory;
import onepiece.dailysnapbackend.object.dto.KeywordPair;
import onepiece.dailysnapbackend.object.postgres.Keyword;
import onepiece.dailysnapbackend.repository.postgres.KeywordRepository;
import onepiece.dailysnapbackend.util.OpenAIUtil;
import onepiece.dailysnapbackend.util.exception.CustomException;
import onepiece.dailysnapbackend.util.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenAIKeywordService {

  private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";
  private static final String MODEL = "gpt-4";
  private final KeywordRepository keywordRepository;
  private final WebClient webClient = WebClient.builder().build();
  private final ObjectMapper objectMapper = new ObjectMapper();
  @Value("${openai.api.key}")
  private String openAiApiKey;

  @Transactional
  public void generateKeywords(KeywordCategory category) {
    log.info("'{}' 카테고리 키워드 생성 시작", category);
    List<KeywordPair> pairs = requestOpenAI(category);

    if (pairs.isEmpty()) {
      log.error("'{}' 카테고리 키워드 생성 실패 (응답 없음)", category);
      throw new CustomException(ErrorCode.INVALID_OPENAI_RESPONSE);
    }

    saveKeywords(category, pairs);
  }

  private List<KeywordPair> requestOpenAI(KeywordCategory category) {
    String prompt = OpenAIUtil.getPrompt(category);
    String requestBody = OpenAIUtil.buildRequestBody(MODEL, prompt, 5000, objectMapper);

    try {
      log.debug("OpenAI 요청 JSON: {}", requestBody);
      String response = webClient.post()
          .uri(OPENAI_URL)
          .headers(this::applyAuthHeaders)
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(requestBody)
          .retrieve()
          .bodyToMono(String.class)
          .block();

      log.debug("OpenAI 응답 수신 완료");
      return parseKeywords(response);
    } catch (Exception e) {
      log.error("OpenAI API 요청 오류: {}", e.getMessage(), e);
      throw new CustomException(ErrorCode.OPENAI_SERVICE_UNAVAILABLE);
    }
  }

  private void applyAuthHeaders(HttpHeaders headers) {
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setBearerAuth(openAiApiKey);
  }

  private List<KeywordPair> parseKeywords(String response) {
    try {
      JsonNode root = objectMapper.readTree(response);
      JsonNode choices = root.path("choices");
      if (!choices.isArray() || choices.isEmpty()) {
        throw new CustomException(ErrorCode.INVALID_OPENAI_RESPONSE);
      }
      String content = choices.get(0).path("message").path("content").asText();
      JsonNode arrayNode = objectMapper.readTree(content);

      List<KeywordPair> list = new ArrayList<>();
      arrayNode.forEach(node -> {
        String ko = node.path("koreanKeyword").asText(null);
        String en = node.path("englishKeyword").asText(null);
        if (ko != null && en != null) {
          list.add(new KeywordPair(ko.trim(), en.trim()));
        }
      });
      return list;
    } catch (Exception e) {
      log.error("OpenAI 응답 파싱 오류: {}", e.getMessage(), e);
      throw new CustomException(ErrorCode.INVALID_OPENAI_RESPONSE);
    }
  }

  private void saveKeywords(KeywordCategory category, List<KeywordPair> pairs) {
    log.info("'{}' 카테고리 키워드 저장 시작", category);

    // 1) DB 에서 해당 카테고리의 가장 마지막 제공일 조회
    LocalDate lastDate = keywordRepository.findMaxProvidedDateByCategory(category);

    // 2) 첫 키워드의 제공일: 마지막일+1일 혹은 오늘
    LocalDate currentDate = (lastDate != null)
        ? lastDate.plusDays(1)
        : LocalDate.now(ZoneId.of("Asia/Seoul"));

    Set<String> seen = new HashSet<>();
    List<Keyword> entities = new ArrayList<>();

    for (KeywordPair pair : pairs) {
      String ko = pair.koreanKeyword();

      // 중복 한글 키워드 필터링
      if (seen.add(ko) && !keywordRepository.existsByKoreanKeyword(ko)) {
        // 3) 하나의 키워드를 저장할 때마다 currentDate 를 세팅
        entities.add(Keyword.builder()
            .koreanKeyword(ko)
            .englishKeyword(pair.englishKeyword())
            .keywordCategory(category)
            .providedDate(currentDate)
            .used(false)
            .build());

        // 4) 다음 키워드는 하루 뒤 날짜로 설정
        currentDate = currentDate.plusDays(1);
      } else {
        log.debug("'{}' 키워드는 중복 또는 이미 존재하여 저장하지 않음", ko);
      }
    }

    if (!entities.isEmpty()) {
      keywordRepository.saveAll(entities);
      keywordRepository.flush();
      log.info("'{}' 카테고리에 {}개 키워드 저장 완료 ({} ~ {})",
          category,
          entities.size(),
          entities.get(0).getProvidedDate(),
          entities.get(entities.size() - 1).getProvidedDate());
    } else {
      log.warn("'{}' 카테고리에 추가 저장할 키워드 없음", category);
    }
  }
}