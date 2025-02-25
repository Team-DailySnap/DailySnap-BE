package onepiece.dailysnapbackend.util.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

  // GLOBAL

  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버에 문제가 발생했습니다."),

  INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),

  ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근이 거부되었습니다."),

  // AUTH

  UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증에 실패했습니다."),

  MISSING_AUTH_TOKEN(HttpStatus.UNAUTHORIZED, "인증 토큰이 필요합니다."),

  INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 엑세스 토큰입니다."),

  INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다."),

  EXPIRED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "엑세스 토큰이 만료되었습니다."),

  EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "리프레시 토큰이 만료되었습니다."),

  REFRESH_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "리프레시 토큰을 찾을 수 없습니다."),

  DUPLICATE_USERNAME(HttpStatus.BAD_REQUEST, "이미 가입된 회원입니다"),

  // MEMBER

  MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "회원을 찾을 수 없습니다."),

  // KEYWORD

  KEYWORD_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 키워드를 찾을 수 없습니다."),
  UNSUPPORTED_CATEGORY(HttpStatus.BAD_REQUEST, "지원되지 않는 키워드 카테고리입니다."),
  KEYWORD_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "키워드 저장에 실패했습니다."),
  JSON_SERIALIZATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "키워드 요청 JSON 직렬화 실패"),
  OPENAI_API_REQUEST_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "OpenAI API 요청에 실패했습니다."),
  OPENAI_RESPONSE_PARSING_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "OpenAI 응답 파싱에 실패했습니다.");


  private final HttpStatus status;
  private final String message;
}