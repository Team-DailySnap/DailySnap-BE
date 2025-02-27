package onepiece.dailysnapbackend.service.keyword;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import onepiece.dailysnapbackend.object.constants.KeywordCategory;
import onepiece.dailysnapbackend.object.postgres.Keyword;
import onepiece.dailysnapbackend.repository.postgres.KeywordRepository;
import onepiece.dailysnapbackend.util.OpenAIUtil;
import onepiece.dailysnapbackend.util.exception.CustomException;
import onepiece.dailysnapbackend.util.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenAIKeywordService {

  private final KeywordRepository keywordRepository;
  private final RestTemplate restTemplate;

  @Value("${openai.api.key}")
  private String openAiApiKey;

  private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";
  private static final String MODEL = "gpt-4";

  /**
   * OpenAI API를 호출하여 새로운 키워드 목록을 생성하고 저장
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW) // 새 트랜잭션에서 실행
  public void generateKeywords(KeywordCategory category) {
    log.info("[OpenAIKeywordService] '{}' 카테고리에 대한 키워드 생성 요청 시작", category);

    String prompt = createPrompt(category);
    List<String> keywords = requestOpenAI(category, prompt);

    if (keywords.isEmpty()) {
      log.error("[OpenAIKeywordService] '{}' 카테고리에 대한 키워드 생성 실패 (응답 비어있음)", category);
      throw new CustomException(ErrorCode.OPENAI_RESPONSE_PARSING_FAILED);
    }

    log.info("[OpenAIKeywordService] '{}' 카테고리의 새로운 키워드 {}개 저장 시작", category.name(), keywords.size());

    List<Keyword> keywordEntities = new ArrayList<>();
    for (String keyword : keywords) {
      keywordEntities.add(
          Keyword.builder()
              .keyword(keyword)
              .category(category)
              .isUsed(false)
              .build()
      );
    }

    try {
      keywordRepository.saveAll(keywordEntities);
      keywordRepository.flush();
      log.info("[OpenAIKeywordService] '{}' 카테고리에 대한 새로운 키워드 {}개 저장 완료", category.name(), keywords.size());
    } catch (Exception e) {
      log.error("[OpenAIKeywordService] ❌ 키워드 저장 실패: {}", e.getMessage(), e);
      throw new CustomException(ErrorCode.KEYWORD_SAVE_FAILED);
    }
  }



  /**
   * 카테고리에 맞는 OpenAI 요청 프롬프트 생성
   */
  private String createPrompt(KeywordCategory category) {
    return switch (category) {
      case SPRING -> OpenAIUtil.SPRING;
      case SUMMER -> OpenAIUtil.SUMMER;
      case AUTUMN -> OpenAIUtil.AUTUMN;
      case WINTER -> OpenAIUtil.WINTER;
      case TRAVEL -> OpenAIUtil.TRAVEL;
      case DAILY -> OpenAIUtil.DAILY;
      case ABSTRACT -> OpenAIUtil.ABSTRACT;
      case RANDOM -> OpenAIUtil.RANDOM;
      default -> throw new CustomException(ErrorCode.UNSUPPORTED_CATEGORY);
    };
  }

  /**
   * OpenAI API 요청 및 응답 처리
   */
  private List<String> requestOpenAI(KeywordCategory category, String prompt) {
    log.info("[OpenAIKeywordService] '{}' 카테고리에 대한 OpenAI API 호출 시작", category);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setBearerAuth(openAiApiKey);

    ObjectMapper mapper = new ObjectMapper();
    String requestBody;
    try {
      requestBody = OpenAIUtil.buildRequestBody(MODEL, prompt, 5000, mapper);
      log.debug("[OpenAIKeywordService] 요청 JSON: {}", requestBody);
    } catch (Exception e) {
      log.error("[OpenAIKeywordService] JSON 빌드 오류: {}", e.getMessage(), e);
      throw new CustomException(ErrorCode.JSON_SERIALIZATION_FAILED);
    }

    HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

    try {
      String response = restTemplate.exchange(OPENAI_URL, HttpMethod.POST, entity, String.class).getBody();
      log.info("[OpenAIKeywordService] OpenAI 응답 수신 완료");
      log.debug("[OpenAIKeywordService] 응답 JSON: {}", response);
      return parseKeywords(category, response);
    } catch (Exception e) {
      log.error("[OpenAIKeywordService] OpenAI API 요청 오류: {}", e.getMessage(), e);
      throw new CustomException(ErrorCode.OPENAI_API_REQUEST_FAILED);
    }
  }

  /**
   * OpenAI 응답에서 키워드 추출 (JSON 배열 방식)
   */
  private List<String> parseKeywords(KeywordCategory category, String response) {
    List<String> keywords = new ArrayList<>();
    try {
      JsonNode root = new ObjectMapper().readTree(response);
      JsonNode choicesNode = root.path("choices");

      if (choicesNode.isArray() && choicesNode.size() > 0) {
        String content = choicesNode.get(0).path("message").path("content").asText();
        log.info("[OpenAIKeywordService] 응답된 키워드 원본: {}", content);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode keywordArray = mapper.readTree(content);

        if (keywordArray.isArray()) {
          for (JsonNode node : keywordArray) {
            String keyword = node.asText().trim();
            if (!keyword.isEmpty()) {
              keywords.add(keyword);
            }
          }
        } else {
          log.warn("[OpenAIKeywordService] OpenAI 응답이 JSON 배열 형식이 아님: {}", content);
          throw new CustomException(ErrorCode.OPENAI_RESPONSE_PARSING_FAILED);
        }
      } else {
        log.warn("[OpenAIKeywordService] '{}' 카테고리의 응답에서 키워드를 찾을 수 없음", category);
        throw new CustomException(ErrorCode.OPENAI_RESPONSE_PARSING_FAILED);
      }

      log.info("[OpenAIKeywordService] '{}' 카테고리 키워드 {}개 파싱 완료", category, keywords.size());
    } catch (Exception e) {
      log.error("[OpenAIKeywordService] OpenAI 응답 파싱 오류: {}", e.getMessage(), e);
      throw new CustomException(ErrorCode.OPENAI_RESPONSE_PARSING_FAILED);
    }
    return keywords;
  }
}
