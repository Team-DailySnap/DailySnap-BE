package onepiece.dailysnapbackend.repository.postgres;

import java.util.UUID;
import onepiece.dailysnapbackend.object.postgres.Photo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhotoRepository extends JpaRepository<Photo, UUID> {

}
