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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.extern.slf4j.Slf4j;

/**
 * Redis配置类
 */
@Configuration
@EnableCaching
@Slf4j
public class RedisConfig {

    /**
     * 配置支持Java 8日期/时间类型的ObjectMapper
     * @return 配置好的ObjectMapper
     */
    @Bean
    public ObjectMapper redisObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper;
    }
    
    /**
     * 配置支持Java 8日期/时间类型的GenericJackson2JsonRedisSerializer
     * @return 序列化器
     */
    @Bean
    public GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer(ObjectMapper redisObjectMapper) {
        return new GenericJackson2JsonRedisSerializer(redisObjectMapper);
    }

    /**
     * 配置自定义RedisTemplate
     * @param connectionFactory Redis连接工厂
     * @return RedisTemplate实例
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory, 
                                                     GenericJackson2JsonRedisSerializer jsonRedisSerializer) {
        try {
            RedisTemplate<String, Object> template = new RedisTemplate<>();
            template.setConnectionFactory(connectionFactory);
            
            // 使用StringRedisSerializer来序列化和反序列化redis的key值
            template.setKeySerializer(new StringRedisSerializer());
            // 使用支持Java 8日期/时间类型的GenericJackson2JsonRedisSerializer
            template.setValueSerializer(jsonRedisSerializer);
            
            // Hash的key也采用StringRedisSerializer的序列化方式
            template.setHashKeySerializer(new StringRedisSerializer());
            // Hash的value采用支持Java 8日期/时间类型的GenericJackson2JsonRedisSerializer
            template.setHashValueSerializer(jsonRedisSerializer);
            
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
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory, 
                                    GenericJackson2JsonRedisSerializer jsonRedisSerializer) {
        try {
            // 生成一个默认配置，通过config对象即可对缓存进行自定义配置
            RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                    // 设置缓存的默认过期时间，也是使用Duration设置
                    .entryTtl(Duration.ofMinutes(30))
                    // 设置key为string序列化
                    .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                    // 设置value为支持Java 8日期/时间类型的json序列化
                    .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jsonRedisSerializer))
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