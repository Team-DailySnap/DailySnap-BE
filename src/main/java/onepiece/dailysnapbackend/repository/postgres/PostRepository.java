package onepiece.dailysnapbackend.repository.postgres;

import java.util.UUID;
import onepiece.dailysnapbackend.object.postgres.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, UUID> {

  @Query(value = """
            SELECT p.*
            FROM post p
            JOIN member m ON p.member_id = m.member_id
            WHERE :nickname IS NULL OR trim(:nickname) = ''
                  OR lower(m.nickname) LIKE lower(concat('%', trim(:nickname), '%'))
            """,
      countQuery = """
            SELECT count(*)
            FROM post p
            JOIN member m ON p.member_id = m.member_id
            WHERE :nickname IS NULL OR trim(:nickname) = ''
                  OR lower(m.nickname) LIKE lower(concat('%', trim(:nickname), '%'))
            """,
      nativeQuery = true)
  Page<Post> filterPosts(@Param("nickname") String nickname, Pageable pageable);
}