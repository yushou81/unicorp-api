package com.csu.unicorp.service;

import com.csu.unicorp.vo.achievement.StudentAchievementOverviewVO;

import java.util.Map;

/**
 * 学生成果统计Service接口
 */
public interface AchievementStatisticsService {
    
    /**
     * 获取学生成果概览
     * 
     * @param userId 用户ID
     * @return 学生成果概览
     */
    StudentAchievementOverviewVO getStudentAchievementOverview(Integer userId);
    
    /**
     * 获取学生成果访问统计
     * 
     * @param userId 用户ID
     * @return 访问统计数据
     */
    Map<String, Object> getStudentAchievementViewStatistics(Integer userId);
    
    /**
     * 获取组织成果统计
     * 
     * @param organizationId 组织ID
     * @return 组织成果统计数据
     */
    Map<String, Object> getOrganizationAchievementStatistics(Integer organizationId);
} 