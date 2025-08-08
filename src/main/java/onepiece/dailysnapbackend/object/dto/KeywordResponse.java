package onepiece.dailysnapbackend.object.dto;

import java.time.LocalDate;
import java.util.UUID;
import lombok.Builder;
import onepiece.dailysnapbackend.object.constants.KeywordCategory;

@Builder
public record KeywordResponse(
    UUID keywordId,
    String koreanKeyword,
    String englishKeyword,
    KeywordCategory keywordCategory,
    LocalDate providedDate,
    boolean used
) {
}