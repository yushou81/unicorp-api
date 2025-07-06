package com.csu.unicorp.service.impl.achievement;

import com.csu.unicorp.entity.User;
import com.csu.unicorp.mapper.UserMapper;
import com.csu.unicorp.mapper.achievement.AchievementViewMapper;
import com.csu.unicorp.mapper.achievement.CompetitionAwardMapper;
import com.csu.unicorp.mapper.achievement.PortfolioItemMapper;
import com.csu.unicorp.mapper.achievement.ResearchAchievementMapper;
import com.csu.unicorp.service.AchievementStatisticsService;
import com.csu.unicorp.service.FileService;
import com.csu.unicorp.vo.achievement.StudentAchievementOverviewVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 学生成果统计Service实现类
 */
@Service
@RequiredArgsConstructor
public class AchievementStatisticsServiceImpl implements AchievementStatisticsService {

    private final UserMapper userMapper;
    private final PortfolioItemMapper portfolioItemMapper;
    private final CompetitionAwardMapper competitionAwardMapper;
    private final ResearchAchievementMapper researchAchievementMapper;
    private final AchievementViewMapper achievementViewMapper;
    private final FileService fileService;
    
    @Override
    public StudentAchievementOverviewVO getStudentAchievementOverview(Integer userId) {
        // 查询用户基本信息
        User user = userMapper.selectById(userId);
        if (user == null) {
            return null;
        }

        // 查询用户的实名认证信息和学生档案
        Map<String, Object> userInfo = userMapper.getUserVerificationAndProfile(userId);
        
        // 查询用户所属组织名称
        String organizationName = null;
        if (user.getOrganizationId() != null) {
            organizationName = userMapper.selectOrganizationNameById(user.getOrganizationId());
        }
        
        // 查询各类成果数量
        int portfolioCount = portfolioItemMapper.selectByUserId(userId).size();
        int awardCount = competitionAwardMapper.selectByUserId(userId).size();
        int researchCount = researchAchievementMapper.selectByUserId(userId).size();
        
        // 查询认证成果数量
        int verifiedAwardCount = (int) competitionAwardMapper.selectByUserId(userId).stream()
                .filter(award -> award.getIsVerified())
                .count();
        int verifiedResearchCount = (int) researchAchievementMapper.selectByUserId(userId).stream()
                .filter(research -> research.getIsVerified())
                .count();
        int verifiedCount = verifiedAwardCount + verifiedResearchCount;
        
        // 查询总访问量
        int totalViewCount = achievementViewMapper.countByUserId(userId);
        
        // 查询总点赞数（目前只有作品项目有点赞功能）
        int totalLikeCount = portfolioItemMapper.selectByUserId(userId).stream()
                .mapToInt(item -> item.getLikeCount())
                .sum();
        
        // 组装VO
        StudentAchievementOverviewVO overview = new StudentAchievementOverviewVO();
        overview.setUserId(userId);
        overview.setUserName(user.getNickname());
        overview.setNickname(user.getNickname());
        overview.setAvatar(fileService.getFullFileUrl(user.getAvatar()));
        overview.setOrganizationId(user.getOrganizationId());
        overview.setOrganizationName(organizationName);
        
        // 设置专业信息（如果有）
        if (userInfo != null && userInfo.get("major") != null) {
            overview.setMajor((String) userInfo.get("major"));
        }
        
        // 设置教育水平（暂无此字段，可以根据需要添加）
        overview.setEducationLevel("本科"); // 默认值，实际应该从用户信息中获取
        
        // 设置统计数据
        overview.setPortfolioCount(portfolioCount);
        overview.setAwardCount(awardCount);
        overview.setResearchCount(researchCount);
        overview.setTotalViewCount(totalViewCount);
        overview.setTotalLikeCount(totalLikeCount);
        overview.setVerifiedCount(verifiedCount);
        
        return overview;
    }

    @Override
    public Map<String, Object> getStudentAchievementViewStatistics(Integer userId) {
        Map<String, Object> result = new HashMap<>();
        
        // 获取各类成果的访问统计
        List<Map<String, Object>> viewStats = achievementViewMapper.countByUserIdGroupByType(userId);
        
        // 处理统计数据
        int portfolioViews = 0;
        int awardViews = 0;
        int researchViews = 0;
        
        for (Map<String, Object> stat : viewStats) {
            String type = (String) stat.get("type");
            Long count = (Long) stat.get("count");
            
            if ("portfolio".equals(type)) {
                portfolioViews = count.intValue();
            } else if ("award".equals(type)) {
                awardViews = count.intValue();
            } else if ("research".equals(type)) {
                researchViews = count.intValue();
            }
        }
        
        int totalViews = portfolioViews + awardViews + researchViews;
        
        // 组装结果
        result.put("totalViews", totalViews);
        result.put("portfolioViews", portfolioViews);
        result.put("awardViews", awardViews);
        result.put("researchViews", researchViews);
        
        // 计算各类成果访问占比
        if (totalViews > 0) {
            result.put("portfolioViewsPercentage", Math.round((float) portfolioViews / totalViews * 100));
            result.put("awardViewsPercentage", Math.round((float) awardViews / totalViews * 100));
            result.put("researchViewsPercentage", Math.round((float) researchViews / totalViews * 100));
        } else {
            result.put("portfolioViewsPercentage", 0);
            result.put("awardViewsPercentage", 0);
            result.put("researchViewsPercentage", 0);
        }
        
        return result;
    }

    @Override
    public Map<String, Object> getOrganizationAchievementStatistics(Integer organizationId) {
        Map<String, Object> result = new HashMap<>();
        
        // 查询该组织下的所有学生ID
        List<User> students = userMapper.selectList(null).stream()
                .filter(user -> organizationId.equals(user.getOrganizationId()))
                .filter(user -> userMapper.hasRole(user.getId(), "STUDENT"))
                .toList();
        
        int totalStudents = students.size();
        int totalPortfolios = 0;
        int totalAwards = 0;
        int totalResearch = 0;
        int totalVerifiedAwards = 0;
        int totalVerifiedResearch = 0;
        
        // 统计该组织下所有学生的成果数量
        for (User student : students) {
            Integer userId = student.getId();
            
            // 统计作品数量
            totalPortfolios += portfolioItemMapper.selectByUserId(userId).size();
            
            // 统计获奖数量和认证获奖数量
            List<com.csu.unicorp.entity.achievement.CompetitionAward> awards = competitionAwardMapper.selectByUserId(userId);
            totalAwards += awards.size();
            totalVerifiedAwards += awards.stream().filter(award -> award.getIsVerified()).count();
            
            // 统计科研成果数量和认证科研成果数量
            List<com.csu.unicorp.entity.achievement.ResearchAchievement> researches = researchAchievementMapper.selectByUserId(userId);
            totalResearch += researches.size();
            totalVerifiedResearch += researches.stream().filter(research -> research.getIsVerified()).count();
        }
        
        // 组装结果
        result.put("organizationId", organizationId);
        result.put("organizationName", userMapper.selectOrganizationNameById(organizationId));
        result.put("totalStudents", totalStudents);
        result.put("totalPortfolios", totalPortfolios);
        result.put("totalAwards", totalAwards);
        result.put("totalResearch", totalResearch);
        result.put("totalAchievements", totalPortfolios + totalAwards + totalResearch);
        result.put("totalVerifiedAchievements", totalVerifiedAwards + totalVerifiedResearch);
        
        // 计算平均值
        if (totalStudents > 0) {
            result.put("avgPortfoliosPerStudent", Math.round((float) totalPortfolios / totalStudents * 10) / 10.0);
            result.put("avgAwardsPerStudent", Math.round((float) totalAwards / totalStudents * 10) / 10.0);
            result.put("avgResearchPerStudent", Math.round((float) totalResearch / totalStudents * 10) / 10.0);
            result.put("avgAchievementsPerStudent", Math.round((float) (totalPortfolios + totalAwards + totalResearch) / totalStudents * 10) / 10.0);
        } else {
            result.put("avgPortfoliosPerStudent", 0);
            result.put("avgAwardsPerStudent", 0);
            result.put("avgResearchPerStudent", 0);
            result.put("avgAchievementsPerStudent", 0);
        }
        
        return result;
    }
    
    @Override
    public Page<StudentAchievementOverviewVO> getSchoolStudentsAchievementOverview(Integer userId, int page, int size) {
        // 获取当前教师或管理员所属的组织ID
        User currentUser = userMapper.selectById(userId);
        if (currentUser == null || currentUser.getOrganizationId() == null) {
            return Page.empty(PageRequest.of(page, size));
        }
        
        Integer organizationId = currentUser.getOrganizationId();
        
        // 获取该组织下的所有学生
        List<User> students = userMapper.selectList(null).stream()
                .filter(user -> organizationId.equals(user.getOrganizationId()))
                .filter(user -> userMapper.hasRole(user.getId(), "STUDENT"))
                .toList();
        
        // 为每个学生生成成果概览
        List<StudentAchievementOverviewVO> allOverviews = new ArrayList<>();
        for (User student : students) {
            StudentAchievementOverviewVO overview = getStudentAchievementOverview(student.getId());
            if (overview != null) {
                allOverviews.add(overview);
            }
        }
        
        // 分页处理
        int start = page * size;
        int end = Math.min(start + size, allOverviews.size());
        
        if (start >= allOverviews.size()) {
            return Page.empty(PageRequest.of(page, size));
        }
        
        List<StudentAchievementOverviewVO> pageContent = allOverviews.subList(start, end);
        
        return new PageImpl<>(pageContent, PageRequest.of(page, size), allOverviews.size());
    }
    
    @Override
    public List<StudentAchievementOverviewVO> getSchoolTopStudents(Integer userId, int limit) {
        // 获取当前教师或管理员所属的组织ID
        User currentUser = userMapper.selectById(userId);
        if (currentUser == null || currentUser.getOrganizationId() == null) {
            return new ArrayList<>();
        }
        
        Integer organizationId = currentUser.getOrganizationId();
        
        // 获取该组织下的所有学生
        List<User> students = userMapper.selectList(null).stream()
                .filter(user -> organizationId.equals(user.getOrganizationId()))
                .filter(user -> userMapper.hasRole(user.getId(), "STUDENT"))
                .toList();
        
        // 为每个学生生成成果概览
        List<StudentAchievementOverviewVO> allOverviews = new ArrayList<>();
        for (User student : students) {
            StudentAchievementOverviewVO overview = getStudentAchievementOverview(student.getId());
            if (overview != null) {
                allOverviews.add(overview);
            }
        }
        
        // 根据成果总数排序（作品数+获奖数+科研成果数）
        return allOverviews.stream()
                .sorted(Comparator.comparingInt(o -> 
                    (o.getPortfolioCount() + o.getAwardCount() + o.getResearchCount()) * -1)) // 降序排序
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    @Override
    public Map<String, Object> getSchoolAchievementStatistics(Integer userId) {
        // 获取当前教师或管理员所属的组织ID
        User currentUser = userMapper.selectById(userId);
        if (currentUser == null || currentUser.getOrganizationId() == null) {
            return new HashMap<>();
        }
        
        Integer organizationId = currentUser.getOrganizationId();
        
        // 直接调用组织成果统计方法
        Map<String, Object> statistics = getOrganizationAchievementStatistics(organizationId);
        
        // 添加更多学校特有的统计数据
        // 例如：按专业统计、按年级统计等
        // 这里可以根据需求添加更多统计数据
        
        // 获取该组织下的所有学生
        List<User> students = userMapper.selectList(null).stream()
                .filter(user -> organizationId.equals(user.getOrganizationId()))
                .filter(user -> userMapper.hasRole(user.getId(), "STUDENT"))
                .toList();
        
        // 统计有成果的学生数量
        int studentsWithAchievements = 0;
        int studentsWithPortfolios = 0;
        int studentsWithAwards = 0;
        int studentsWithResearch = 0;
        
        for (User student : students) {
            Integer studentId = student.getId();
            
            int portfolioCount = portfolioItemMapper.selectByUserId(studentId).size();
            int awardCount = competitionAwardMapper.selectByUserId(studentId).size();
            int researchCount = researchAchievementMapper.selectByUserId(studentId).size();
            
            if (portfolioCount > 0 || awardCount > 0 || researchCount > 0) {
                studentsWithAchievements++;
            }
            
            if (portfolioCount > 0) {
                studentsWithPortfolios++;
            }
            
            if (awardCount > 0) {
                studentsWithAwards++;
            }
            
            if (researchCount > 0) {
                studentsWithResearch++;
            }
        }
        
        // 添加统计数据
        statistics.put("studentsWithAchievements", studentsWithAchievements);
        statistics.put("studentsWithPortfolios", studentsWithPortfolios);
        statistics.put("studentsWithAwards", studentsWithAwards);
        statistics.put("studentsWithResearch", studentsWithResearch);
        
        // 计算百分比
        int totalStudents = students.size();
        if (totalStudents > 0) {
            statistics.put("achievementCoverage", Math.round((float) studentsWithAchievements / totalStudents * 100));
            statistics.put("portfolioCoverage", Math.round((float) studentsWithPortfolios / totalStudents * 100));
            statistics.put("awardCoverage", Math.round((float) studentsWithAwards / totalStudents * 100));
            statistics.put("researchCoverage", Math.round((float) studentsWithResearch / totalStudents * 100));
        } else {
            statistics.put("achievementCoverage", 0);
            statistics.put("portfolioCoverage", 0);
            statistics.put("awardCoverage", 0);
            statistics.put("researchCoverage", 0);
        }
        
        return statistics;
    }
} 