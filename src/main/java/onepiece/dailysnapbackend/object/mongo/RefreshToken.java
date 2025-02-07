package onepiece.dailysnapbackend.object.mongo;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "refresh_tokens")
@Getter
@Builder
public class RefreshToken {

  @Id
  private Long refreshTokenId;

  @NotNull
  private String token;

  @Indexed
  @NotNull
  private Long memberId;
}
