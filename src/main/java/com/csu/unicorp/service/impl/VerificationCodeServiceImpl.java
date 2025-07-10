package com.csu.unicorp.service.impl;

import com.csu.unicorp.common.utils.VerificationCodeUtil;
import com.csu.unicorp.service.EmailService;
import com.csu.unicorp.service.VerificationCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 验证码服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VerificationCodeServiceImpl implements VerificationCodeService {
    
    private final StringRedisTemplate redisTemplate;
    private final EmailService emailService;
    
    /**
     * Redis中存储验证码的key前缀
     */
    private static final String EMAIL_CODE_PREFIX = "email_code:";
    
    /**
     * Redis中存储发送限制的key前缀
     */
    private static final String EMAIL_LIMIT_PREFIX = "email_limit:";
    
    /**
     * 验证码有效期（分钟）
     */
    private static final long CODE_EXPIRATION_MINUTES = 5;
    
    /**
     * 发送限制时间（秒）
     */
    private static final long SEND_LIMIT_SECONDS = 60;
    
    @Override
    public String generateEmailVerificationCode(String email) {
        // 先检查是否可以发送验证码
        if (!canSendCode(email)) {
            log.warn("邮箱{}发送验证码过于频繁", email);
            return null;
        }
        
        // 生成验证码
        String code = VerificationCodeUtil.generateCode();
        log.info("为邮箱{}生成验证码: {}", email, code);
        
        // 存储验证码到Redis
        String codeKey = getEmailCodeKey(email);
        redisTemplate.opsForValue().set(codeKey, code, CODE_EXPIRATION_MINUTES, TimeUnit.MINUTES);
        
        // 设置发送限制
        String limitKey = getEmailLimitKey(email);
        redisTemplate.opsForValue().set(limitKey, "1", SEND_LIMIT_SECONDS, TimeUnit.SECONDS);
        
        // 发送验证码邮件
        boolean sendSuccess = emailService.sendVerificationCode(email, code);
        
        if (!sendSuccess) {
            // 发送失败，删除Redis中的验证码
            redisTemplate.delete(codeKey);
            return null;
        }
        
        return code;
    }
    
    @Override
    public boolean verifyEmailCode(String email, String code) {
        if (email == null || code == null) {
            return false;
        }
        
        String codeKey = getEmailCodeKey(email);
        String storedCode = redisTemplate.opsForValue().get(codeKey);
        
        if (storedCode != null && storedCode.equals(code)) {
            // 验证成功，删除Redis中的验证码（一次性使用）
            redisTemplate.delete(codeKey);
            return true;
        }
        
        return false;
    }
    
    @Override
    public boolean canSendCode(String email) {
        String limitKey = getEmailLimitKey(email);
        return Boolean.FALSE.equals(redisTemplate.hasKey(limitKey));
    }
    
    /**
     * 获取邮箱验证码在Redis中的key
     * 
     * @param email 邮箱地址
     * @return Redis的key
     */
    private String getEmailCodeKey(String email) {
        return EMAIL_CODE_PREFIX + email;
    }
    
    /**
     * 获取邮箱发送限制在Redis中的key
     * 
     * @param email 邮箱地址
     * @return Redis的key
     */
    private String getEmailLimitKey(String email) {
        return EMAIL_LIMIT_PREFIX + email;
    }
} 