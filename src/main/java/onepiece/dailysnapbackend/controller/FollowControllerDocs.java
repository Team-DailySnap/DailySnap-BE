package onepiece.dailysnapbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import java.util.UUID;
import onepiece.dailysnapbackend.object.dto.CustomOAuth2User;
import onepiece.dailysnapbackend.object.dto.FollowRequest;
import onepiece.dailysnapbackend.object.dto.MemberResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

public interface FollowControllerDocs {

  @Operation(
      summary = "사용자 팔로우",
      description = """
          이 API는 인증이 필요합니다.
          
          ### 요청 파라미터
          - **`followeeId`** (UUID): 팔로우할 사용자의 ID (필수)
          
          ### 반환값
          - HTTP 200 OK: 팔로우 성공
          """
  )
  ResponseEntity<Void> followMember(CustomOAuth2User userDetails, UUID followeeId);

  @Operation(
      summary = "사용자 언팔로우",
      description = """
          이 API는 인증이 필요합니다.
          
          ### 요청 파라미터
          - **`followeeId`** (UUID): 언팔로우할 사용자의 ID (필수)
          
          ### 반환값
          - HTTP 200 OK: 언팔로우 성공
          """
  )
  ResponseEntity<Void> unfollowMember(CustomOAuth2User userDetails, UUID followeeId);

  @Operation(
      summary = "팔로워 목록 조회",
      description = """
          이 API는 인증이 필요합니다.
          
          ### 요청 파라미터
          - **`pageNumber`** (Integer): 페이지 번호 (기본값: 0)
          - **`pageSize`** (Integer): 페이지 크기 (기본값: 30, 최대 100)
          - **`sortField`** (String): 정렬 기준 필드 (기본값: "createdDate")
          - **`sortDirection`** (String): 정렬 방향 (기본값: "DESC", 허용값: "ASC", "DESC")
          
          ### 반환값
          - **`Page<MemberResponse>`**: 팔로워 목록 (페이지네이션 적용)
          """
  )
  Page<MemberResponse> getFollowers(CustomOAuth2User userDetails, FollowRequest request);

  @Operation(
      summary = "팔로잉 목록 조회",
      description = """
          이 API는 인증이 필요합니다.
          
          ### 요청 파라미터
          - **`pageNumber`** (Integer): 페이지 번호 (기본값: 0)
          - **`pageSize`** (Integer): 페이지 크기 (기본값: 30, 최대 100)
          - **`sortField`** (String): 정렬 기준 필드 (기본값: "createdDate")
          - **`sortDirection`** (String): 정렬 방향 (기본값: "DESC", 허용값: "ASC", "DESC")
          
          ### 반환값
          - **`Page<MemberResponse>`**: 팔로잉 목록 (페이지네이션 적용)
          """
  )
  Page<MemberResponse> getFollowings(CustomOAuth2User userDetails, FollowRequest request);
}
