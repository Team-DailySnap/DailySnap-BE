package onepiece.dailysnapbackend.repository.postgres;

import java.util.List;
import java.util.UUID;
import onepiece.dailysnapbackend.object.postgres.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, UUID> {

  @Query("""
         select p
         from Post p
           join fetch p.member m
         where p.keyword.keywordId = :keywordId
         order by function('random')
         """)
  List<Post> findRandomOneWithMemberByKeywordId(@Param("keywordId") UUID keywordId, Pageable pageable);

}