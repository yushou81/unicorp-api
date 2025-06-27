package com.csu.unicorp.common.utils;

import com.csu.unicorp.entity.Organization;
import com.csu.unicorp.entity.User;
import com.csu.unicorp.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 账号生成器
 * 用于系统自动生成唯一账号
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
     * 为学生生成账号
     * 生成格式：学校简称 + 年份后两位 + 5位递增序号
     * 例如：csu25XXXXX (中南大学2025级XXXXX号)
     * 
     * @param organization 学校信息
     * @return 生成的唯一账号
     */
    public String generateStudentAccount(Organization organization) {
        // 提取学校名称拼音首字母作为前缀
        String prefix = extractOrganizationPrefix(organization.getOrganizationName());
        
        // 获取当前年份后两位
        String yearSuffix = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yy"));
        
        // 计算基础序号 (学校ID * 10000 + 当前年份顺序号)
        int baseSequence = organization.getId() * 10000;
        
        // 查找当前学校最大账号序号
        Integer maxSequence = userMapper.selectMaxSequenceByPrefix(prefix + yearSuffix);
        int sequence = maxSequence != null ? maxSequence + 1 : 1;
        
        // 生成账号：前缀 + 年份后两位 + 5位序号（补0）
        String sequenceStr = String.format("%05d", sequence);
        String account = prefix + yearSuffix + sequenceStr;
        
        // 检查账号是否已存在，如果存在则重新生成
        int attempts = 0;
        while (userMapper.selectByAccount(account) != null) {
            sequence++;
            sequenceStr = String.format("%05d", sequence);
            account = prefix + yearSuffix + sequenceStr;
            
            attempts++;
            if (attempts >= MAX_RETRY_ATTEMPTS) {
                // 如果多次尝试失败，添加随机后缀确保唯一性
                String randomSuffix = generateRandomSuffix();
                account = prefix + yearSuffix + sequenceStr + randomSuffix;
                log.warn("多次尝试生成学生账号失败，添加随机后缀: {}", randomSuffix);
                break;
            }
        }
        
        log.info("生成学生账号: {}", account);
        return account;
    }
    
    /**
     * 为教师生成唯一账号
     * 生成格式：teacher_ + 学校简称 + 随机字符 + 时间戳
     * 
     * @param organization 学校信息
     * @return 生成的唯一教师账号
     */
    public String generateTeacherAccount(Organization organization) {
        String prefix = "teacher_" + extractOrganizationPrefix(organization.getOrganizationName());
        return generateUniqueStaffAccount(prefix, "教师");
    }
    
    /**
     * 为企业导师生成唯一账号
     * 生成格式：mentor_ + 企业简称 + 随机字符 + 时间戳
     * 
     * @param organization 企业信息
     * @return 生成的唯一导师账号
     */
    public String generateMentorAccount(Organization organization) {
        String prefix = "mentor_" + extractOrganizationPrefix(organization.getOrganizationName());
        return generateUniqueStaffAccount(prefix, "企业导师");
    }
    
    /**
     * 生成唯一的员工账号（教师/导师）
     * 
     * @param prefix 账号前缀
     * @param staffType 员工类型描述（用于日志）
     * @return 唯一账号
     */
    private String generateUniqueStaffAccount(String prefix, String staffType) {
        String account;
        int attempts = 0;
        
        do {
            // 生成随机字符和时间戳组合
            String randomPart = generateRandomSuffix();
            String timestamp = String.valueOf(System.currentTimeMillis() % 100000);
            
            account = prefix + randomPart + timestamp;
            attempts++;
            
            if (attempts >= MAX_RETRY_ATTEMPTS) {
                // 如果多次尝试失败，使用UUID确保唯一性
                String uuid = UUID.randomUUID().toString().substring(0, 8);
                account = prefix + uuid;
                log.warn("多次尝试生成{}账号失败，使用UUID: {}", staffType, uuid);
                break;
            }
        } while (userMapper.selectByAccount(account) != null);
        
        log.info("生成{}账号: {}", staffType, account);
        return account;
    }
    
    /**
     * 生成随机后缀
     * 
     * @return 3位随机字符
     */
    private String generateRandomSuffix() {
        // 生成3位随机字符（字母+数字）
        StringBuilder sb = new StringBuilder();
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        
        for (int i = 0; i < 3; i++) {
            int index = ThreadLocalRandom.current().nextInt(chars.length());
            sb.append(chars.charAt(index));
        }
        
        return sb.toString();
    }
    
    /**
     * 根据学校名称提取拼音首字母作为前缀
     * 如果无法提取或提取结果不理想，则使用默认前缀+随机字符
     *
     * @param organizationName 组织名称
     * @return 提取的前缀
     */
    private String extractOrganizationPrefix(String organizationName) {
        // 这里可以接入拼音工具库进行更准确的转换
        // 简单实现，针对常见高校提供固定前缀
        if (organizationName.contains("中南大学")) {
            return "csu";
        } else if (organizationName.contains("湖南大学")) {
            return "hnu";
        } else if (organizationName.contains("北京大学")) {
            return "pku";
        } else if (organizationName.contains("清华大学")) {
            return "thu";
        } else if (organizationName.contains("复旦大学")) {
            return "fdu";
        }
        
        // 对于其他学校，提取前几个字符作为前缀
        // 实际项目中应使用拼音转换库获取更准确的拼音首字母
        String defaultPrefix = "uni";
        
        // 如果组织名称太短，加上随机字符
        if (organizationName.length() < 2) {
            return defaultPrefix + ThreadLocalRandom.current().nextInt(10, 100);
        }
        
        // 简单实现：取前三个字符
        return organizationName.substring(0, Math.min(3, organizationName.length())).toLowerCase();
    }
} 