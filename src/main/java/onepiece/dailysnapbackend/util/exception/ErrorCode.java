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

  COOKIES_NOT_FOUND(HttpStatus.BAD_REQUEST, "쿠키가 요청에 포함되지 않았습니다."),

  REFRESH_TOKEN_EMPTY(HttpStatus.BAD_REQUEST, "리프레시 토큰이 비어있습니다."),

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

  POST_NOT_FOUND(HttpStatus.BAD_REQUEST, "게시물을 찾을 수 없습니다."),

  // S3

  INVALID_FILE_EXTENSION(HttpStatus.BAD_REQUEST, "유효하지 않은 파일 확장자입니다."),

  FILE_UPLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드 중 오류가 발생했습니다."),

  S3_UPLOAD_AMAZON_SERVICE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S3 서비스 에러로 인해 파일 업로드에 실패했습니다."),

  S3_UPLOAD_AMAZON_CLIENT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S3 클라이언트 에러로 인해 파일 업로드에 실패했습니다."),

  S3_UPLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S3 파일 업로드 중 오류 발생"),

  S3_DELETE_AMAZON_SERVICE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S3 서비스 에러로 인해 파일 삭제에 실패했습니다."),

  S3_DELETE_AMAZON_CLIENT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S3 클라이언트 에러로 인해 파일 삭제에 실패했습니다."),

  S3_DELETE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S3 파일 삭제 중 오류 발생"),

  INVALID_FILE_REQUEST(HttpStatus.BAD_REQUEST, "유효하지 않은 파일 요청입니다."),

  INVALID_FILE_PATH(HttpStatus.BAD_REQUEST, "유효하지 않은 파일 URL 요청입니다."),

  // LIKE

  ALREADY_LIKED(HttpStatus.BAD_REQUEST, "이미 좋아요를 눌렀습니다."),

  LIKE_HISTORY_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "좋아요 내역 저장에 실패했습니다."),

  // FOLLOW

  ALREADY_FOLLOWED(HttpStatus.BAD_REQUEST, "이미 팔로우한 사용자입니다."),

  FOLLOW_RELATIONSHIP_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 팔로우 관계를 찾을 수 없습니다."),

  // PAGEABLE

  INVALID_SORT_FIELD(HttpStatus.BAD_REQUEST, "필터링 조회 시 정렬 필드 요청이 잘못되었습니다."),

  ;

  private final HttpStatus status;
  private final String message;
}
