package onepiece.dailysnapbackend.repository.postgres;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import onepiece.dailysnapbackend.object.postgres.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KeywordRepository extends JpaRepository<Keyword, UUID> {

  Optional<Keyword> findKeywordByDate(LocalDate date);

  Optional<Keyword> findKeywordByKeywordId(UUID keywordId);
}
