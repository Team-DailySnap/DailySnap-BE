package onepiece.dailysnapbackend.repository.postgres;

import java.util.UUID;
import onepiece.dailysnapbackend.object.postgres.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, UUID> {

}
