package onepiece.dailysnapbackend.repository.mongo;

import java.util.UUID;
import onepiece.dailysnapbackend.object.mongo.LikeHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeHistoryRepository extends JpaRepository<LikeHistory, UUID> {

  Boolean existsByPostIdAndMemberId(UUID postId, UUID memberId);

}
