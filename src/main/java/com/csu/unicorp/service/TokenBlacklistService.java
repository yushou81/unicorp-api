package com.csu.unicorp.service;

/**
 * 令牌黑名单服务接口
 * 用于管理已失效的JWT令牌
 */
public interface TokenBlacklistService {

    /**
     * 将令牌添加到黑名单
     *
     * @param token 令牌
     * @param reason 失效原因
     * @param expirationTimeInSeconds 令牌过期时间（秒）
     */
    void addToBlacklist(String token, String reason, long expirationTimeInSeconds);

    /**
     * 检查令牌是否在黑名单中
     *
     * @param token 令牌
     * @return 是否在黑名单中
     */
    boolean isBlacklisted(String token);

    /**
     * 从黑名单中移除令牌
     *
     * @param token 令牌
     * @return 是否成功移除
     */
    boolean removeFromBlacklist(String token);
} 