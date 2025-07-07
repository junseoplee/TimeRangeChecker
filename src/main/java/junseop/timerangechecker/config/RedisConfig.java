package junseop.timerangechecker.config;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis設定クラス
 * キャッシュ機能およびRedisテンプレート設定を提供
 */
@Configuration
@EnableRedisRepositories
@EnableCaching
public class RedisConfig {

  @Value("${spring.data.redis.host:localhost}")
  private String redisHost;

  @Value("${spring.data.redis.port:6379}")
  private int redisPort;

  /**
   * Redis接続ファクトリー設定
   */
  @Bean
  public RedisConnectionFactory redisConnectionFactory() {
    return new LettuceConnectionFactory(redisHost, redisPort);
  }

  /**
   * RedisTemplateの設定
   * 文字列キーとJSON値のシリアライゼーション
   */
  @Bean
  public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);
    
    // キーのシリアライゼーション設定
    template.setKeySerializer(new StringRedisSerializer());
    template.setHashKeySerializer(new StringRedisSerializer());
    
    // 値のシリアライゼーション設定（JSON形式）
    template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
    template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
    
    template.afterPropertiesSet();
    return template;
  }

  /**
   * RedisCacheManager設定
   * 統一されたキャッシュ管理と有効期限設定
   */
  @Bean
  public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
    RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
        .entryTtl(Duration.ofHours(1))  // デフォルト1時間の有効期限
        .serializeKeysWith(org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair
            .fromSerializer(new StringRedisSerializer()))
        .serializeValuesWith(org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair
            .fromSerializer(new GenericJackson2JsonRedisSerializer()));

    return RedisCacheManager.builder(connectionFactory)
        .cacheDefaults(config)
        .build();
  }
}
