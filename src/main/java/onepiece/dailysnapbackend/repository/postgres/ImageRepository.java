package onepiece.dailysnapbackend.repository.postgres;

import java.util.UUID;
import onepiece.dailysnapbackend.object.postgres.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, UUID> {

}
