package onepiece.dailysnapbackend.repository.postgres;

import java.util.List;
import java.util.UUID;
import onepiece.dailysnapbackend.object.postgres.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, UUID> {

  List<Image> findByPost(Post post);
}
