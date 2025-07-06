package com.csu.unicorp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.csu.unicorp.dto.recommendation.RecommendationStatusUpdateDTO;
import com.csu.unicorp.dto.recommendation.UserBehaviorRecordDTO;
import com.csu.unicorp.dto.recommendation.UserFeatureUpdateDTO;
import com.csu.unicorp.vo.JobRecommendationVO;
import com.csu.unicorp.vo.StudentTalentVO;
import com.csu.unicorp.vo.UserFeatureVO;

import java.util.List;
import java.util.Map;

/**
 * 推荐系统服务接口
 */
public interface RecommendationService {
    
    /**
     * 获取学生的岗位推荐列表
     *
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 岗位推荐列表
     */
    IPage<JobRecommendationVO> getJobRecommendations(Integer userId, int page, int size);
    
    /**
     * 获取企业的人才推荐列表
     *
     * @param organizationId 组织ID
     * @param page 页码
     * @param size 每页大小
     * @return 人才推荐列表
     */
    IPage<StudentTalentVO> getTalentRecommendations(Integer organizationId, int page, int size);
    
    /**
     * 更新岗位推荐状态
     *
     * @param recommendationId 推荐ID
     * @param userId 用户ID
     * @param statusUpdateDTO 状态更新DTO
     * @return 是否成功
     */
    boolean updateJobRecommendationStatus(Integer recommendationId, Integer userId, RecommendationStatusUpdateDTO statusUpdateDTO);
    
    /**
     * 更新人才推荐状态
     *
     * @param recommendationId 推荐ID
     * @param organizationId 组织ID
     * @param statusUpdateDTO 状态更新DTO
     * @return 是否成功
     */
    boolean updateTalentRecommendationStatus(Integer recommendationId, Integer organizationId, RecommendationStatusUpdateDTO statusUpdateDTO);
    
    /**
     * 记录用户行为
     *
     * @param userId 用户ID
     * @param behaviorDTO 行为记录DTO
     * @return 是否成功
     */
    boolean recordUserBehavior(Integer userId, UserBehaviorRecordDTO behaviorDTO);
    
    /**
     * 获取用户特征
     *
     * @param userId 用户ID
     * @return 用户特征
     */
    UserFeatureVO getUserFeature(Integer userId);
    
    /**
     * 更新用户特征
     *
     * @param userId 用户ID
     * @param featureDTO 特征更新DTO
     * @return 更新后的用户特征
     */
    UserFeatureVO updateUserFeature(Integer userId, UserFeatureUpdateDTO featureDTO);
    
    /**
     * 为学生生成岗位推荐
     * 
     * @param userId 学生用户ID
     * @return 生成的推荐数量
     */
    int generateJobRecommendationsForUser(Integer userId);
    
    /**
     * 为企业生成人才推荐
     * 
     * @param organizationId 企业组织ID
     * @return 生成的推荐数量
     */
    int generateTalentRecommendationsForOrganization(Integer organizationId);
    
    /**
     * 获取用户行为统计
     * 
     * @param userId 用户ID
     * @return 行为统计数据
     */
    Map<String, Object> getUserBehaviorStatistics(Integer userId);
} 