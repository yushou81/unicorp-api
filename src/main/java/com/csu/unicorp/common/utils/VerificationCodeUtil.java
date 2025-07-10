package com.csu.unicorp.common.utils;

import java.util.Random;

/**
 * 验证码生成工具类
 */
public class VerificationCodeUtil {
    
    /**
     * 验证码字符集，不包含容易混淆的字符（0, O, 1, I, l）
     */
    private static final String VERIFICATION_CODE_CHARS = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ";
    
    /**
     * 默认验证码长度
     */
    private static final int DEFAULT_CODE_LENGTH = 6;
    
    /**
     * 生成指定长度的随机验证码
     * 
     * @param length 验证码长度
     * @return 生成的验证码
     */
    public static String generateCode(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("验证码长度必须大于0");
        }
        
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(VERIFICATION_CODE_CHARS.length());
            sb.append(VERIFICATION_CODE_CHARS.charAt(index));
        }
        
        return sb.toString();
    }
    
    /**
     * 生成默认长度(6位)的随机验证码
     * 
     * @return 生成的验证码
     */
    public static String generateCode() {
        return generateCode(DEFAULT_CODE_LENGTH);
    }
} 