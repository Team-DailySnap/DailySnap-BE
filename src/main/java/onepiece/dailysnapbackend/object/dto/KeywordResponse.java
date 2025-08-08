package onepiece.dailysnapbackend.object.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Builder;
import onepiece.dailysnapbackend.object.constants.KeywordCategory;
import onepiece.dailysnapbackend.object.postgres.Keyword;

@Builder
public record KeywordResponse(
    UUID keywordId,
    String koreanKeyword,
    String englishKeyword,
    KeywordCategory keywordCategory,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    LocalDate providedDate,
    boolean used
) {

  public static KeywordResponse of(Keyword keyword) {
    return new KeywordResponse(
        keyword.getKeywordId(),
        keyword.getKoreanKeyword(),
        keyword.getEnglishKeyword(),
        keyword.getKeywordCategory(),
        keyword.getProvidedDate(),
        keyword.isUsed()
    );
  }
}