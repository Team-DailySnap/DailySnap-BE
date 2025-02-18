package onepiece.dailysnapbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableJpaAuditing
@EnableMongoAuditing
public class DailySnapBackendApplication {

  public static void main(String[] args) {
    SpringApplication.run(DailySnapBackendApplication.class, args);
  }
}
