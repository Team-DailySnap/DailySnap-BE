package onepiece.dailysnapbackend.repository.postgres;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import onepiece.dailysnapbackend.object.constants.KeywordCategory;
import onepiece.dailysnapbackend.object.postgres.Keyword;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface KeywordRepository extends JpaRepository<Keyword, UUID> {

  Optional<Keyword> findByKeywordCategoryAndProvidedDate(KeywordCategory keywordCategory, LocalDate providedDate);

  Optional<Keyword> findFirstByKeywordCategoryAndUsedFalse(KeywordCategory keywordCategory);

  Optional<Keyword> findByProvidedDate(LocalDate providedDate);

  Optional<Keyword> findKeywordByKeywordId(UUID keywordId);

  boolean existsByKoreanKeyword(String koreanKeyword);

  @Query(value = """
    SELECT k.* FROM keyword k 
    WHERE (:keyword = '' OR k.keyword ILIKE CONCAT('%', TRIM(:keyword), '%')) 
      AND (:category = '' OR k.category = :category) 
      AND (:providedDate = '' OR k.provided_date = :proviedDate)
      AND (:isUsed IS NULL OR k.is_used = CAST(:isUsed AS BOOLEAN))
    """, nativeQuery = true)
  Page<Keyword> filteredKeyword(
      String keyword,
      String category,
      String providedDate,
      Boolean isUsed,
      Pageable pageable);

}