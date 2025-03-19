package onepiece.dailysnapbackend.repository.postgres;

import java.time.LocalDate;
import java.util.List;
import onepiece.dailysnapbackend.object.postgres.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BestPostRepository extends JpaRepository<Post, Long> {

  // 상위 30개 인기 게시물 조회
  List<Post> findTop30ByOrderByLikeCountDesc();

  // 일간 인기 게시물 조회
  List<Post> findByDailyBestCreatedDateGreaterThanEqualOrderByLikeCountDesc(LocalDate startDate);

  // 주간 인기 게시물 조회
  List<Post> findByWeeklyBestWeekStartDateGreaterThanEqualOrderByLikeCountDesc(LocalDate startDate);
}
