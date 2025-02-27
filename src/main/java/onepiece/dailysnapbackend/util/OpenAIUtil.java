package onepiece.dailysnapbackend.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import onepiece.dailysnapbackend.util.exception.CustomException;
import onepiece.dailysnapbackend.util.exception.ErrorCode;

/**
 * OpenAI 관련 유틸 클래스
 * - 추천 프롬프트 상수 (JSON 배열로 반환 요청)
 * - OpenAI 요청 JSON 문자열 생성 메서드
 */
public class OpenAIUtil {

  // 추천 프롬프트 (JSON 배열 반환 요청 추가)
  public static final String SPRING = """
      봄과 관련된 사진 촬영 키워드 100개를 추천해줘.
      JSON 배열 형식으로 반환하고, 반드시 한 단어로 구성해야 해.
      예: ["벚꽃", "봄", "피크닉"]
      """;

  public static final String SUMMER = """
      여름과 관련된 사진 촬영 키워드 100개를 추천해줘.
      JSON 배열 형식으로 반환하고, 반드시 한 단어로 구성해야 해.
      예: ["해변", "태양", "수영"]
      """;

  public static final String AUTUMN = """
      가을과 관련된 사진 촬영 키워드 100개를 추천해줘.
      JSON 배열 형식으로 반환하고, 반드시 한 단어로 구성해야 해.
      예: ["단풍", "낙엽", "캠핑"]
      """;

  public static final String WINTER = """
      겨울과 관련된 사진 촬영 키워드 100개를 추천해줘.
      JSON 배열 형식으로 반환하고, 반드시 한 단어로 구성해야 해.
      예: ["눈", "크리스마스", "코트"]
      """;

  public static final String TRAVEL = """
      여행지에서 사진을 찍기 좋은 키워드 100개를 추천해줘.
      JSON 배열 형식으로 반환하고, 반드시 한 단어로 구성해야 해.
      예: ["랜드마크", "야경", "자연"]
      """;

  public static final String DAILY = """
      일상에서 찍을 수 있는 사진 촬영 키워드 100개를 추천해줘.
      JSON 배열 형식으로 반환하고, 반드시 한 단어로 구성해야 해.
      예: ["커피", "독서", "거리"]
      """;

  public static final String ABSTRACT = """
      추상적인 사진 촬영 키워드 100개를 추천해줘.
      JSON 배열 형식으로 반환하고, 반드시 한 단어로 구성해야 해.
      예: ["패턴", "반사", "그림자"]
      """;

  public static final String RANDOM = """
      무작위로 사진을 찍기 좋은 키워드 100개를 추천해줘.
      JSON 배열 형식으로 반환하고, 반드시 한 단어로 구성해야 해.
      예: ["고요", "역동", "감성"]
      """;

  /**
   * OpenAI 요청에 사용할 JSON 문자열을 생성
   *
   * @param model      사용 모델
   * @param prompt     사용자 프롬프트
   * @param maxTokens  최대 토큰 수
   * @param mapper     ObjectMapper 인스턴스
   * @return 생성된 JSON 문자열
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
