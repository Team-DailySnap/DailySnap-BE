package onepiece.dailysnapbackend.repository.mongo;

import java.util.Optional;
import onepiece.dailysnapbackend.object.mongo.RefreshToken;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RefreshTokenRepository extends MongoRepository<RefreshToken, String> {

  Optional<RefreshToken> findByToken(String token);

  Optional<RefreshToken> findByMemberId(Long memberId);

  void deleteByToken(String token);

  void deleteByMemberId(Long memberId);
}
