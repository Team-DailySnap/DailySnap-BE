package onepiece.dailysnapbackend.repository;

import onepiece.dailysnapbackend.object.postgres.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
  Member findByUsername(String username);
}
