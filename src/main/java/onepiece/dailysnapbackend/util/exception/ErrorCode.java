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

  INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 액세스 토큰입니다."),

  INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다."),

  EXPIRED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "액세스 토큰이 만료되었습니다."),

  EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "리프레시 토큰이 만료되었습니다."),

  REFRESH_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "리프레시 토큰을 찾을 수 없습니다."),

  TOKEN_BLACKLISTED(HttpStatus.UNAUTHORIZED, "블랙리스트에 등록된 토큰입니다."),

  DUPLICATE_USERNAME(HttpStatus.BAD_REQUEST, "이미 가입된 회원입니다."),

  // MEMBER
  MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다."),

  // KEYWORD
  KEYWORD_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 키워드를 찾을 수 없습니다."),

  UNSUPPORTED_CATEGORY(HttpStatus.BAD_REQUEST, "지원되지 않는 키워드 카테고리입니다."),

  KEYWORD_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "키워드 저장에 실패했습니다."),

  JSON_SERIALIZATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "JSON 직렬화에 실패했습니다."),

  INVALID_OPENAI_RESPONSE(HttpStatus.BAD_REQUEST, "유효하지 않은 OpenAI 응답입니다."),

  OPENAI_SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "OpenAI 서비스 이용이 불가합니다."),

  KEYWORD_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 존재하는 키워드입니다."),

  INVALID_SPECIFIED_DATE(HttpStatus.BAD_REQUEST, "지정 날짜는 오늘 이후의 날짜여야 합니다."),

  INVALID_DATE_REQUEST(HttpStatus.BAD_REQUEST, "미래 날짜에 대한 키워드 요청은 허용되지 않습니다."),

  // POST
  FILE_SIZE_EXCEED(HttpStatus.PAYLOAD_TOO_LARGE, "업로드 가능한 파일 크기를 초과했습니다."),

  FILE_COUNT_EXCEED(HttpStatus.BAD_REQUEST, "업로드 가능한 파일 수를 초과했습니다."),

  FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다."),

  INVALID_FILE_TYPE(HttpStatus.BAD_REQUEST, "지원하지 않는 파일 형식입니다."),

  INVALID_FILTER(HttpStatus.BAD_REQUEST, "잘못된 필터 값입니다.");

  private final HttpStatus status;
  private final String message;
}
