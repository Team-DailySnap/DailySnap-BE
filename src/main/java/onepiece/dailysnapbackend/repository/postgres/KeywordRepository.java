package onepiece.dailysnapbackend.repository.postgres;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import onepiece.dailysnapbackend.object.constants.KeywordCategory;
import onepiece.dailysnapbackend.object.postgres.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KeywordRepository extends JpaRepository<Keyword, UUID> {

  // 사용되지 않은 키워드 중 가장 먼저 등록된 키워드 조회
  Optional<Keyword> findTopByCategoryAndIsUsedFalse(KeywordCategory category);

  // 특정 날짜에 제공될 '관리자 지정(ADMIN_SET)' 키워드 조회
  Optional<Keyword> findByCategoryAndSpecifiedDate(KeywordCategory category, LocalDate date);

  // 특정 카테고리의 키워드 개수 조회
  long countByCategory(KeywordCategory category);

  // 가장 최근 제공된 키워드 조회
  Optional<Keyword> findTopByOrderByProvidedDateDesc();

  // 특정 카테고리에 속한 모든 키워드 조회
  List<Keyword> findByCategory(KeywordCategory category);

  // 특정 날짜에 제공된 모든 키워드 조회
  List<Keyword> findByProvidedDate(LocalDate date);

  // 특정 키워드가 이미 존재하는지 확인
  boolean existsByKeyword(String keyword);
}
