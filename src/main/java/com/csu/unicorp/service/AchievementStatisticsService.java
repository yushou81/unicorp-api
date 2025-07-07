package com.csu.unicorp.service;

import com.csu.unicorp.vo.achievement.StudentAchievementOverviewVO;
import org.springframework.data.domain.Page;

import java.util.List;
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
    
    /**
     * 获取学校学生成果概览列表
     * 
     * @param userId 当前教师或管理员ID
     * @param page 页码
     * @param size 每页大小
     * @return 学生成果概览分页列表
     */
    Page<StudentAchievementOverviewVO> getSchoolStudentsAchievementOverview(Integer userId, int page, int size);
    
    /**
     * 获取学校成果优秀学生列表
     * 
     * @param userId 当前教师或管理员ID
     * @param limit 返回数量限制
     * @return 优秀学生列表
     */
    List<StudentAchievementOverviewVO> getSchoolTopStudents(Integer userId, int limit);
    
    /**
     * 获取学校成果统计数据
     * 
     * @param userId 当前教师或管理员ID
     * @return 学校成果统计数据
     */
    Map<String, Object> getSchoolAchievementStatistics(Integer userId);
} 