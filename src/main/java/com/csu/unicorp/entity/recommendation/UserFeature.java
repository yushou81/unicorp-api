package com.csu.unicorp.entity.recommendation;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户特征实体类
 * 用于存储用户的特征向量，供推荐系统使用
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("user_features")
public class UserFeature {
    
    /**
     * 特征ID，自增主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /**
     * 用户ID
     */
    private Integer userId;
    
    /**
     * 特征向量（JSON格式存储）
     */
    private String featureVector;
    
    /**
     * 技能标签（JSON数组格式存储）
     */
    private String skills;
    
    /**
     * 兴趣领域（JSON数组格式存储）
     */
    private String interests;
    
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
     * 偏好工作类型
     */
    private String preferredJobType;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
} 