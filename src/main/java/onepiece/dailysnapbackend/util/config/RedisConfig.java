package onepiece.dailysnapbackend.util.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import onepiece.dailysnapbackend.object.postgres.Post;

import java.util.List;

@Configuration
public class RedisConfig {

  @Bean
  public RedisTemplate<String, List<Post>> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
    RedisTemplate<String, List<Post>> template = new RedisTemplate<>();
    template.setConnectionFactory(redisConnectionFactory);

    template.setKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

    return template;
  }
}
