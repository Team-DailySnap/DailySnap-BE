package onepiece.dailysnapbackend.repository.postgres;

import java.util.Optional;
import java.util.UUID;
import onepiece.dailysnapbackend.object.constants.SocialPlatform;
import onepiece.dailysnapbackend.object.postgres.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, UUID> {
  Boolean existsByUsername(String username);

  Optional<Member> findByUsernameAndSocialPlatform(String username, SocialPlatform socialPlatform);
}
