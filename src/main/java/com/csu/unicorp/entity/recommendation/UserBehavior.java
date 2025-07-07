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
 * 用户行为记录实体类
 * 用于记录用户的行为数据，供推荐系统分析用户偏好
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("user_behaviors")
public class UserBehavior {
    
    /**
     * 行为ID，自增主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /**
     * 用户ID
     */
    private Integer userId;
    
    /**
     * 行为类型（view-浏览, search-搜索, apply-申请, favorite-收藏）
     */
    private String behaviorType;
    
    /**
     * 目标类型（job-岗位, category-分类）
     */
    private String targetType;
    
    /**
     * 目标ID（如岗位ID、分类ID等）
     */
    private Integer targetId;
    
    /**
     * 行为权重（不同行为的重要性不同）
     */
    private Double weight;
    
    /**
     * 搜索关键词（当行为类型为search时使用）
     */
    private String searchKeyword;
    
    /**
     * 行为发生时间
     */
    private LocalDateTime occurredAt;
} 