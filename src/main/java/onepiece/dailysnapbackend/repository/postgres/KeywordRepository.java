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

  Optional<Keyword> findByCategoryAndSpecifiedDate(KeywordCategory category, LocalDate specifiedDate);

  Optional<Keyword> findFirstByCategoryAndIsUsedFalse(KeywordCategory category);

  Optional<Keyword> findByProvidedDate(LocalDate providedDate);

  long countByCategoryAndIsUsedFalse(@Param("category") String category);

  void deleteKeywordByKeyword(String keyword);

  boolean existsByKeyword(String keyword);


  @Query(value = """
    SELECT k.* FROM keyword k 
    WHERE (:keyword = '' OR k.keyword ILIKE CONCAT('%', TRIM(:keyword), '%')) 
      AND (:category = '' OR k.category = :category) 
      AND (:providedDate = '' OR k.provided_date = CAST(:providedDate AS DATE))
      AND (:isUsed = '' OR k.is_used = CAST(:isUsed AS BOOLEAN))
    """, nativeQuery = true)
  Page<Keyword> filteredKeyword(
      @Param("keyword") String keyword,
      @Param("category") String category,
      @Param("providedDate") String providedDate,
      @Param("isUsed") String isUsed,
      Pageable pageable);

}