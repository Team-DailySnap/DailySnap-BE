package onepiece.dailysnapbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import onepiece.dailysnapbackend.object.dto.CustomOAuth2User;
import onepiece.dailysnapbackend.object.dto.KeywordFilterRequest;
import onepiece.dailysnapbackend.object.dto.KeywordResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

public interface KeywordControllerDocs {

  @Operation(
      summary = "키워드 목록 조회 (동적 필터링)",
      description = """
          ### 요청 파라미터
          - `koreanKeyword` (String, optional): 검색할 한국어 키워드 (부분 일치)
          - `keywordCategory` (KeywordCategory, optional): 필터링할 키워드 카테고리
          - `providedDate` (LocalDate, optional): 제공 날짜 필터 (형식: yyyy-MM-dd)
          - `used` (Boolean, optional): 사용 여부 필터 (true: 사용된 키워드, false: 미사용 키워드)
          - `pageNumber` (int, optional): 페이지 번호 (1부터 시작, 기본값: 1)
          - `pageSize` (int, optional): 페이지 당 항목 수 (기본값: `PageableConstants.DEFAULT_PAGE_SIZE`)
          - `sortField` (KeywordSortField, optional): 정렬 기준 필드 (기본값: CREATED_DATE)
          - `sortDirection` (Sort.Direction, optional): 정렬 방향 (ASC 또는 DESC, 기본값: DESC)
          
          ### 응답 데이터
          - `content` (List<KeywordResponse>): 조회된 키워드 응답 객체 리스트  
            - `keywordId` (UUID): 키워드 고유 ID  
            - `koreanKeyword` (String): 한국어 키워드  
            - `englishKeyword` (String): 영어 키워드  
            - `keywordCategory` (KeywordCategory): 키워드 카테고리  
            - `providedDate` (LocalDate): 제공 날짜 (yyyy-MM-dd)  
            - `used` (boolean): 사용 여부  
          - `pageable` (Object): 페이지 요청 정보  
            - `pageNumber` (int)  
            - `pageSize` (int)  
            - `offset` (long)  
            - `paged` (boolean)  
            - `unpaged` (boolean)  
          - `totalPages` (int): 전체 페이지 수  
          - `totalElements` (long): 전체 항목 수  
          - `last` (boolean): 마지막 페이지 여부  
          - `first` (boolean): 첫 페이지 여부  
          - `sort` (Object): 정렬 정보  
          - `numberOfElements` (int): 현재 페이지의 항목 수  
          - `empty` (boolean): 결과 비어있는지 여부
          
          ### 사용 방법
          1. 클라이언트에서 아래 예시처럼 쿼리 파라미터를 붙여 GET 요청을 보냅니다.
             ```
             GET /api/keyword?
               koreanKeyword=주제&
               keywordCategory=ADMIN_SET&
               providedDate=2025-08-09&
               used=false&
               pageNumber=1&
               pageSize=20&
               sortField=CREATED_DATE&
               sortDirection=DESC
             ```
          2. 서버는 전달된 필터 조건에 맞춰 키워드를 조회하여 Page 형태로 반환합니다.
          
          ### 유의 사항
          - 모든 파라미터는 선택 사항이며, 미전달 시 기본값으로 처리됩니다.  
          - `pageNumber`는 1부터 시작합니다.  
          - `providedDate` 필터 시 반드시 `yyyy-MM-dd` 형식을 준수해야 합니다.  
          """
  )
  ResponseEntity<Page<KeywordResponse>> filteredKeywords(
      CustomOAuth2User customOAuth2User,
      KeywordFilterRequest request
  );
}
