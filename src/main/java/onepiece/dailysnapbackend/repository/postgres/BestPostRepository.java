package onepiece.dailysnapbackend.repository.postgres;

import java.time.LocalDateTime;
import java.util.List;
import onepiece.dailysnapbackend.object.postgres.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BestPostRepository extends JpaRepository<Post, Long> {

  @Query(value = "SELECT p FROM Post p WHERE p.createdDate BETWEEN :startDate AND :endDate " +
                 "ORDER BY p.likeCount DESC LIMIT 30")
  List<Post> findTop30ByCreatedAtBetweenOrderByLikeCountDescDaily(LocalDateTime startDate, LocalDateTime endDate);

  @Query(value = "SELECT p FROM Post p WHERE p.createdDate BETWEEN :startDate AND :endDate " +
                 "ORDER BY p.likeCount DESC LIMIT 30")
  List<Post> findTop30ByCreatedAtBetweenOrderByLikeCountDescWeekly(LocalDateTime startDate, LocalDateTime endDate);

  @Query(value = "SELECT p FROM Post p WHERE p.createdDate BETWEEN :startDate AND :endDate " +
                 "ORDER BY p.likeCount DESC LIMIT 30")
  List<Post> findTop30ByCreatedAtBetweenOrderByLikeCountDescMonthly(LocalDateTime startDate, LocalDateTime endDate);
}
