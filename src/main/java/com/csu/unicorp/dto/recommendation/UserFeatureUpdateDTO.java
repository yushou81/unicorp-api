package com.csu.unicorp.dto.recommendation;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 用户特征更新DTO类
 * 用于接收用户特征更新请求
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserFeatureUpdateDTO {
    
    /**
     * 技能标签列表
     */
    private List<String> skills;
    
    /**
     * 兴趣领域列表
     */
    private List<String> interests;
    
    /**
     * 专业领域
     */
    private String major;
    
    /**
     * 学历等级
     */
    private String educationLevel;
    
    /**
     * 偏好工作地点
     */
    private String preferredLocation;
    
    /**
     * 偏好工作类型（单数形式，向后兼容）
     */
    private String preferredJobType;
    
    /**
     * 偏好工作类型列表（复数形式，与接口文档一致）
     */
    private List<String> preferredJobTypes;
} 