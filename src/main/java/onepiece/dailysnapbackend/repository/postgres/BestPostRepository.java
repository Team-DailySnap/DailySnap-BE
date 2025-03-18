package onepiece.dailysnapbackend.repository.postgres;

import java.time.LocalDate;
import java.util.List;
import onepiece.dailysnapbackend.object.postgres.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BestPostRepository extends JpaRepository<Post, Long> {

  List<Post> findTop30ByOrderByLikeCountDesc();

  @Query("SELECT p FROM Post p WHERE p.dailyBest.createdDate >= :startDate ORDER BY p.likeCount DESC")
  List<Post> findByDailyBest(LocalDate startDate, Pageable pageable);

  @Query("SELECT p FROM Post p WHERE p.weeklyBest.weekStartDate >= :startDate ORDER BY p.likeCount DESC")
  List<Post> findByWeeklyBest(LocalDate startDate, Pageable pageable);
}
