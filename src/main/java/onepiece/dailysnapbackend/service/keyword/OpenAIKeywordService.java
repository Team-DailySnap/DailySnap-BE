package onepiece.dailysnapbackend.service.keyword;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import onepiece.dailysnapbackend.object.constants.KeywordCategory;
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

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenAIKeywordService {

  private final KeywordRepository keywordRepository;
  private final WebClient webClient = WebClient.builder().build();
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Value("${openai.api.key}")
  private String openAiApiKey;

  private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";
  private static final String MODEL = "gpt-4";

  @Transactional
  public void generateKeywords(KeywordCategory category) {
    log.info("'{}' 카테고리 키워드 생성 시작", category);
    List<String> keywords = requestOpenAI(category);

    if (keywords.isEmpty()) {
      log.error("'{}' 카테고리 키워드 생성 실패 (응답 없음)", category);
      throw new CustomException(ErrorCode.INVALID_OPENAI_RESPONSE);
    }

    saveKeywords(category, keywords);
  }

  private List<String> requestOpenAI(KeywordCategory category) {
    String prompt = OpenAIUtil.getPrompt(category);
    String requestBody = OpenAIUtil.buildRequestBody(MODEL, prompt, 5000, objectMapper);

    try {
      log.info("OpenAI 요청 JSON: {}", requestBody);
      String response = webClient.post()
          .uri(OPENAI_URL)
          .headers(headers -> headers.addAll(createHeaders()))
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(requestBody)
          .retrieve()
          .bodyToMono(String.class)
          .block();

      log.info("OpenAI 응답 수신 완료");
      return parseKeywords(category, response);
    } catch (Exception e) {
      log.error("OpenAI API 요청 오류: {}", e.getMessage(), e);
      throw new CustomException(ErrorCode.OPENAI_SERVICE_UNAVAILABLE);
    }
  }

  private HttpHeaders createHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setBearerAuth(openAiApiKey);
    return headers;
  }

  private List<String> parseKeywords(KeywordCategory category, String response) {
    try {
      JsonNode root = objectMapper.readTree(response);
      JsonNode choicesNode = root.path("choices");

      if (!choicesNode.isArray() || choicesNode.isEmpty()) {
        log.error("'{}' 카테고리 응답에서 키워드를 찾을 수 없음", category);
        throw new CustomException(ErrorCode.INVALID_OPENAI_RESPONSE);
      }

      String content = choicesNode.get(0).path("message").path("content").asText();
      JsonNode keywordArray = objectMapper.readTree(content);

      List<String> keywords = new ArrayList<>();
      keywordArray.forEach(node -> keywords.add(node.asText().trim()));

      log.info("'{}' 카테고리 키워드 {}개 파싱 완료", category, keywords.size());
      return keywords;
    } catch (Exception e) {
      log.error("OpenAI 응답 파싱 오류: {}", e.getMessage(), e);
      throw new CustomException(ErrorCode.INVALID_OPENAI_RESPONSE);
    }
  }

  /**
   *  중복 체크 후 키워드 저장
   */
  private void saveKeywords(KeywordCategory category, List<String> keywords) {
    log.info("'{}' 카테고리 키워드 저장 시작", category);

    Set<String> uniqueKeywords = new HashSet<>(keywords);
    List<Keyword> keywordEntities = new ArrayList<>();

    uniqueKeywords.forEach(keyword -> {
      if (!keywordRepository.existsByKoreanKeyword(keyword)) {
        keywordEntities.add(Keyword.builder()
            .koreanKeyword(keyword)
            .keywordCategory(category)
            .used(false)
            .build());
      } else {
        log.error("'{}' 키워드는 이미 존재하여 저장하지 않음", keyword);
      }
    });

    if (!keywordEntities.isEmpty()) {
      keywordRepository.saveAll(keywordEntities);
      keywordRepository.flush();
      log.info("'{}' 카테고리 {}개 키워드 저장 완료", category, keywordEntities.size());
    } else {
      log.error("'{}' 카테고리에 추가 저장할 키워드 없음", category);
    }
  }
}
