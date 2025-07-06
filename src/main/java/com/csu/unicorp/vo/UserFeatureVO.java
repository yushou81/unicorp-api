package com.csu.unicorp.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户特征VO类
 * 用于返回用户的特征信息
 */
@Data
public class UserFeatureVO {
    
    /**
     * 用户ID
     */
    private Integer userId;
    
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
     * 偏好工作类型
     */
    private String preferredJobType;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 用户行为统计
     */
    private UserBehaviorStatsVO behaviorStats;
    
    /**
     * 用户行为统计内部类
     */
    @Data
    public static class UserBehaviorStatsVO {
        /**
         * 浏览的岗位数量
         */
        private Integer viewedJobsCount;
        
        /**
         * 申请的岗位数量
         */
        private Integer appliedJobsCount;
        
        /**
         * 收藏的岗位数量
         */
        private Integer favoriteJobsCount;
        
        /**
         * 感兴趣的分类列表
         */
        private List<CategoryInterestVO> interestedCategories;
    }
    
    /**
     * 分类兴趣内部类
     */
    @Data
    public static class CategoryInterestVO {
        /**
         * 分类ID
         */
        private Integer categoryId;
        
        /**
         * 分类名称
         */
        private String categoryName;
        
        /**
         * 兴趣分数
         */
        private Double interestScore;
    }
} 