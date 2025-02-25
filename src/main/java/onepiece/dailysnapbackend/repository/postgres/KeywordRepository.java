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

  // ADMIN_SET 카테고리에서 특정 날짜의 키워드를 검색
  @Query(value = """
            SELECT k.*
            FROM keyword k
            WHERE k.category = 'ADMIN_SET'
              AND k.specified_date = :providedDate
            """,
      nativeQuery = true)
  Page<Keyword> findAdminSetKeyword(@Param("providedDate") LocalDate providedDate, Pageable pageable);

  // 특정 카테고리에서 isUsed=false인 키워드 조회
  @Query(value = """
            SELECT k.*
            FROM keyword k
            WHERE k.category = :category
              AND k.is_used = false
            """,
      nativeQuery = true)
  Page<Keyword> findUnusedKeywords(@Param("category") KeywordCategory category, Pageable pageable);

  // 특정 카테고리에서 사용되지 않은 키워드 개수 확인
  @Query(value = """
            SELECT COUNT(*)
            FROM keyword k
            WHERE k.category = :category
              AND k.is_used = false
            """,
      nativeQuery = true)
  long countUnusedKeywords(@Param("category") KeywordCategory category);

  // 기존 필터링
  @Query(value = """
            SELECT k.*
            FROM keyword k
            WHERE (:keyword IS NULL OR lower(k.keyword) LIKE lower(concat('%', trim(:keyword), '%')))
              AND (:category IS NULL OR k.category = :category)
              AND (
                   :providedDate IS NULL
                   OR (:providedDate = 'NULL_DATE' AND k.provided_date IS NULL)
                   OR (:providedDate != 'NULL_DATE' AND k.provided_date = :providedDate)
                 )
            """,
      nativeQuery = true)
  Page<Keyword> filteredKeyword(@Param("keyword") String keyword,
      @Param("category") KeywordCategory category,
      @Param("providedDate") LocalDate providedDate,
      Pageable pageable);
}
