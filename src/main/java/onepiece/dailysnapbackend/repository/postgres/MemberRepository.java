package onepiece.dailysnapbackend.repository.postgres;

import java.util.Optional;
import onepiece.dailysnapbackend.object.postgres.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
  Boolean existsByUsername(String username);

  Optional<Member> findByUsername(String username);
}