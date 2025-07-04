package com.csu.unicorp.config;

import java.time.Duration;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;

import lombok.extern.slf4j.Slf4j;

/**
 * Redis配置类
 */
@Configuration
@EnableCaching
@Slf4j
public class RedisConfig {

    /**
     * 配置自定义RedisTemplate
     * @param connectionFactory Redis连接工厂
     * @return RedisTemplate实例
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        try {
            RedisTemplate<String, Object> template = new RedisTemplate<>();
            template.setConnectionFactory(connectionFactory);
            
            // 使用StringRedisSerializer来序列化和反序列化redis的key值
            template.setKeySerializer(new StringRedisSerializer());
            // 使用GenericJackson2JsonRedisSerializer来序列化和反序列化redis的value值
            template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
            
            // Hash的key也采用StringRedisSerializer的序列化方式
            template.setHashKeySerializer(new StringRedisSerializer());
            // Hash的value采用GenericJackson2JsonRedisSerializer的序列化方式
            template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
            
            template.afterPropertiesSet();
            log.info("Redis连接成功，使用标准RedisTemplate");
            return template;
        } catch (Exception e) {
            log.warn("Redis连接失败: {}，将使用本地缓存降级", e.getMessage());
            throw e;
        }
    }
    
    /**
     * 配置缓存管理器
     * @param connectionFactory Redis连接工厂
     * @return 缓存管理器
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        try {
            // 生成一个默认配置，通过config对象即可对缓存进行自定义配置
            RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                    // 设置缓存的默认过期时间，也是使用Duration设置
                    .entryTtl(Duration.ofMinutes(30))
                    // 设置key为string序列化
                    .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                    // 设置value为json序列化
                    .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
                    // 不缓存null值
                    .disableCachingNullValues();
                    
            // 使用自定义的缓存配置初始化一个cacheManager
            return RedisCacheManager.builder(connectionFactory)
                    .cacheDefaults(config)
                    .build();
        } catch (Exception e) {
            log.warn("Redis缓存管理器初始化失败: {}，将使用本地缓存降级", e.getMessage());
            throw e;
        }
    }
} 