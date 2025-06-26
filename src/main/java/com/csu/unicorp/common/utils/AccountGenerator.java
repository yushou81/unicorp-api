package com.csu.unicorp.common.utils;

import com.csu.unicorp.entity.Organization;
import com.csu.unicorp.entity.User;
import com.csu.unicorp.mapper.UserMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 账号生成器
 * 用于系统自动生成唯一账号
 */
@Component
public class AccountGenerator {
    
    private final UserMapper userMapper;
    
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
        while (userMapper.selectByAccount(account) != null) {
            sequence++;
            sequenceStr = String.format("%05d", sequence);
            account = prefix + yearSuffix + sequenceStr;
        }
        
        return account;
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