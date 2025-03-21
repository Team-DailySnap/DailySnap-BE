package onepiece.dailysnapbackend.object.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BestPostFilter {
  DAILY("daily"),
  WEEKLY("weekly"),
  MONTHLY("monthly");

  private final String description;
}
