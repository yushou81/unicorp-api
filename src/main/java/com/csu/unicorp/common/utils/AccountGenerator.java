package com.csu.unicorp.common.utils;

import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Component;

import com.csu.unicorp.entity.organization.Organization;
import com.csu.unicorp.mapper.UserMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * 账号生成器
 * 用于系统自动生成唯一账号 - 统一为8位数字
 */
@Slf4j
@Component
public class AccountGenerator {
    
    private final UserMapper userMapper;
    private static final int MAX_RETRY_ATTEMPTS = 5;
    
    public AccountGenerator(UserMapper userMapper) {
        this.userMapper = userMapper;
    }
    
    /**
     * 为学生生成账号 - 8位数字
     * 
     * @param organization 学校信息
     * @return 生成的唯一账号
     */
    public String generateStudentAccount(Organization organization) {
        return generateUniqueAccount("学生");
    }
    
    /**
     * 为教师生成唯一账号 - 8位数字
     * 
     * @param organization 学校信息
     * @return 生成的唯一教师账号
     */
    public String generateTeacherAccount(Organization organization) {
        return generateUniqueAccount("教师");
    }
    
    /**
     * 为企业导师生成唯一账号 - 8位数字
     * 
     * @param organization 企业信息
     * @return 生成的唯一导师账号
     */
    public String generateMentorAccount(Organization organization) {
        return generateUniqueAccount("企业导师");
    }
    
    /**
     * 生成唯一的8位数字账号
     * 使用时间戳后6位 + 2位随机数
     * 
     * @param userType 用户类型描述（用于日志）
     * @return 唯一账号
     */
    private String generateUniqueAccount(String userType) {
        String account;
        int attempts = 0;
        
        do {
            // 获取当前时间戳后6位
            String timestamp = String.valueOf(System.currentTimeMillis()).substring(7);
            
            // 生成2位随机数
            String randomPart = String.format("%02d", ThreadLocalRandom.current().nextInt(100));
            
            // 组合成8位数字账号
            account = timestamp + randomPart;
            attempts++;
            
            if (attempts >= MAX_RETRY_ATTEMPTS) {
                // 如果多次尝试失败，使用完全随机的8位数字
                account = generateRandomEightDigits();
                log.warn("多次尝试生成{}账号失败，使用完全随机8位数字: {}", userType, account);
                break;
            }
        } while (userMapper.selectByAccount(account) != null);
        
        log.info("生成{}账号: {}", userType, account);
        return account;
    }
    
    /**
     * 生成随机的8位数字
     * 
     * @return 8位随机数字字符串
     */
    private String generateRandomEightDigits() {
        // 生成8位随机数字
        int randomNum = 10000000 + ThreadLocalRandom.current().nextInt(90000000);
        return String.valueOf(randomNum).substring(0, 8);
    }
} 