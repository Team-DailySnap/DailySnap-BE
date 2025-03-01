package onepiece.dailysnapbackend.service.keyword;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import onepiece.dailysnapbackend.object.constants.KeywordCategory;
import onepiece.dailysnapbackend.object.postgres.Keyword;
import onepiece.dailysnapbackend.repository.postgres.KeywordRepository;
import onepiece.dailysnapbackend.util.OpenAIUtil;
import onepiece.dailysnapbackend.util.exception.CustomException;
import onepiece.dailysnapbackend.util.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
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
   *  OpenAI API를 호출하여 새로운 키워드를 생성하고 저장
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void generateKeywords(KeywordCategory category) {
    log.info("[OpenAIKeywordService] '{}' 카테고리 키워드 생성 시작", category);

    List<String> keywords = requestOpenAI(category, createPrompt(category));

    if (keywords.isEmpty()) {
      log.error("[OpenAIKeywordService] '{}' 카테고리 키워드 생성 실패 (응답 없음)", category);
      throw new CustomException(ErrorCode.INVALID_OPENAI_RESPONSE);
    }

    saveKeywords(category, keywords);
  }

  /**
   *  OpenAI 요청 프롬프트 생성
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
   *  OpenAI API 호출 및 응답 처리
   */
  private List<String> requestOpenAI(KeywordCategory category, String prompt) {
    log.info("[OpenAIKeywordService] '{}' 카테고리 OpenAI API 요청 시작", category);

    try {
      String requestBody = OpenAIUtil.buildRequestBody(MODEL, prompt, 5000, new ObjectMapper());
      log.info("[OpenAIKeywordService] 요청 JSON: {}", requestBody);

      ResponseEntity<String> responseEntity = restTemplate.exchange(
          OPENAI_URL, HttpMethod.POST, new HttpEntity<>(requestBody, createHeaders()), String.class);

      log.info("[OpenAIKeywordService] OpenAI 응답 수신 완료");
      return parseKeywords(category, responseEntity.getBody());
    } catch (Exception e) {
      log.error("[OpenAIKeywordService] OpenAI API 요청 오류: {}", e.getMessage(), e);
      throw new CustomException(ErrorCode.OPENAI_SERVICE_UNAVAILABLE);
    }
  }

  /**
   *  OpenAI 요청 헤더 생성
   */
  private HttpHeaders createHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setBearerAuth(openAiApiKey);
    return headers;
  }

  /**
   *  OpenAI 응답에서 키워드 파싱
   */
  private List<String> parseKeywords(KeywordCategory category, String response) {
    try {
      JsonNode root = new ObjectMapper().readTree(response);
      JsonNode choicesNode = root.path("choices");

      if (!choicesNode.isArray() || choicesNode.isEmpty()) {
        log.warn("[OpenAIKeywordService] '{}' 카테고리 응답에서 키워드를 찾을 수 없음", category);
        throw new CustomException(ErrorCode.INVALID_OPENAI_RESPONSE);
      }

      String content = choicesNode.get(0).path("message").path("content").asText();
      log.debug("[OpenAIKeywordService] 응답된 키워드 원본: {}", content);

      JsonNode keywordArray = new ObjectMapper().readTree(content);
      List<String> keywords = new ArrayList<>();

      if (keywordArray.isArray()) {
        keywordArray.forEach(node -> {
          String keyword = node.asText().trim();
          if (!keyword.isEmpty()) keywords.add(keyword);
        });
      } else {
        throw new CustomException(ErrorCode.INVALID_OPENAI_RESPONSE);
      }

      log.info("[OpenAIKeywordService] '{}' 카테고리 키워드 {}개 파싱 완료", category, keywords.size());
      return keywords;
    } catch (Exception e) {
      log.error("[OpenAIKeywordService] OpenAI 응답 파싱 오류: {}", e.getMessage(), e);
      throw new CustomException(ErrorCode.INVALID_OPENAI_RESPONSE);
    }
  }

  /**
   *  중복 체크 후 키워드 저장
   */
  private void saveKeywords(KeywordCategory category, List<String> keywords) {
    log.info("[OpenAIKeywordService] '{}' 카테고리 키워드 저장 시작", category);

    Set<String> uniqueKeywords = new HashSet<>(keywords);
    List<Keyword> keywordEntities = new ArrayList<>();

    uniqueKeywords.forEach(keyword -> {
      if (!keywordRepository.existsByKeyword(keyword)) {
        keywordEntities.add(Keyword.builder()
            .keyword(keyword)
            .category(category)
            .isUsed(false)
            .build());
      } else {
        log.warn("[OpenAIKeywordService] '{}' 키워드는 이미 존재하여 저장하지 않음", keyword);
      }
    });

    if (!keywordEntities.isEmpty()) {
      keywordRepository.saveAll(keywordEntities);
      keywordRepository.flush();
      log.info("[OpenAIKeywordService] '{}' 카테고리 {}개 키워드 저장 완료", category, keywordEntities.size());
    } else {
      log.warn("[OpenAIKeywordService] '{}' 카테고리에 추가 저장할 키워드 없음", category);
    }
  }
}
