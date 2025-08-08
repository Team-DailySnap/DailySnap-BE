package onepiece.dailysnapbackend.object.dto;

import java.time.LocalDate;
import java.util.UUID;
import lombok.Builder;
import onepiece.dailysnapbackend.object.constants.KeywordCategory;
import onepiece.dailysnapbackend.object.postgres.Keyword;
import onepiece.dailysnapbackend.object.postgres.Member;
import onepiece.dailysnapbackend.object.postgres.Post;

@Builder
public record PostResponse(
    UUID postId,
    String nickname,
    String profileImageUrl,
    String koreanKeyword,
    String englishKeyword,
    KeywordCategory keywordCategory,
    LocalDate providedDate,
    String imageUrl,
    String description,
    int likeCount
) {

  public static PostResponse from(Post post, Member member, Keyword keyword) {
    return PostResponse.builder()
        .postId(post.getPostId())
        .nickname(member.getNickname())
        .profileImageUrl(member.getProfileImageUrl())
        .koreanKeyword(keyword.getKoreanKeyword())
        .englishKeyword(keyword.getEnglishKeyword())
        .keywordCategory(keyword.getKeywordCategory())
        .providedDate(keyword.getProvidedDate())
        .imageUrl(post.getImageUrl())
        .description(post.getDescription())
        .likeCount(post.getLikeCount())
        .build();
  }
}