package com.csu.unicorp.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 缓存服务接口
 */
public interface CacheService {

    /**
     * 设置缓存
     * @param key 缓存键
     * @param value 缓存值
     * @param timeout 过期时间
     * @param unit 时间单位
     */
    void set(String key, Object value, long timeout, TimeUnit unit);

    /**
     * 设置缓存（默认过期时间）
     * @param key 缓存键
     * @param value 缓存值
     */
    void set(String key, Object value);

    /**
     * 获取缓存
     * @param key 缓存键
     * @param clazz 返回类型
     * @param <T> 泛型
     * @return 缓存值
     */
    <T> T get(String key, Class<T> clazz);

    /**
     * 删除缓存
     * @param key 缓存键
     * @return 是否成功
     */
    boolean delete(String key);

    /**
     * 批量删除缓存
     * @param pattern 缓存键模式
     * @return 删除数量
     */
    long deleteByPattern(String pattern);

    /**
     * 设置缓存过期时间
     * @param key 缓存键
     * @param timeout 过期时间
     * @param unit 时间单位
     * @return 是否成功
     */
    boolean expire(String key, long timeout, TimeUnit unit);

    /**
     * 判断缓存是否存在
     * @param key 缓存键
     * @return 是否存在
     */
    boolean hasKey(String key);

    /**
     * 获取列表缓存
     * @param key 缓存键
     * @param clazz 元素类型
     * @param <T> 泛型
     * @return 列表
     */
    <T> List<T> getList(String key, Class<T> clazz);

    /**
     * 设置列表缓存
     * @param key 缓存键
     * @param list 列表
     * @param timeout 过期时间
     * @param unit 时间单位
     * @param <T> 泛型
     */
    <T> void setList(String key, List<T> list, long timeout, TimeUnit unit);

    /**
     * 设置列表缓存（默认过期时间）
     * @param key 缓存键
     * @param list 列表
     * @param <T> 泛型
     */
    <T> void setList(String key, List<T> list);

    /**
     * 获取散列表缓存
     * @param key 缓存键
     * @return 散列表
     */
    Map<Object, Object> getHash(String key);

    /**
     * 设置散列表缓存
     * @param key 缓存键
     * @param map 散列表
     * @param timeout 过期时间
     * @param unit 时间单位
     */
    void setHash(String key, Map<String, Object> map, long timeout, TimeUnit unit);

    /**
     * 设置散列表缓存（默认过期时间）
     * @param key 缓存键
     * @param map 散列表
     */
    void setHash(String key, Map<String, Object> map);

    /**
     * 设置散列表字段
     * @param key 缓存键
     * @param field 字段
     * @param value 值
     */
    void setHashField(String key, String field, Object value);

    /**
     * 获取散列表字段
     * @param key 缓存键
     * @param field 字段
     * @param clazz 返回类型
     * @param <T> 泛型
     * @return 值
     */
    <T> T getHashField(String key, String field, Class<T> clazz);

    /**
     * 删除散列表字段
     * @param key 缓存键
     * @param fields 字段
     * @return 删除数量
     */
    Long deleteHashFields(String key, Object... fields);

    /**
     * 增加计数
     * @param key 缓存键
     * @param delta 增量
     * @return 增加后的值
     */
    Long increment(String key, long delta);

    /**
     * 获取匹配的键
     * @param pattern 模式
     * @return 键集合
     */
    Set<String> keys(String pattern);
    
    /**
     * 获取缓存过期时间
     * @param key 缓存键
     * @return 过期时间（秒），如果键不存在或没有设置过期时间则返回-1
     */
    Long getExpire(String key);
} 