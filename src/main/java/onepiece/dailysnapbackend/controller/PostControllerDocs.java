package onepiece.dailysnapbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import java.util.UUID;
import onepiece.dailysnapbackend.object.dto.CustomOAuth2User;
import onepiece.dailysnapbackend.object.dto.PostFilteredRequest;
import onepiece.dailysnapbackend.object.dto.PostRequest;
import onepiece.dailysnapbackend.object.dto.PostResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

public interface PostControllerDocs {

  @Operation(
      summary = "게시물 업로드",
      description = """
          ### 요청 파라미터
          - `image` (file, required): 업로드할 이미지 파일 (MultipartFile)
          - `description` (String, optional): 게시물 설명
          
          ### 응답 데이터
          - 없음 (빈 본문)
          
          ### 사용 방법
          1. 클라이언트에서 Authorization 헤더에 `Bearer {accessToken}`을 포함합니다.  
          2. `Content-Type: multipart/form-data` 로 아래와 같이 폼 데이터 요청을 보냅니다:
             ```
             POST /api/auth
             Content-Type: multipart/form-data
             Authorization: Bearer eyJhbGciOiJI...
          
             --boundary
             Content-Disposition: form-data; name="image"; filename="photo.jpg"
             Content-Type: image/jpeg
          
             (파일 바이너리)
             --boundary
             Content-Disposition: form-data; name="description"
          
             오늘의 키워드 사진입니다.
             --boundary--
             ```
          3. 서버가 이미지를 저장하고 200 OK 응답을 반환합니다.
          
          ### 유의 사항
          - `image` 파일은 반드시 전송해야 합니다.  
          - `description`은 최대 길이 제한이 없으나, 필요 시 클라이언트에서 적절히 검증해 주세요.  
          - 파일 업로드 실패 시 4xx/5xx 에러가 발생할 수 있습니다.  
          """
  )
  ResponseEntity<Void> uploadPost(
      CustomOAuth2User customOAuth2User,
      PostRequest request
  );

  @Operation(
      summary = "게시물 상세 조회",
      description = """
          ### 요청 파라미터
          - `post-id` (UUID, required, path): 조회할 게시물의 고유 ID
          
          ### 응답 데이터
          - `postId` (UUID): 게시물 고유 ID  
          - `nickname` (String): 작성자 닉네임  
          - `profileImageUrl` (String): 작성자 프로필 이미지 URL  
          - `koreanKeyword` (String): 게시물에 사용된 한국어 키워드  
          - `englishKeyword` (String): 게시물에 사용된 영어 키워드  
          - `keywordCategory` (KeywordCategory): 키워드 카테고리  
          - `providedDate` (LocalDate): 키워드 제공 날짜 (YYYY-MM-DD)  
          - `imageUrl` (String): 게시물 이미지 URL  
          - `description` (String): 게시물 설명  
          - `likeCount` (int): 좋아요 수
          
          ### 사용 방법
          1. 클라이언트에서 Authorization 헤더에 `Bearer {accessToken}`을 포함하여 GET 요청을 보냅니다:
             ```
             GET /api/auth/{post-id}
             ```
          2. 서버가 해당 `post-id`에 매핑된 게시물을 조회하여 `PostResponse` 형태로 반환합니다.
          
          ### 유의 사항
          - `post-id`는 UUID 형식이어야 합니다.  
          - 존재하지 않는 `post-id`로 요청 시 400 Bad Request 응답이 반환됩니다.  
          """
  )
  ResponseEntity<PostResponse> getPost(
      CustomOAuth2User userDetails,
      UUID postId
  );

  @Operation(
      summary = "게시물 목록 조회 (동적 필터링)",
      description = """
          ### 요청 파라미터
          - `keywordId` (UUID, optional, query): 키워드 고유 ID로 필터링
          - `koreanKeyword` (String, optional, query): 한국어 키워드 텍스트로 필터링 (부분 일치)
          - `englishKeyword` (String, optional, query): 영어 키워드 텍스트로 필터링 (부분 일치)
          - `description` (String, optional, query): 게시물 설명 텍스트로 필터링 (부분 일치)
          - `pageNumber` (int, optional, query): 페이지 번호 (1 이상, 기본값: 1)
          - `pageSize` (int, optional, query): 페이지 크기 (기본값: `PageableConstants.DEFAULT_PAGE_SIZE`)
          - `sortField` (PostSortField, optional, query): 정렬 기준 필드 (CREATED_DATE, LIKE_COUNT; 기본값: CREATED_DATE)
          - `sortDirection` (Sort.Direction, optional, query): 정렬 방향 (ASC 또는 DESC; 기본값: DESC)
          
          ### 응답 데이터
          - `content` (List<PostResponse>): 조회된 게시물 리스트  
            - `postId` (UUID): 게시물 고유 ID  
            - `nickname` (String): 작성자 닉네임  
            - `profileImageUrl` (String): 작성자 프로필 이미지 URL  
            - `koreanKeyword` (String): 게시물에 사용된 한국어 키워드  
            - `englishKeyword` (String): 게시물에 사용된 영어 키워드  
            - `keywordCategory` (KeywordCategory): 키워드 카테고리  
            - `providedDate` (LocalDate): 키워드 제공 날짜 (yyyy-MM-dd)  
            - `imageUrl` (String): 게시물 이미지 URL  
            - `description` (String): 게시물 설명  
            - `likeCount` (int): 좋아요 수  
          - `pageable` (Object): 페이지 요청 정보  
          - `totalPages` (int): 전체 페이지 수  
          - `totalElements` (long): 전체 게시물 수  
          - `first` (boolean): 첫 페이지 여부  
          - `last` (boolean): 마지막 페이지 여부  
          - `numberOfElements` (int): 현재 페이지 게시물 수  
          - `empty` (boolean): 결과 비어있는지 여부
          
          ### 사용 방법
          1. 클라이언트에서 Authorization 헤더에 `Bearer {accessToken}`을 포함하여 GET 요청을 보냅니다.  
          2. 쿼리 파라미터로 필터·페이징·정렬 조건을 전달합니다. 예:
             ```
             GET /api/post?
               keywordId=3fa85f64-5717-4562-b3fc-2c963f66afa6&
               koreanKeyword=사진&
               description=여행&
               pageNumber=1&
               pageSize=10&
               sortField=LIKE_COUNT&
               sortDirection=DESC
             ```
          
          ### 유의 사항
          - 모든 파라미터는 선택 사항이며, 미전달 시 기본값이 적용됩니다.  
          - `pageNumber`는 1부터 시작합니다.  
          - `sortField`와 `sortDirection`은 `PostSortField` 및 `Sort.Direction` enum 값만 허용됩니다.  
          - UUID 형식 필터(`keywordId`) 시 올바른 UUID 형식을 준수해야 합니다.
          """
  )
  ResponseEntity<Page<PostResponse>> filteredPost(
      PostFilteredRequest request
  );

  @Operation(
      summary = "홈 화면 7일치 키워드별 랜덤 포스트 조회",
      description = """
          ### 요청 파라미터
          없음 — 이 API는 현재 날짜로부터 7일치의 키워드를 자동으로 조회하고, 각 키워드별로 랜덤한 포스트를 1개씩 반환합니다.
          
          ### 응답 데이터
          - `postId` (UUID): 포스트 고유 식별자
          - `nickname` (String): 작성자 닉네임
          - `profileImageUrl` (String): 작성자 프로필 이미지 URL
          - `koreanKeyword` (String): 키워드(한글)
          - `englishKeyword` (String): 키워드(영문)
          - `keywordCategory` (KeywordCategory): 키워드 카테고리
          - `providedDate` (LocalDate): 키워드 제공일
          - `imageUrl` (String): 포스트 이미지 URL
          - `description` (String): 포스트 설명
          - `likeCount` (int): 포스트 좋아요 수
          
          ### 사용 방법
          1. 클라이언트는 `GET /api/post/home` 요청을 보냅니다.
          2. 서버는 최근 7일 동안 사용된 키워드를 조회합니다.
          3. 각 키워드에 대해 랜덤하게 1개의 포스트를 선택하여 반환합니다.
          4. 응답은 최대 7개의 포스트 객체로 구성됩니다. (각 키워드별 1개)
          
          ### 유의 사항
          - 7일 이내에 사용된 키워드가 없거나, 특정 키워드에 해당하는 포스트가 없는 경우 해당 키워드는 응답에서 제외됩니다.
          - 반환되는 포스트는 매 요청 시 랜덤하게 선택되므로, 같은 키워드라도 매번 다른 포스트가 반환될 수 있습니다.
          - 정렬 순서는 키워드의 제공일(`providedDate`) 기준으로 최신순입니다.
          """
  )
  ResponseEntity<List<PostResponse>> get7DaysRandomPost();
}
