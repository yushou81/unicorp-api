package com.csu.unicorp.service.impl;

import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.csu.unicorp.common.constants.CacheConstants;
import com.csu.unicorp.service.CacheService;
import com.csu.unicorp.service.TokenBlacklistService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 令牌黑名单服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TokenBlacklistServiceImpl implements TokenBlacklistService {

    private final CacheService cacheService;
    
    /**
     * 黑名单键前缀
     */
    private static final String BLACKLIST_PREFIX = "auth:blacklist:";

    @Override
    public void addToBlacklist(String token, String reason, long expirationTimeInSeconds) {
        String key = CacheConstants.AUTH_BLACKLIST_PREFIX + token;
        cacheService.set(key, reason, expirationTimeInSeconds, TimeUnit.SECONDS);
        log.info("令牌已添加到黑名单，原因: {}, 过期时间: {} 秒", reason, expirationTimeInSeconds);
    }

    @Override
    public boolean isBlacklisted(String token) {
        String key = CacheConstants.AUTH_BLACKLIST_PREFIX + token;
        return cacheService.hasKey(key);
    }

    @Override
    public boolean removeFromBlacklist(String token) {
        String key = CacheConstants.AUTH_BLACKLIST_PREFIX + token;
        return cacheService.delete(key);
    }
} 