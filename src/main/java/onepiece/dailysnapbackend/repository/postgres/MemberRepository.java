package onepiece.dailysnapbackend.repository.postgres;

import java.util.Optional;
import java.util.UUID;
import onepiece.dailysnapbackend.object.postgres.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, UUID> {

  Optional<Member> findByUsername(String username);

  Boolean existsByUsername(String username);
}
