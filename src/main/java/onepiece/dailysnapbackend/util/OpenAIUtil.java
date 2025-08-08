package onepiece.dailysnapbackend.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.experimental.UtilityClass;
import onepiece.dailysnapbackend.object.constants.KeywordCategory;
import onepiece.dailysnapbackend.util.exception.CustomException;
import onepiece.dailysnapbackend.util.exception.ErrorCode;

@UtilityClass
public class OpenAIUtil {

  /**
   * 주어진 카테고리에 맞춰, 한국어 키워드 & 영어 번역 키워드 쌍을
   * JSON 배열(객체 요소)로 반환해 달라는 프롬프트를 생성합니다.
   */
  public String getPrompt(KeywordCategory category) {
    return String.format("""
        '%s' 카테고리에 해당하는 사진 촬영 키워드 100개를 추천하고,
        각각의 한글 키워드에 대응하는 영어 번역을 함께 제공합니다.
        결과를 JSON 배열 형식으로 반환해주세요.
        각 요소는 객체이며, "koreanKeyword"와 "englishKeyword" 필드를 가집니다.
        해당 키워드에 맞는 사진을 업로드하는 어플리케이션이므로, 키워드 선택이 매우 중요합니다.
        사진을 업로드 하기 적합한 키워드 위주로 출력해주세요.
        한국어 단어를 먼저 출력하며, 이후 해당 한국어 단어에 맞는 영어 단어를 번역하여 출력합니다.
        예:
        [
          {"koreanKeyword":"벚꽃","englishKeyword":"cherry blossom"},
          {"koreanKeyword":"봄","englishKeyword":"spring"},
          ...
        ]
        """, category);
  }

  /**
   * OpenAI 요청 JSON 문자열 생성
   */
  public String buildRequestBody(String model, String prompt, int maxTokens, ObjectMapper mapper) {
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
