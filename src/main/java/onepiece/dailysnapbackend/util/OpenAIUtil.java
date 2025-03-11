package onepiece.dailysnapbackend.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import onepiece.dailysnapbackend.object.constants.KeywordCategory;
import onepiece.dailysnapbackend.util.exception.CustomException;
import onepiece.dailysnapbackend.util.exception.ErrorCode;

import java.util.Map;

public class OpenAIUtil {

  private static final Map<KeywordCategory, String> PROMPTS = Map.of(
      KeywordCategory.SPRING, """
            봄과 관련된 사진 촬영 키워드 100개를 추천해줘.
            JSON 배열 형식으로 반환하고, 반드시 한 단어로 구성해야 해.
            예: ["벚꽃", "봄", "피크닉"]
            """,
      KeywordCategory.SUMMER, """
            여름과 관련된 사진 촬영 키워드 100개를 추천해줘.
            JSON 배열 형식으로 반환하고, 반드시 한 단어로 구성해야 해.
            예: ["해변", "태양", "수영"]
            """,
      KeywordCategory.AUTUMN, """
            가을과 관련된 사진 촬영 키워드 100개를 추천해줘.
            JSON 배열 형식으로 반환하고, 반드시 한 단어로 구성해야 해.
            예: ["단풍", "낙엽", "캠핑"]
            """,
      KeywordCategory.WINTER, """
            겨울과 관련된 사진 촬영 키워드 100개를 추천해줘.
            JSON 배열 형식으로 반환하고, 반드시 한 단어로 구성해야 해.
            예: ["눈", "크리스마스", "코트"]
            """,
      KeywordCategory.TRAVEL, """
            여행지에서 사진을 찍기 좋은 키워드 100개를 추천해줘.
            JSON 배열 형식으로 반환하고, 반드시 한 단어로 구성해야 해.
            예: ["랜드마크", "야경", "자연"]
            """,
      KeywordCategory.DAILY, """
            일상에서 찍을 수 있는 사진 촬영 키워드 100개를 추천해줘.
            JSON 배열 형식으로 반환하고, 반드시 한 단어로 구성해야 해.
            예: ["커피", "독서", "거리"]
            """,
      KeywordCategory.ABSTRACT, """
            추상적인 사진 촬영 키워드 100개를 추천해줘.
            JSON 배열 형식으로 반환하고, 반드시 한 단어로 구성해야 해.
            예: ["패턴", "반사", "그림자"]
            """,
      KeywordCategory.RANDOM, """
            무작위로 사진을 찍기 좋은 키워드 100개를 추천해줘.
            JSON 배열 형식으로 반환하고, 반드시 한 단어로 구성해야 해.
            예: ["고요", "역동", "감성"]
            """
  );

  /**
   * KeywordCategory에 맞는 프롬프트 반환
   */
  public static String getPrompt(KeywordCategory category) {
    return PROMPTS.getOrDefault(category, "무작위 키워드를 추천해줘. JSON 배열 형식으로 반환해야 해.");
  }

  /**
   * OpenAI 요청 JSON 문자열 생성
   */
  public static String buildRequestBody(String model, String prompt, int maxTokens, ObjectMapper mapper) {
    ObjectNode requestJson = mapper.createObjectNode();
    requestJson.put("model", model);
    requestJson.put("max_tokens", maxTokens);

    ArrayNode messages = requestJson.putArray("messages");

    ObjectNode systemMessage = mapper.createObjectNode();
    systemMessage.put("role", "system");
    systemMessage.put("content", "너는 사진작가이며, 사람들이 좋은 사진을 찍을 수 있도록 키워드를 JSON 배열로 제공하는 역할을 한다.");
    messages.add(systemMessage);

    ObjectNode userMessage = mapper.createObjectNode();
    userMessage.put("role", "user");
    userMessage.put("content", prompt);
    messages.add(userMessage);

    try {
      return mapper.writeValueAsString(requestJson);
    } catch (Exception e) {
      throw new CustomException(ErrorCode.JSON_SERIALIZATION_FAILED);
    }
  }
}
