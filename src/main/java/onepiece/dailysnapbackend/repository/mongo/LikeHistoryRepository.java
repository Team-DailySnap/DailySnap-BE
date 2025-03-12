package onepiece.dailysnapbackend.repository.mongo;

import java.util.UUID;
import onepiece.dailysnapbackend.object.mongo.LikeHistory;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LikeHistoryRepository extends MongoRepository<LikeHistory, UUID> {

  Boolean existsByPostIdAndMemberId(UUID postId, UUID memberId);

}
