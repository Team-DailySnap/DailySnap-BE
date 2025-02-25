package onepiece.dailysnapbackend.repository.postgres;

import java.util.UUID;
import onepiece.dailysnapbackend.object.postgres.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KeywordRepository extends JpaRepository<Keyword, UUID> {
}
