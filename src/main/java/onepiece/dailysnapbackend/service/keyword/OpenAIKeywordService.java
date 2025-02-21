package onepiece.dailysnapbackend.service.keyword;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import onepiece.dailysnapbackend.object.constants.KeywordCategory;
import onepiece.dailysnapbackend.object.postgres.Keyword;
import onepiece.dailysnapbackend.repository.postgres.KeywordRepository;
import onepiece.dailysnapbackend.util.QuestionUtil;
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
  private final RestTemplate restTemplate; // RestTemplate 추가

  @Value("${openai.api.key}")
  private String openAiApiKey;

  private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";
  private static final String MODEL = "gpt-4";

  /**
   * 🔹 OpenAI API를 호출하여 새로운 키워드 목록을 생성하고 저장
   */
  public void generateKeywords(KeywordCategory category) {
    String prompt = createPrompt(category);
    List<String> keywords = requestOpenAI(prompt);

    for (String keyword : keywords) {
      keywordRepository.save(
          Keyword.builder()
              .keyword(keyword)
              .category(category)
              .createdDate(LocalDateTime.now())
              .isUsed(false)
              .build()
      );
    }

    log.info("[OpenAIKeywordService] '{}' 카테고리에 대한 새로운 키워드 {}개 저장 완료", category.name(), keywords.size());
  }

  /**
   * 🔹 카테고리에 맞는 OpenAI 요청 프롬프트 생성
   */
  private String createPrompt(KeywordCategory category) {
    return switch (category) {
      case SEASON_SPRING -> QuestionUtil.SPRING;
      case SEASON_SUMMER -> QuestionUtil.SUMMER;
      case SEASON_AUTUMN -> QuestionUtil.AUTUMN;
      case SEASON_WINTER -> QuestionUtil.WINTER;
      case TRAVEL -> QuestionUtil.TRAVEL;
      case DAILY -> QuestionUtil.DAILY;
      case ABSTRACT -> QuestionUtil.ABSTRACT;
      case RANDOM -> QuestionUtil.RANDOM;
      default -> throw new CustomException(ErrorCode.UNSUPPORTED_CATEGORY);
    };
  }

  /**
   * 🔹 OpenAI API 요청 및 응답 처리
   */
  private List<String> requestOpenAI(String prompt) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setBearerAuth(openAiApiKey);

    String requestBody = String.format("""
            {
                "model": "%s",
                "messages": [
                    {"role": "system", "content": "너는 사진작가이며, 사람들이 좋은 사진을 찍을 수 있도록 키워드를 제공하는 역할을 한다."},
                    {"role": "user", "content": "%s"}
                ],
                "max_tokens": 100
            }
            """, MODEL, prompt);

    HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

    try {
      String response = restTemplate.exchange(OPENAI_URL, HttpMethod.POST, entity, String.class).getBody();
      return parseKeywords(response);
    } catch (Exception e) {
      log.error("🚨 OpenAI API 요청 오류: {}", e.getMessage());
      throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * 🔹 OpenAI 응답에서 키워드 추출
   */
  private List<String> parseKeywords(String response) {
    List<String> keywords = new ArrayList<>();
    try {
      JsonNode root = new ObjectMapper().readTree(response);
      String content = root.path("choices").get(0).path("message").path("content").asText();
      String[] keywordArray = content.split("\n");

      for (String keyword : keywordArray) {
        keywords.add(keyword.trim());
      }

      log.info("[OpenAIKeywordService] 키워드 파싱 완료: {}개", keywords.size());
    } catch (Exception e) {
      log.error("🚨 OpenAI 응답 파싱 오류: {}", e.getMessage());
      throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
    }
    return keywords;
  }
}
