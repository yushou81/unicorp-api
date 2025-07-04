package com.csu.unicorp.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.csu.unicorp.service.CacheService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Redis缓存服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RedisCacheServiceImpl implements CacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    
    // 内存缓存，用于Redis不可用时的降级处理
    private final Map<String, Object> localCache = new HashMap<>();
    private final Map<String, Long> localCacheExpiry = new HashMap<>();
    
    /**
     * 执行Redis操作，如果Redis不可用则进行降级处理
     * @param redisOperation Redis操作
     * @param fallback 降级处理
     * @return 操作结果
     */
    private <T> T executeWithFallback(RedisOperation<T> redisOperation, FallbackOperation<T> fallback) {
        try {
            return redisOperation.execute();
        } catch (RedisConnectionFailureException e) {
            log.warn("Redis连接失败，使用本地缓存降级: {}", e.getMessage());
            return fallback.execute();
        } catch (Exception e) {
            log.error("Redis操作异常: {}", e.getMessage());
            return fallback.execute();
        }
    }

    @Override
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        executeWithFallback(() -> {
            redisTemplate.opsForValue().set(key, value, timeout, unit);
            return null;
        }, () -> {
            localCache.put(key, value);
            // 计算过期时间（毫秒）
            long expireTime = System.currentTimeMillis() + unit.toMillis(timeout);
            localCacheExpiry.put(key, expireTime);
            return null;
        });
    }

    @Override
    public void set(String key, Object value) {
        executeWithFallback(() -> {
            redisTemplate.opsForValue().set(key, value);
            return null;
        }, () -> {
            localCache.put(key, value);
            return null;
        });
    }

    @Override
    public <T> T get(String key, Class<T> clazz) {
        return executeWithFallback(() -> {
            Object value = redisTemplate.opsForValue().get(key);
            if (value == null) {
                return null;
            }
            
            try {
                if (value instanceof String) {
                    return objectMapper.readValue((String) value, clazz);
                } else {
                    return objectMapper.convertValue(value, clazz);
                }
            } catch (Exception e) {
                log.error("Redis缓存转换异常: {}", e.getMessage());
                return null;
            }
        }, () -> {
            // 检查本地缓存是否过期
            if (localCacheExpiry.containsKey(key) && 
                System.currentTimeMillis() > localCacheExpiry.get(key)) {
                localCache.remove(key);
                localCacheExpiry.remove(key);
                return null;
            }
            
            Object value = localCache.get(key);
            if (value == null) {
                return null;
            }
            
            try {
                return objectMapper.convertValue(value, clazz);
            } catch (Exception e) {
                log.error("本地缓存转换异常: {}", e.getMessage());
                return null;
            }
        });
    }

    @Override
    public boolean delete(String key) {
        return executeWithFallback(() -> {
            Boolean result = redisTemplate.delete(key);
            return Boolean.TRUE.equals(result);
        }, () -> {
            localCache.remove(key);
            localCacheExpiry.remove(key);
            return true;
        });
    }

    @Override
    public long deleteByPattern(String pattern) {
        return executeWithFallback(() -> {
            Set<String> keys = redisTemplate.keys(pattern);
            if (keys != null && !keys.isEmpty()) {
                Long count = redisTemplate.delete(keys);
                return count != null ? count : 0;
            }
            return 0L;
        }, () -> {
            // 简单模式匹配实现
            long count = 0;
            List<String> keysToRemove = new ArrayList<>();
            
            for (String key : localCache.keySet()) {
                if (matchesPattern(key, pattern)) {
                    keysToRemove.add(key);
                    count++;
                }
            }
            
            for (String key : keysToRemove) {
                localCache.remove(key);
                localCacheExpiry.remove(key);
            }
            
            return count;
        });
    }
    
    /**
     * 简单的模式匹配实现
     */
    private boolean matchesPattern(String key, String pattern) {
        // 将Redis的通配符模式转换为Java正则表达式
        String regex = pattern
                .replace(".", "\\.")
                .replace("*", ".*");
        return key.matches(regex);
    }

    @Override
    public boolean expire(String key, long timeout, TimeUnit unit) {
        return executeWithFallback(() -> {
            Boolean result = redisTemplate.expire(key, timeout, unit);
            return Boolean.TRUE.equals(result);
        }, () -> {
            if (localCache.containsKey(key)) {
                long expireTime = System.currentTimeMillis() + unit.toMillis(timeout);
                localCacheExpiry.put(key, expireTime);
                return true;
            }
            return false;
        });
    }

    @Override
    public boolean hasKey(String key) {
        return executeWithFallback(() -> {
            Boolean result = redisTemplate.hasKey(key);
            return Boolean.TRUE.equals(result);
        }, () -> {
            // 检查本地缓存是否过期
            if (localCacheExpiry.containsKey(key) && 
                System.currentTimeMillis() > localCacheExpiry.get(key)) {
                localCache.remove(key);
                localCacheExpiry.remove(key);
                return false;
            }
            return localCache.containsKey(key);
        });
    }

    @Override
    public <T> List<T> getList(String key, Class<T> clazz) {
        return executeWithFallback(() -> {
            Object value = redisTemplate.opsForValue().get(key);
            if (value == null) {
                return new ArrayList<>();
            }
            
            try {
                JavaType javaType = objectMapper.getTypeFactory().constructParametricType(List.class, clazz);
                if (value instanceof String) {
                    return objectMapper.readValue((String) value, javaType);
                } else if (value instanceof List) {
                    List<?> list = (List<?>) value;
                    return list.stream()
                            .map(item -> objectMapper.convertValue(item, clazz))
                            .collect(Collectors.toList());
                }
                return new ArrayList<>();
            } catch (Exception e) {
                log.error("Redis列表缓存转换异常: {}", e.getMessage());
                return new ArrayList<>();
            }
        }, () -> {
            // 检查本地缓存是否过期
            if (localCacheExpiry.containsKey(key) && 
                System.currentTimeMillis() > localCacheExpiry.get(key)) {
                localCache.remove(key);
                localCacheExpiry.remove(key);
                return new ArrayList<>();
            }
            
            Object value = localCache.get(key);
            if (value == null) {
                return new ArrayList<>();
            }
            
            try {
                if (value instanceof List) {
                    List<?> list = (List<?>) value;
                    return list.stream()
                            .map(item -> objectMapper.convertValue(item, clazz))
                            .collect(Collectors.toList());
                }
                return new ArrayList<>();
            } catch (Exception e) {
                log.error("本地列表缓存转换异常: {}", e.getMessage());
                return new ArrayList<>();
            }
        });
    }

    @Override
    public <T> void setList(String key, List<T> list, long timeout, TimeUnit unit) {
        executeWithFallback(() -> {
            redisTemplate.opsForValue().set(key, list, timeout, unit);
            return null;
        }, () -> {
            localCache.put(key, list);
            long expireTime = System.currentTimeMillis() + unit.toMillis(timeout);
            localCacheExpiry.put(key, expireTime);
            return null;
        });
    }

    @Override
    public <T> void setList(String key, List<T> list) {
        executeWithFallback(() -> {
            redisTemplate.opsForValue().set(key, list);
            return null;
        }, () -> {
            localCache.put(key, list);
            return null;
        });
    }

    @Override
    public Map<Object, Object> getHash(String key) {
        return executeWithFallback(() -> {
            return redisTemplate.opsForHash().entries(key);
        }, () -> {
            // 检查本地缓存是否过期
            if (localCacheExpiry.containsKey(key) && 
                System.currentTimeMillis() > localCacheExpiry.get(key)) {
                localCache.remove(key);
                localCacheExpiry.remove(key);
                return new HashMap<>();
            }
            
            Object value = localCache.get(key);
            if (value instanceof Map) {
                return (Map<Object, Object>) value;
            }
            return new HashMap<>();
        });
    }

    @Override
    public void setHash(String key, Map<String, Object> map, long timeout, TimeUnit unit) {
        executeWithFallback(() -> {
            redisTemplate.opsForHash().putAll(key, map);
            redisTemplate.expire(key, timeout, unit);
            return null;
        }, () -> {
            localCache.put(key, map);
            long expireTime = System.currentTimeMillis() + unit.toMillis(timeout);
            localCacheExpiry.put(key, expireTime);
            return null;
        });
    }

    @Override
    public void setHash(String key, Map<String, Object> map) {
        executeWithFallback(() -> {
            redisTemplate.opsForHash().putAll(key, map);
            return null;
        }, () -> {
            localCache.put(key, map);
            return null;
        });
    }

    @Override
    public void setHashField(String key, String field, Object value) {
        executeWithFallback(() -> {
            redisTemplate.opsForHash().put(key, field, value);
            return null;
        }, () -> {
            // 获取或创建哈希表
            Map<String, Object> hash = (Map<String, Object>) localCache.getOrDefault(key, new HashMap<>());
            hash.put(field, value);
            localCache.put(key, hash);
            return null;
        });
    }

    @Override
    public <T> T getHashField(String key, String field, Class<T> clazz) {
        return executeWithFallback(() -> {
            Object value = redisTemplate.opsForHash().get(key, field);
            if (value == null) {
                return null;
            }
            
            try {
                return objectMapper.convertValue(value, clazz);
            } catch (Exception e) {
                log.error("Redis Hash字段转换异常: {}", e.getMessage());
                return null;
            }
        }, () -> {
            // 检查本地缓存是否过期
            if (localCacheExpiry.containsKey(key) && 
                System.currentTimeMillis() > localCacheExpiry.get(key)) {
                localCache.remove(key);
                localCacheExpiry.remove(key);
                return null;
            }
            
            Object hashObj = localCache.get(key);
            if (hashObj instanceof Map) {
                Map<String, Object> hash = (Map<String, Object>) hashObj;
                Object value = hash.get(field);
                if (value != null) {
                    try {
                        return objectMapper.convertValue(value, clazz);
                    } catch (Exception e) {
                        log.error("本地Hash字段转换异常: {}", e.getMessage());
                    }
                }
            }
            return null;
        });
    }

    @Override
    public Long deleteHashFields(String key, Object... fields) {
        return executeWithFallback(() -> {
            return redisTemplate.opsForHash().delete(key, fields);
        }, () -> {
            // 检查本地缓存是否过期
            if (localCacheExpiry.containsKey(key) && 
                System.currentTimeMillis() > localCacheExpiry.get(key)) {
                localCache.remove(key);
                localCacheExpiry.remove(key);
                return 0L;
            }
            
            Object hashObj = localCache.get(key);
            if (hashObj instanceof Map) {
                Map<String, Object> hash = (Map<String, Object>) hashObj;
                long count = 0;
                for (Object field : fields) {
                    if (hash.remove(field) != null) {
                        count++;
                    }
                }
                return count;
            }
            return 0L;
        });
    }

    @Override
    public Long increment(String key, long delta) {
        return executeWithFallback(() -> {
            return redisTemplate.opsForValue().increment(key, delta);
        }, () -> {
            // 检查本地缓存是否过期
            if (localCacheExpiry.containsKey(key) && 
                System.currentTimeMillis() > localCacheExpiry.get(key)) {
                localCache.remove(key);
                localCacheExpiry.remove(key);
                localCache.put(key, delta);
                return delta;
            }
            
            Object value = localCache.get(key);
            long newValue;
            if (value instanceof Number) {
                newValue = ((Number) value).longValue() + delta;
            } else {
                newValue = delta;
            }
            localCache.put(key, newValue);
            return newValue;
        });
    }

    @Override
    public Set<String> keys(String pattern) {
        return executeWithFallback(() -> {
            return redisTemplate.keys(pattern);
        }, () -> {
            // 简单模式匹配实现
            return localCache.keySet().stream()
                    .filter(key -> matchesPattern(key, pattern))
                    .collect(Collectors.toSet());
        });
    }
    
    /**
     * Redis操作接口
     */
    @FunctionalInterface
    private interface RedisOperation<T> {
        T execute();
    }
    
    /**
     * 降级操作接口
     */
    @FunctionalInterface
    private interface FallbackOperation<T> {
        T execute();
    }
} 