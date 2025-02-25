package onepiece.dailysnapbackend.service.keyword;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
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
  @Transactional
  public void generateKeywords(KeywordCategory category) {
    String prompt = createPrompt(category);
    List<String> keywords = requestOpenAI(prompt);

    for (String keyword : keywords) {
      try {
        keywordRepository.save(
            Keyword.builder()
                .keyword(keyword)
                .category(category)
                .isUsed(false)
                .build()
        );
      } catch (Exception e) {
        log.error("키워드 저장 실패: {}", e.getMessage());
        throw new CustomException(ErrorCode.KEYWORD_SAVE_FAILED);
      }
    }

    log.info("[OpenAIKeywordService] '{}' 카테고리에 대한 새로운 키워드 {}개 저장 완료", category.name(), keywords.size());
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
  private List<String> requestOpenAI(String prompt) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setBearerAuth(openAiApiKey);

    ObjectMapper mapper = new ObjectMapper();
    String requestBody;
    try {
      // 여기서 OpenAIUtil.buildRequestBody는 JSON 직렬화에 실패할 경우 CustomException(JSON_SERIALIZATION_FAILED)을 던집니다.
      requestBody = OpenAIUtil.buildRequestBody(MODEL, prompt, 100, mapper);
    } catch (Exception e) {
      log.error("JSON 빌드 오류: {}", e.getMessage());
      throw new CustomException(ErrorCode.JSON_SERIALIZATION_FAILED);
    }

    HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

    try {
      String response = restTemplate.exchange(OPENAI_URL, HttpMethod.POST, entity, String.class).getBody();
      log.info("[OpenAIKeywordService] Raw OpenAI Response: {}", response);
      return parseKeywords(response);
    } catch (Exception e) {
      log.error("OpenAI API 요청 오류: {}", e.getMessage());
      throw new CustomException(ErrorCode.OPENAI_API_REQUEST_FAILED);
    }
  }

  /**
   * OpenAI 응답에서 키워드 추출
   */
  private List<String> parseKeywords(String response) {
    List<String> keywords = new ArrayList<>();
    try {
      JsonNode root = new ObjectMapper().readTree(response);
      String content = root.path("choices").get(0).path("message").path("content").asText();
      String[] keywordArray = content.split("\n");

      for (String keyword : keywordArray) {
        if (!keyword.trim().isEmpty()) {
          keywords.add(keyword.trim());
        }
      }

      log.info("[OpenAIKeywordService] 키워드 파싱 완료: {}개", keywords.size());
    } catch (Exception e) {
      log.error("OpenAI 응답 파싱 오류: {}", e.getMessage());
      throw new CustomException(ErrorCode.OPENAI_RESPONSE_PARSING_FAILED);
    }
    return keywords;
  }
}
