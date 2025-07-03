package com.csu.unicorp.vo.achievement;

import lombok.Data;

/**
 * 学生成果概览VO
 */
@Data
public class StudentAchievementOverviewVO {
    
    /**
     * 学生ID
     */
    private Integer userId;
    
    /**
     * 学生姓名
     */
    private String userName;
    
    /**
     * 学生昵称
     */
    private String nickname;
    
    /**
     * 学生头像
     */
    private String avatar;
    
    /**
     * 学生所属组织ID
     */
    private Integer organizationId;
    
    /**
     * 学生所属组织名称
     */
    private String organizationName;
    
    /**
     * 专业
     */
    private String major;
    
    /**
     * 教育水平
     */
    private String educationLevel;
    
    /**
     * 作品数量
     */
    private Integer portfolioCount;
    
    /**
     * 竞赛获奖数量
     */
    private Integer awardCount;
    
    /**
     * 科研成果数量
     */
    private Integer researchCount;
    
    /**
     * 总访问量
     */
    private Integer totalViewCount;
    
    /**
     * 总点赞数
     */
    private Integer totalLikeCount;
    
    /**
     * 认证成果数量
     */
    private Integer verifiedCount;
} 