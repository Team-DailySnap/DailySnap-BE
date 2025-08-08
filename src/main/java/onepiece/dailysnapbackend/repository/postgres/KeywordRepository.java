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

public interface KeywordRepository extends JpaRepository<Keyword, UUID> {

  Optional<Keyword> findByProvidedDate(LocalDate providedDate);

  boolean existsByKoreanKeyword(String koreanKeyword);

  // 특정 카테고리에서 가장 마지막에 저장된 providedDate 를 조회
  @Query("SELECT MAX(k.providedDate) FROM Keyword k WHERE k.keywordCategory = :category")
  LocalDate findMaxProvidedDateByCategory(@Param("category") KeywordCategory category);

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