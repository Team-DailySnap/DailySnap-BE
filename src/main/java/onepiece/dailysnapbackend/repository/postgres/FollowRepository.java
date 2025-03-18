package onepiece.dailysnapbackend.repository.postgres;

import java.util.Optional;
import java.util.UUID;
import onepiece.dailysnapbackend.object.postgres.Follow;
import onepiece.dailysnapbackend.object.postgres.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, UUID> {

  boolean existsByFollowerAndFollowee(Member follower, Member followee);

  Optional<Follow> findByFollowerAndFollowee(Member follower, Member followee);
}
