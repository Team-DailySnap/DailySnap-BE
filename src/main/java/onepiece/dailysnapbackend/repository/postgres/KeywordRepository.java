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
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface KeywordRepository extends JpaRepository<Keyword, UUID> {

  Optional<Keyword> findFirstByCategoryAndSpecifiedDate(KeywordCategory category, LocalDate specifiedDate);

  Optional<Keyword> findFirstByCategoryAndIsUsedFalse(KeywordCategory category);

  Optional<Keyword> findFirstByProvidedDate(LocalDate providedDate);

  long countByCategoryAndIsUsedFalse(@Param("category") String category);

  void deleteKeywordByKeyword(String keyword);

  boolean existsByKeyword(String keyword);

  @Query(value = """
    SELECT k.* FROM keyword k 
    WHERE (nvl(:keyword, '') = '' OR k.keyword ILIKE CONCAT('%', TRIM(:keyword), '%')) 
      AND (nvl(:category, '') = '' OR k.category = CAST(:category AS text)) 
      AND (nvl(:providedDate, '') = '' OR k.provided_date IS NULL OR k.provided_date = CAST(:providedDate AS DATE))
      AND (nvl(:isUsed, '') = '' OR k.is_used = :isUsed)
    ORDER BY k.created_date DESC
    """, nativeQuery = true)
  Page<Keyword> filteredKeyword(
      @Param("keyword") String keyword,
      @Param("category") String category,
      @Param("providedDate") LocalDate providedDate,
      @Param("isUsed") Boolean isUsed,
      Pageable pageable);

}