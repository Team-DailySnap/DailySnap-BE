package onepiece.dailysnapbackend.repository.postgres;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import onepiece.dailysnapbackend.object.constants.KeywordCategory;
import onepiece.dailysnapbackend.object.postgres.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface KeywordRepository extends JpaRepository<Keyword, UUID> {

  Optional<Keyword> findByProvidedDate(LocalDate providedDate);

  boolean existsByKoreanKeyword(String koreanKeyword);

  // 특정 카테고리에서 가장 마지막에 저장된 providedDate 를 조회
  @Query("SELECT MAX(k.providedDate) FROM Keyword k WHERE k.keywordCategory = :category")
  LocalDate findMaxProvidedDateByCategory(@Param("category") KeywordCategory category);

  List<Keyword> findTop7ByUsedIsTrueOrderByProvidedDateDesc();
}