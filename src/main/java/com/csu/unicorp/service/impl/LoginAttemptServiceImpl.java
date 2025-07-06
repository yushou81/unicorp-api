package com.csu.unicorp.service.impl;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.csu.unicorp.common.constants.CacheConstants;
import com.csu.unicorp.service.CacheService;
import com.csu.unicorp.service.LoginAttemptService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 登录尝试服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LoginAttemptServiceImpl implements LoginAttemptService {

    private final CacheService cacheService;
    
    /**
     * 最大尝试次数
     */
    @Value("${auth.login.max-attempts:" + CacheConstants.AUTH_MAX_ATTEMPTS + "}")
    private int maxAttempts;
    
    /**
     * 尝试记录过期时间（分钟）
     */
    @Value("${auth.login.attempts-timeout:" + CacheConstants.AUTH_ATTEMPTS_EXPIRE_TIME + "}")
    private long attemptsTimeout;
    
    /**
     * 默认锁定时间（分钟）
     */
    @Value("${auth.login.default-lock-time:" + CacheConstants.AUTH_DEFAULT_LOCK_TIME + "}")
    private long defaultLockTime;

    @Override
    public int recordFailedAttempt(String username) {
        String key = CacheConstants.AUTH_ATTEMPTS_PREFIX + username;
        
        // 获取当前尝试次数
        Integer attempts = cacheService.get(key, Integer.class);
        int currentAttempts = (attempts == null) ? 0 : attempts;
        
        // 增加尝试次数
        currentAttempts++;
        
        // 更新缓存
        cacheService.set(key, currentAttempts, attemptsTimeout, TimeUnit.MINUTES);
        
        // 计算剩余尝试次数
        int remainingAttempts = maxAttempts - currentAttempts;
        
        // 如果达到最大尝试次数，锁定账户
        if (remainingAttempts <= 0) {
            lockAccount(username, "超过最大登录尝试次数", defaultLockTime);
            log.warn("用户 {} 已被锁定，原因: 超过最大登录尝试次数", username);
            throw new org.springframework.security.authentication.LockedException("账户已被锁定，请在" + defaultLockTime + "分钟后重试");
        } else {
            log.info("用户 {} 登录失败，剩余尝试次数: {}", username, remainingAttempts);
        }
        
        return Math.max(remainingAttempts, 0);
    }

    @Override
    public void resetAttempts(String username) {
        String key = CacheConstants.AUTH_ATTEMPTS_PREFIX + username;
        cacheService.delete(key);
        log.info("用户 {} 登录尝试次数已重置", username);
    }

    @Override
    public boolean isLocked(String username) {
        String key = CacheConstants.AUTH_LOCKED_PREFIX + username;
        return cacheService.hasKey(key);
    }

    @Override
    public void lockAccount(String username, String reason, long lockDurationInMinutes) {
        String key = CacheConstants.AUTH_LOCKED_PREFIX + username;
        cacheService.set(key, reason, lockDurationInMinutes, TimeUnit.MINUTES);
        log.info("用户 {} 已被锁定，原因: {}, 锁定时长: {} 分钟", username, reason, lockDurationInMinutes);
    }

    @Override
    public boolean unlockAccount(String username) {
        String key = CacheConstants.AUTH_LOCKED_PREFIX + username;
        boolean result = cacheService.delete(key);
        
        // 同时重置尝试次数
        if (result) {
            resetAttempts(username);
            log.info("用户 {} 已被解锁", username);
        }
        
        return result;
    }

    @Override
    public int getRemainingAttempts(String username) {
        String key = CacheConstants.AUTH_ATTEMPTS_PREFIX + username;
        Integer attempts = cacheService.get(key, Integer.class);
        
        if (attempts == null) {
            return maxAttempts;
        }
        
        return Math.max(maxAttempts - attempts, 0);
    }

    @Override
    public long getLockTimeRemaining(String username) {
        if (!isLocked(username)) {
            return 0;
        }
        
        String key = CacheConstants.AUTH_LOCKED_PREFIX + username;
        Long expireTime = cacheService.getExpire(key);
        
        if (expireTime == null || expireTime < 0) {
            return 0;
        }
        
        // 转换为分钟并向上取整
        return (expireTime + 59) / 60;
    }
} 