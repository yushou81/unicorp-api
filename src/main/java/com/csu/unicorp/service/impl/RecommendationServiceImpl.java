package com.csu.unicorp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.unicorp.common.exception.BusinessException;
import com.csu.unicorp.common.exception.ResourceNotFoundException;
import com.csu.unicorp.dto.recommendation.RecommendationStatusUpdateDTO;
import com.csu.unicorp.dto.recommendation.UserBehaviorRecordDTO;
import com.csu.unicorp.dto.recommendation.UserFeatureUpdateDTO;
import com.csu.unicorp.entity.job.Job;
import com.csu.unicorp.entity.job.JobCategory;
import com.csu.unicorp.entity.recommendation.JobFeature;
import com.csu.unicorp.entity.recommendation.JobRecommendation;
import com.csu.unicorp.entity.recommendation.TalentRecommendation;
import com.csu.unicorp.entity.recommendation.UserBehavior;
import com.csu.unicorp.entity.recommendation.UserFeature;
import com.csu.unicorp.mapper.job.JobCategoryMapper;
import com.csu.unicorp.mapper.job.JobMapper;
import com.csu.unicorp.mapper.recommendation.JobFeatureMapper;
import com.csu.unicorp.mapper.recommendation.JobRecommendationMapper;
import com.csu.unicorp.mapper.recommendation.TalentRecommendationMapper;
import com.csu.unicorp.mapper.recommendation.UserBehaviorMapper;
import com.csu.unicorp.mapper.recommendation.UserFeatureMapper;
import com.csu.unicorp.service.RecommendationService;
import com.csu.unicorp.vo.JobCategoryVO;
import com.csu.unicorp.vo.JobRecommendationVO;
import com.csu.unicorp.vo.StudentTalentVO;
import com.csu.unicorp.vo.UserFeatureVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 推荐系统服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationServiceImpl implements RecommendationService {

    private final JobRecommendationMapper jobRecommendationMapper;
    private final TalentRecommendationMapper talentRecommendationMapper;
    private final UserBehaviorMapper userBehaviorMapper;
    private final UserFeatureMapper userFeatureMapper;
    private final JobFeatureMapper jobFeatureMapper;
    private final JobMapper jobMapper;
    private final JobCategoryMapper jobCategoryMapper;
    private final ObjectMapper objectMapper;

    // 行为权重配置
    private static final Map<String, Double> BEHAVIOR_WEIGHTS = Map.of(
            "view", 1.0,
            "search", 2.0,
            "apply", 5.0,
            "favorite", 3.0
    );

    @Override
    public IPage<JobRecommendationVO> getJobRecommendations(Integer userId, int page, int size) {
        Page<JobRecommendationVO> pageParam = new Page<>(page, size);
        
        // 获取推荐岗位
        IPage<JobRecommendationVO> jobRecommendations = jobRecommendationMapper.pageRecommendedJobsWithDetails(pageParam, userId);
        
        // 为每个岗位加载分类信息
        for (JobRecommendationVO job : jobRecommendations.getRecords()) {
            // 获取岗位分类
            if (job.getCategoryId() != null) {
                JobCategory category = jobCategoryMapper.selectById(job.getCategoryId());
                if (category != null) {
                    JobCategoryVO categoryVO = new JobCategoryVO();
                    BeanUtils.copyProperties(category, categoryVO);
                    job.setCategory(categoryVO);
                }
            }
        }
        
        return jobRecommendations;
    }

    @Override
    public IPage<StudentTalentVO> getTalentRecommendations(Integer organizationId, int page, int size) {
        Page<StudentTalentVO> pageParam = new Page<>(page, size);
        return talentRecommendationMapper.pageRecommendedTalentsWithDetails(pageParam, organizationId);
    }

    @Override
    @Transactional
    public boolean updateJobRecommendationStatus(Integer recommendationId, Integer userId, RecommendationStatusUpdateDTO statusUpdateDTO) {
        // 检查推荐是否存在且属于当前用户
        JobRecommendation recommendation = jobRecommendationMapper.selectById(recommendationId);
        if (recommendation == null) {
            throw new ResourceNotFoundException("推荐不存在");
        }
        
        if (!recommendation.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此推荐");
        }
        
        // 验证状态值是否有效
        String status = statusUpdateDTO.getStatus();
        if (!List.of("viewed", "ignored", "applied").contains(status)) {
            throw new BusinessException("无效的状态值");
        }
        
        // 更新状态
        recommendation.setStatus(status);
        recommendation.setUpdatedAt(LocalDateTime.now());
        
        return jobRecommendationMapper.updateById(recommendation) > 0;
    }

    @Override
    @Transactional
    public boolean updateTalentRecommendationStatus(Integer recommendationId, Integer organizationId, RecommendationStatusUpdateDTO statusUpdateDTO) {
        // 检查推荐是否存在且属于当前组织
        TalentRecommendation recommendation = talentRecommendationMapper.selectById(recommendationId);
        if (recommendation == null) {
            throw new ResourceNotFoundException("推荐不存在");
        }
        
        if (!recommendation.getOrganizationId().equals(organizationId)) {
            throw new BusinessException("无权操作此推荐");
        }
        
        // 验证状态值是否有效
        String status = statusUpdateDTO.getStatus();
        if (!List.of("viewed", "contacted", "ignored").contains(status)) {
            throw new BusinessException("无效的状态值");
        }
        
        // 更新状态
        recommendation.setStatus(status);
        recommendation.setUpdatedAt(LocalDateTime.now());
        
        return talentRecommendationMapper.updateById(recommendation) > 0;
    }

    @Override
    @Transactional
    public boolean recordUserBehavior(Integer userId, UserBehaviorRecordDTO behaviorDTO) {
        // 验证行为类型是否有效
        String behaviorType = behaviorDTO.getBehaviorType();
        if (!BEHAVIOR_WEIGHTS.containsKey(behaviorType)) {
            throw new BusinessException("无效的行为类型");
        }
        
        // 验证目标类型是否有效
        String targetType = behaviorDTO.getTargetType();
        if (!List.of("job", "category").contains(targetType)) {
            throw new BusinessException("无效的目标类型");
        }
        
        // 创建行为记录
        UserBehavior behavior = new UserBehavior();
        behavior.setUserId(userId);
        behavior.setBehaviorType(behaviorType);
        behavior.setTargetType(targetType);
        behavior.setTargetId(behaviorDTO.getTargetId());
        behavior.setWeight(BEHAVIOR_WEIGHTS.get(behaviorType));
        behavior.setSearchKeyword(behaviorDTO.getSearchKeyword());
        behavior.setOccurredAt(LocalDateTime.now());
        
        return userBehaviorMapper.insert(behavior) > 0;
    }

    @Override
    public UserFeatureVO getUserFeature(Integer userId) {
        // 获取用户特征
        UserFeature userFeature = userFeatureMapper.selectByUserId(userId);
        if (userFeature == null) {
            // 如果不存在，返回空对象
            return new UserFeatureVO();
        }
        
        UserFeatureVO featureVO = new UserFeatureVO();
        featureVO.setUserId(userFeature.getUserId());
        featureVO.setMajor(userFeature.getMajor());
        featureVO.setEducationLevel(userFeature.getEducationLevel());
        featureVO.setPreferredLocation(userFeature.getPreferredLocation());
        featureVO.setPreferredJobType(userFeature.getPreferredJobType());
        
        // 设置preferredJobTypes，如果preferredJobType不为空，则将其作为列表中的唯一元素
        if (userFeature.getPreferredJobType() != null && !userFeature.getPreferredJobType().isEmpty()) {
            featureVO.setPreferredJobTypes(List.of(userFeature.getPreferredJobType()));
        }
        
        featureVO.setUpdatedAt(userFeature.getUpdatedAt());
        
        // 解析JSON格式的技能和兴趣
        try {
            if (StringUtils.hasText(userFeature.getSkills())) {
                featureVO.setSkills(objectMapper.readValue(userFeature.getSkills(), new TypeReference<List<String>>() {}));
            }
            
            if (StringUtils.hasText(userFeature.getInterests())) {
                featureVO.setInterests(objectMapper.readValue(userFeature.getInterests(), new TypeReference<List<String>>() {}));
            }
        } catch (JsonProcessingException e) {
            log.error("解析用户特征JSON失败", e);
        }
        
        // 获取用户行为统计
        Map<String, Object> behaviorStats = getUserBehaviorStatistics(userId);
        UserFeatureVO.UserBehaviorStatsVO statsVO = new UserFeatureVO.UserBehaviorStatsVO();
        statsVO.setViewedJobsCount((Integer) behaviorStats.get("viewedJobsCount"));
        statsVO.setAppliedJobsCount((Integer) behaviorStats.get("appliedJobsCount"));
        statsVO.setFavoriteJobsCount((Integer) behaviorStats.get("favoriteJobsCount"));
        
        // 获取用户感兴趣的分类
        List<Map<String, Object>> categoryInterests = userBehaviorMapper.calculateUserCategoryInterests(userId);
        List<UserFeatureVO.CategoryInterestVO> interestedCategories = new ArrayList<>();
        
        for (Map<String, Object> interest : categoryInterests) {
            Integer categoryId = (Integer) interest.get("category_id");
            Double interestScore = ((Number) interest.get("interest_score")).doubleValue();
            
            JobCategory category = jobCategoryMapper.selectById(categoryId);
            if (category != null) {
                UserFeatureVO.CategoryInterestVO categoryInterestVO = new UserFeatureVO.CategoryInterestVO();
                categoryInterestVO.setCategoryId(categoryId);
                categoryInterestVO.setCategoryName(category.getName());
                categoryInterestVO.setInterestScore(interestScore);
                interestedCategories.add(categoryInterestVO);
            }
        }
        
        statsVO.setInterestedCategories(interestedCategories);
        featureVO.setBehaviorStats(statsVO);
        
        return featureVO;
    }

    @Override
    @Transactional
    public UserFeatureVO updateUserFeature(Integer userId, UserFeatureUpdateDTO featureDTO) {
        // 检查用户特征是否存在
        UserFeature userFeature = userFeatureMapper.selectByUserId(userId);
        boolean isNew = false;
        
        if (userFeature == null) {
            // 如果不存在，创建新的
            userFeature = new UserFeature();
            userFeature.setUserId(userId);
            userFeature.setCreatedAt(LocalDateTime.now());
            isNew = true;
        }
        
        // 更新基本信息
        userFeature.setMajor(featureDTO.getMajor());
        userFeature.setEducationLevel(featureDTO.getEducationLevel());
        userFeature.setPreferredLocation(featureDTO.getPreferredLocation());
        
        // 处理偏好工作类型，优先使用preferredJobTypes
        if (featureDTO.getPreferredJobTypes() != null && !featureDTO.getPreferredJobTypes().isEmpty()) {
            // 如果有多个工作类型，使用第一个作为单一工作类型
            userFeature.setPreferredJobType(featureDTO.getPreferredJobTypes().get(0));
        } else if (featureDTO.getPreferredJobType() != null) {
            // 如果没有preferredJobTypes但有preferredJobType，则使用preferredJobType
        userFeature.setPreferredJobType(featureDTO.getPreferredJobType());
        }
        
        userFeature.setUpdatedAt(LocalDateTime.now());
        
        // 将列表转换为JSON字符串
        try {
            if (featureDTO.getSkills() != null) {
                userFeature.setSkills(objectMapper.writeValueAsString(featureDTO.getSkills()));
            }
            
            if (featureDTO.getInterests() != null) {
                userFeature.setInterests(objectMapper.writeValueAsString(featureDTO.getInterests()));
            }
        } catch (JsonProcessingException e) {
            log.error("转换用户特征为JSON失败", e);
            throw new BusinessException("保存用户特征失败");
        }
        
        // 保存或更新
        if (isNew) {
            userFeatureMapper.insert(userFeature);
        } else {
            userFeatureMapper.updateById(userFeature);
        }
        
        // 返回更新后的特征
        return getUserFeature(userId);
    }

    @Override
    @Transactional
    public int generateJobRecommendationsForUser(Integer userId) {
        // 获取用户特征
        UserFeature userFeature = userFeatureMapper.selectByUserId(userId);
        if (userFeature == null) {
            log.warn("用户特征不存在，无法生成推荐，userId={}", userId);
            return 0;
        }
        
        // 获取所有开放状态的岗位特征
        List<JobFeature> allJobFeatures = jobFeatureMapper.selectAllActiveJobFeatures();
        if (allJobFeatures.isEmpty()) {
            log.warn("没有可用的岗位特征，无法生成推荐");
            return 0;
        }
        
        // 计算用户与每个岗位的匹配度
        List<Map.Entry<Integer, Double>> jobScores = new ArrayList<>();
        
        for (JobFeature jobFeature : allJobFeatures) {
            // 计算匹配分数
            double score = calculateMatchScore(userFeature, jobFeature);
            jobScores.add(Map.entry(jobFeature.getJobId(), score));
        }
        
        // 按分数降序排序
        jobScores.sort(Map.Entry.<Integer, Double>comparingByValue().reversed());
        
        // 取前10个最匹配的岗位
        int count = 0;
        for (int i = 0; i < Math.min(10, jobScores.size()); i++) {
            Map.Entry<Integer, Double> entry = jobScores.get(i);
            Integer jobId = entry.getKey();
            Double score = entry.getValue();
            
            // 检查是否已推荐过
            int existingCount = jobRecommendationMapper.countExistingRecommendation(userId, jobId);
            if (existingCount > 0) {
                continue;
            }
            
            // 创建推荐记录
            JobRecommendation recommendation = new JobRecommendation();
            recommendation.setUserId(userId);
            recommendation.setJobId(jobId);
            recommendation.setScore(score);
            recommendation.setReason(generateRecommendationReason(userFeature, jobFeatureMapper.selectByJobId(jobId)));
            recommendation.setStatus("new");
            recommendation.setCreatedAt(LocalDateTime.now());
            recommendation.setUpdatedAt(LocalDateTime.now());
            
            jobRecommendationMapper.insert(recommendation);
            count++;
        }
        
        return count;
    }

    @Override
    @Transactional
    public int generateTalentRecommendationsForOrganization(Integer organizationId) {
        // 获取组织发布的所有岗位
        List<Job> organizationJobs = jobMapper.selectList(
                new LambdaQueryWrapper<Job>()
                        .eq(Job::getOrganizationId, organizationId)
                        .eq(Job::getStatus, "open")
                        .eq(Job::getIsDeleted, false)
        );
        
        if (organizationJobs.isEmpty()) {
            log.warn("组织没有发布岗位，无法生成人才推荐，organizationId={}", organizationId);
            return 0;
        }
        
        // 获取所有学生用户特征
        List<UserFeature> allStudentFeatures = userFeatureMapper.selectAllStudentFeatures();
        if (allStudentFeatures.isEmpty()) {
            log.warn("没有可用的学生特征，无法生成推荐");
            return 0;
        }
        
        // 计算每个学生与组织岗位的最高匹配度
        Map<Integer, Double> studentScores = new HashMap<>();
        Map<Integer, String> studentReasons = new HashMap<>();
        
        for (UserFeature studentFeature : allStudentFeatures) {
            double maxScore = 0.0;
            String bestReason = "";
            
            for (Job job : organizationJobs) {
                JobFeature jobFeature = jobFeatureMapper.selectByJobId(job.getId());
                if (jobFeature != null) {
                    double score = calculateMatchScore(studentFeature, jobFeature);
                    if (score > maxScore) {
                        maxScore = score;
                        bestReason = "与贵公司的 " + job.getTitle() + " 岗位匹配度高";
                    }
                }
            }
            
            if (maxScore > 0) {
                studentScores.put(studentFeature.getUserId(), maxScore);
                studentReasons.put(studentFeature.getUserId(), bestReason);
            }
        }
        
        // 按分数降序排序
        List<Map.Entry<Integer, Double>> sortedStudents = new ArrayList<>(studentScores.entrySet());
        sortedStudents.sort(Map.Entry.<Integer, Double>comparingByValue().reversed());
        
        // 取前10个最匹配的学生
        int count = 0;
        for (int i = 0; i < Math.min(10, sortedStudents.size()); i++) {
            Map.Entry<Integer, Double> entry = sortedStudents.get(i);
            Integer studentId = entry.getKey();
            Double score = entry.getValue();
            
            // 检查是否已推荐过
            int existingCount = talentRecommendationMapper.countExistingRecommendation(organizationId, studentId);
            if (existingCount > 0) {
                continue;
            }
            
            // 创建推荐记录
            TalentRecommendation recommendation = new TalentRecommendation();
            recommendation.setOrganizationId(organizationId);
            recommendation.setStudentId(studentId);
            recommendation.setScore(score);
            recommendation.setReason(studentReasons.get(studentId));
            recommendation.setStatus("new");
            recommendation.setCreatedAt(LocalDateTime.now());
            recommendation.setUpdatedAt(LocalDateTime.now());
            
            talentRecommendationMapper.insert(recommendation);
            count++;
        }
        
        return count;
    }

    @Override
    public Map<String, Object> getUserBehaviorStatistics(Integer userId) {
        Map<String, Object> statistics = new HashMap<>();
        
        // 统计各类行为数量
        int viewedJobsCount = userBehaviorMapper.selectUserBehaviorsByType(userId, "view").size();
        int appliedJobsCount = userBehaviorMapper.selectUserBehaviorsByType(userId, "apply").size();
        int favoriteJobsCount = userBehaviorMapper.selectUserBehaviorsByType(userId, "favorite").size();
        
        statistics.put("viewedJobsCount", viewedJobsCount);
        statistics.put("appliedJobsCount", appliedJobsCount);
        statistics.put("favoriteJobsCount", favoriteJobsCount);
        
        // 获取最近的搜索关键词
        List<String> recentSearches = userBehaviorMapper.getRecentSearchKeywords(userId, 5);
        statistics.put("recentSearches", recentSearches);
        
        // 获取用户感兴趣的分类
        List<Map<String, Object>> categoryInterests = userBehaviorMapper.calculateUserCategoryInterests(userId);
        statistics.put("categoryInterests", categoryInterests);
        
        return statistics;
    }
    
    /**
     * 计算用户特征与岗位特征的匹配分数
     */
    private double calculateMatchScore(UserFeature userFeature, JobFeature jobFeature) {
        double score = 0.0;
        
        // 1. 基于专业匹配
        if (userFeature.getMajor() != null && jobFeature.getKeywords() != null) {
            if (jobFeature.getKeywords().contains(userFeature.getMajor())) {
                score += 10.0;
            }
        }
        
        // 2. 基于技能匹配
        try {
            if (StringUtils.hasText(userFeature.getSkills()) && StringUtils.hasText(jobFeature.getRequiredSkills())) {
                List<String> userSkills = objectMapper.readValue(userFeature.getSkills(), new TypeReference<List<String>>() {});
                List<String> jobSkills = objectMapper.readValue(jobFeature.getRequiredSkills(), new TypeReference<List<String>>() {});
                
                // 计算技能匹配度
                int matchedSkills = 0;
                for (String skill : userSkills) {
                    if (jobSkills.contains(skill)) {
                        matchedSkills++;
                    }
                }
                
                if (!jobSkills.isEmpty()) {
                    score += 20.0 * ((double) matchedSkills / jobSkills.size());
                }
            }
        } catch (JsonProcessingException e) {
            log.error("解析技能JSON失败", e);
        }
        
        // 3. 基于兴趣与岗位关键词匹配
        try {
            if (StringUtils.hasText(userFeature.getInterests()) && StringUtils.hasText(jobFeature.getKeywords())) {
                List<String> userInterests = objectMapper.readValue(userFeature.getInterests(), new TypeReference<List<String>>() {});
                List<String> jobKeywords = objectMapper.readValue(jobFeature.getKeywords(), new TypeReference<List<String>>() {});
                
                // 计算兴趣匹配度
                int matchedInterests = 0;
                for (String interest : userInterests) {
                    for (String keyword : jobKeywords) {
                        if (keyword.contains(interest) || interest.contains(keyword)) {
                            matchedInterests++;
                            break;
                        }
                    }
                }
                
                if (!userInterests.isEmpty()) {
                    score += 15.0 * ((double) matchedInterests / userInterests.size());
                }
            }
        } catch (JsonProcessingException e) {
            log.error("解析兴趣/关键词JSON失败", e);
        }
        
        // 4. 基于学历匹配
        if (userFeature.getEducationLevel() != null && jobFeature.getJobId() != null) {
            Job job = jobMapper.selectById(jobFeature.getJobId());
            if (job != null && job.getEducationRequirement() != null) {
                // 学历等级映射
                Map<String, Integer> eduLevels = Map.of(
                        "bachelor", 1,
                        "master", 2,
                        "doctorate", 3,
                        "any", 0
                );
                
                Integer userEduLevel = eduLevels.getOrDefault(userFeature.getEducationLevel(), 0);
                Integer jobEduLevel = eduLevels.getOrDefault(job.getEducationRequirement(), 0);
                
                // 如果用户学历符合或高于要求
                if (jobEduLevel == 0 || userEduLevel >= jobEduLevel) {
                    score += 15.0;
                } else {
                    // 学历不符合要求，降低分数
                    score -= 10.0;
                }
            }
        }
        
        // 5. 基于工作地点偏好匹配
        if (userFeature.getPreferredLocation() != null && jobFeature.getJobId() != null) {
            Job job = jobMapper.selectById(jobFeature.getJobId());
            if (job != null && job.getLocation() != null) {
                if (job.getLocation().contains(userFeature.getPreferredLocation()) || 
                    userFeature.getPreferredLocation().contains(job.getLocation())) {
                    score += 10.0;
                }
            }
        }
        
        // 6. 基于工作类型偏好匹配
        if (userFeature.getPreferredJobType() != null && jobFeature.getJobId() != null) {
            Job job = jobMapper.selectById(jobFeature.getJobId());
            if (job != null && job.getJobType() != null) {
                if (job.getJobType().equals(userFeature.getPreferredJobType())) {
                    score += 10.0;
                }
            }
        }
        
        return Math.max(0, score);
    }
    
    /**
     * 生成推荐原因
     */
    private String generateRecommendationReason(UserFeature userFeature, JobFeature jobFeature) {
        if (jobFeature == null || jobFeature.getJobId() == null) {
            return "根据您的个人特征推荐";
        }
        
        Job job = jobMapper.selectById(jobFeature.getJobId());
        if (job == null) {
            return "根据您的个人特征推荐";
        }
        
        List<String> reasons = new ArrayList<>();
        
        // 1. 基于专业匹配
        if (userFeature.getMajor() != null && jobFeature.getKeywords() != null) {
            if (jobFeature.getKeywords().contains(userFeature.getMajor())) {
                reasons.add("与您的专业相关");
            }
        }
        
        // 2. 基于技能匹配
        try {
            if (StringUtils.hasText(userFeature.getSkills()) && StringUtils.hasText(jobFeature.getRequiredSkills())) {
                List<String> userSkills = objectMapper.readValue(userFeature.getSkills(), new TypeReference<List<String>>() {});
                List<String> jobSkills = objectMapper.readValue(jobFeature.getRequiredSkills(), new TypeReference<List<String>>() {});
                
                List<String> matchedSkills = userSkills.stream()
                        .filter(jobSkills::contains)
                        .collect(Collectors.toList());
                
                if (!matchedSkills.isEmpty()) {
                    if (matchedSkills.size() == 1) {
                        reasons.add("需要您掌握的" + matchedSkills.get(0) + "技能");
                    } else {
                        reasons.add("需要您掌握的多项技能");
                    }
                }
            }
        } catch (JsonProcessingException e) {
            log.error("解析技能JSON失败", e);
        }
        
        // 3. 基于工作地点偏好匹配
        if (userFeature.getPreferredLocation() != null && job.getLocation() != null) {
            if (job.getLocation().contains(userFeature.getPreferredLocation()) || 
                userFeature.getPreferredLocation().contains(job.getLocation())) {
                reasons.add("位于您偏好的" + job.getLocation() + "地区");
            }
        }
        
        // 4. 基于工作类型偏好匹配
        if (userFeature.getPreferredJobType() != null && job.getJobType() != null) {
            if (job.getJobType().equals(userFeature.getPreferredJobType())) {
                String jobTypeDesc = "";
                switch (job.getJobType()) {
                    case "full_time":
                        jobTypeDesc = "全职";
                        break;
                    case "part_time":
                        jobTypeDesc = "兼职";
                        break;
                    case "internship":
                        jobTypeDesc = "实习";
                        break;
                    case "remote":
                        jobTypeDesc = "远程";
                        break;
                    default:
                        jobTypeDesc = job.getJobType();
                }
                reasons.add("符合您偏好的" + jobTypeDesc + "工作类型");
            }
        }
        
        // 如果没有具体原因，返回默认原因
        if (reasons.isEmpty()) {
            return "根据您的个人特征推荐";
        }
        
        // 最多返回两个原因
        if (reasons.size() > 2) {
            reasons = reasons.subList(0, 2);
        }
        
        return String.join("，", reasons);
    }
} 