package com.csu.unicorp.service;

/**
 * 验证码服务接口
 */
public interface VerificationCodeService {
    
    /**
     * 为指定邮箱生成验证码
     * 
     * @param email 邮箱地址
     * @return 生成的验证码，如果发送失败则返回null
     */
    String generateEmailVerificationCode(String email);
    
    /**
     * 验证邮箱验证码是否正确
     * 
     * @param email 邮箱地址
     * @param code 验证码
     * @return 是否验证成功
     */
    boolean verifyEmailCode(String email, String code);
    
    /**
     * 检查邮箱是否可以发送验证码（是否在限制期内）
     * 
     * @param email 邮箱地址
     * @return 是否可以发送验证码
     */
    boolean canSendCode(String email);
} 