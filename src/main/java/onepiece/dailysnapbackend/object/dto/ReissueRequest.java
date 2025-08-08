package onepiece.dailysnapbackend.object.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ReissueRequest {

  @NotBlank
  private String refreshToken;
}
