package com.csu.unicorp.service.impl.community;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import com.csu.unicorp.config.TestDatabaseConfig;
import com.csu.unicorp.config.TestRedisConfig;
import com.csu.unicorp.service.CacheService;

/**
 * 缓存测试基类
 */
@SpringBootTest
@ActiveProfiles("test")
@Import({TestDatabaseConfig.class, TestRedisConfig.class})
public abstract class BaseCacheServiceTest {

    @Autowired
    protected CacheService cacheService;
    
    @Autowired
    protected RedisTemplate<String, Object> redisTemplate;
    
    /**
     * 每个测试前清空Redis缓存
     */
    @BeforeEach
    public void clearCache() {
        // 清空当前数据库中的所有key
        redisTemplate.getConnectionFactory().getConnection().flushDb();
    }
} 