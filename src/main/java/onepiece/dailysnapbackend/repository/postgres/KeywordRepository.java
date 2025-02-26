package onepiece.dailysnapbackend.repository.postgres;

import java.time.LocalDate;
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

  @Query(value = """
            SELECT k.* FROM keyword k 
            WHERE k.category = 'ADMIN_SET' 
              AND k.specified_date = :providedDate 
            LIMIT 1
            """, nativeQuery = true)
  Keyword findAdminSetKeyword(@Param("providedDate") LocalDate providedDate);

  @Query(value = """
            SELECT k.* FROM keyword k 
            WHERE k.category = CAST(:category AS text) 
              AND k.is_used = false 
            LIMIT 1
            """, nativeQuery = true)
  Keyword findUnusedKeyword(@Param("category") KeywordCategory category);

  long countByCategoryAndIsUsedFalse(@Param("category") KeywordCategory category);

  @Query(value = """
    SELECT k.* FROM keyword k 
    WHERE (:keyword IS NULL OR k.keyword ILIKE CONCAT('%', TRIM(:keyword), '%')) 
      AND (:category IS NULL OR k.category = CAST(:category AS text)) 
      AND (:providedDate IS NULL OR k.provided_date IS NULL OR k.provided_date = CAST(:providedDate AS DATE)) 
    ORDER BY k.created_date DESC
    """, nativeQuery = true)
  Page<Keyword> filteredKeyword(
      @Param("keyword") String keyword,
      @Param("category") KeywordCategory category,
      @Param("providedDate") LocalDate providedDate,
      Pageable pageable);
}

// grok3
/*
1. 네이티브 쿼리
2. 네이티브 쿼리와 JPQL의 차이점
3. 네이티브쿼리 count
4. 네이티브쿼리를 사용할 때 pageable
5. 48-64줄 수정
 */