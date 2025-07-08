package com.csu.unicorp.service.impl.achievement;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.unicorp.common.exception.BusinessException;
import com.csu.unicorp.dto.achievement.AchievementVerifyDTO;
import com.csu.unicorp.dto.achievement.ResearchAchievementCreationDTO;
import com.csu.unicorp.entity.User;
import com.csu.unicorp.entity.UserVerification;
import com.csu.unicorp.entity.achievement.ResearchAchievement;
import com.csu.unicorp.mapper.UserMapper;
import com.csu.unicorp.mapper.UserVerificationMapper;
import com.csu.unicorp.mapper.achievement.ResearchAchievementMapper;
import com.csu.unicorp.service.FileService;
import com.csu.unicorp.service.ResearchAchievementService;
import com.csu.unicorp.vo.achievement.ResearchAchievementVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 科研成果Service实现类
 */
@Service
@RequiredArgsConstructor
public class ResearchAchievementServiceImpl implements ResearchAchievementService {

    private final ResearchAchievementMapper researchAchievementMapper;
    private final UserMapper userMapper;
    private final UserVerificationMapper userVerificationMapper;
    private final FileService fileService;

    @Override
    public List<ResearchAchievementVO> getResearchAchievements(Integer userId) {
        List<ResearchAchievement> achievements = researchAchievementMapper.selectByUserId(userId);
        return achievements.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ResearchAchievementVO> getResearchAchievementPage(Integer userId, Integer page, Integer size) {
        Page<ResearchAchievement> pageParam = new Page<>(page, size);
        Page<ResearchAchievement> achievementPage = researchAchievementMapper.selectPageByUserId(pageParam, userId);
        
        // 转换为VO
        Page<ResearchAchievementVO> voPage = new Page<>(achievementPage.getCurrent(), achievementPage.getSize(), achievementPage.getTotal());
        List<ResearchAchievementVO> voList = achievementPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);
        
        return voPage;
    }

    @Override
    public Page<ResearchAchievementVO> getPublicResearchAchievementPage(Integer page, Integer size) {
        Page<ResearchAchievement> pageParam = new Page<>(page, size);
        Page<ResearchAchievement> achievementPage = researchAchievementMapper.selectPublicPage(pageParam);
        
        // 转换为VO
        Page<ResearchAchievementVO> voPage = new Page<>(achievementPage.getCurrent(), achievementPage.getSize(), achievementPage.getTotal());
        List<ResearchAchievementVO> voList = achievementPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);
        
        return voPage;
    }

    @Override
    public Page<ResearchAchievementVO> getPublicResearchAchievementPageByType(String type, Integer page, Integer size) {
        Page<ResearchAchievement> pageParam = new Page<>(page, size);
        Page<ResearchAchievement> achievementPage = researchAchievementMapper.selectPublicPageByType(pageParam, type);
        
        // 转换为VO
        Page<ResearchAchievementVO> voPage = new Page<>(achievementPage.getCurrent(), achievementPage.getSize(), achievementPage.getTotal());
        List<ResearchAchievementVO> voList = achievementPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);
        
        return voPage;
    }

    @Override
    public Page<ResearchAchievementVO> getUnverifiedResearchAchievementPage(Integer organizationId, Integer page, Integer size) {
        Page<ResearchAchievement> pageParam = new Page<>(page, size);
        Page<ResearchAchievement> achievementPage = researchAchievementMapper.selectUnverifiedPageByOrganization(pageParam, organizationId);
        
        // 转换为VO
        Page<ResearchAchievementVO> voPage = new Page<>(achievementPage.getCurrent(), achievementPage.getSize(), achievementPage.getTotal());
        List<ResearchAchievementVO> voList = achievementPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);
        
        return voPage;
    }

    @Override
    public ResearchAchievementVO getResearchAchievementDetail(Integer id, String viewerIp) {
        ResearchAchievement achievement = researchAchievementMapper.selectById(id);
        if (achievement == null || achievement.getIsDeleted()) {
            throw new BusinessException("科研成果不存在");
        }
        
        // TODO: 记录访问记录，可以在这里添加访问统计逻辑
        
        return convertToVO(achievement);
    }

    @Override
    @Transactional
    public ResearchAchievementVO createResearchAchievement(Integer userId, ResearchAchievementCreationDTO dto, 
                                                        MultipartFile file, MultipartFile coverImage) {
        // 检查用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        ResearchAchievement achievement = new ResearchAchievement();
        BeanUtils.copyProperties(dto, achievement);
        
        achievement.setUserId(userId);
        achievement.setIsVerified(false);
        achievement.setCreatedAt(LocalDateTime.now());
        achievement.setUpdatedAt(LocalDateTime.now());
        achievement.setIsDeleted(false);
        
        // 处理文件上传
        if (file != null && !file.isEmpty()) {
            String fileUrl = fileService.uploadFile(file, "resources");
            achievement.setFileUrl(fileUrl);
        }
        
        // 处理封面图片上传
        if (coverImage != null && !coverImage.isEmpty()) {
            String coverImageUrl = fileService.uploadFile(coverImage, "resources");
            achievement.setCoverImageUrl(coverImageUrl);
        }
        
        researchAchievementMapper.insert(achievement);
        
        return convertToVO(achievement);
    }

    @Override
    @Transactional
    public ResearchAchievementVO updateResearchAchievement(Integer id, Integer userId, ResearchAchievementCreationDTO dto,
                                                        MultipartFile file, MultipartFile coverImage) {
        ResearchAchievement achievement = researchAchievementMapper.selectById(id);
        if (achievement == null || achievement.getIsDeleted()) {
            throw new BusinessException("科研成果不存在");
        }
        
        // 检查权限
        if (!Objects.equals(achievement.getUserId(), userId)) {
            throw new BusinessException("无权限修改此科研成果");
        }
        
        // 如果已认证，不允许修改
        if (achievement.getIsVerified()) {
            throw new BusinessException("已认证的科研成果不允许修改");
        }
        
        BeanUtils.copyProperties(dto, achievement);
        
        // 处理文件上传
        if (file != null && !file.isEmpty()) {
            // 如果已有文件，先删除旧文件
            if (achievement.getFileUrl() != null && !achievement.getFileUrl().isEmpty()) {
                fileService.deleteFile(achievement.getFileUrl());
            }
            
            String fileUrl = fileService.uploadFile(file, "resources");
            achievement.setFileUrl(fileUrl);
        }
        
        // 处理封面图片上传
        if (coverImage != null && !coverImage.isEmpty()) {
            // 如果已有封面图片，先删除旧图片
            if (achievement.getCoverImageUrl() != null && !achievement.getCoverImageUrl().isEmpty()) {
                fileService.deleteFile(achievement.getCoverImageUrl());
            }
            
            String coverImageUrl = fileService.uploadFile(coverImage, "resources");
            achievement.setCoverImageUrl(coverImageUrl);
        }
        
        achievement.setUpdatedAt(LocalDateTime.now());
        
        researchAchievementMapper.updateById(achievement);
        
        return convertToVO(achievement);
    }

    @Override
    @Transactional
    public boolean deleteResearchAchievement(Integer id, Integer userId) {
        ResearchAchievement achievement = researchAchievementMapper.selectById(id);
        if (achievement == null || achievement.getIsDeleted()) {
            throw new BusinessException("科研成果不存在");
        }
        
        // 检查权限
        if (!Objects.equals(achievement.getUserId(), userId)) {
            throw new BusinessException("无权限删除此科研成果");
        }
        
        // 使用逻辑删除
        return researchAchievementMapper.deleteById(id) > 0;
    }

    @Override
    @Transactional
    public ResearchAchievementVO verifyResearchAchievement(Integer id, Integer verifierId, AchievementVerifyDTO verifyDTO) {
        ResearchAchievement achievement = researchAchievementMapper.selectById(id);
        if (achievement == null || achievement.getIsDeleted()) {
            throw new BusinessException("科研成果不存在");
        }
        
        // 获取认证人信息
        User verifier = userMapper.selectById(verifierId);
        if (verifier == null) {
            throw new BusinessException("认证人不存在");
        }
        
        // 获取认证人的实名信息
        UserVerification verifierVerification = userVerificationMapper.selectById(verifierId);
        String verifierName = verifierVerification != null ? verifierVerification.getRealName() : verifier.getNickname();
        
        // 更新认证状态
        achievement.setIsVerified(verifyDTO.getIsVerified());
        achievement.setVerifierId(verifierId);
        achievement.setVerifierName(verifierName);
        achievement.setVerifyDate(LocalDateTime.now());
        achievement.setUpdatedAt(LocalDateTime.now());
        
        researchAchievementMapper.updateById(achievement);
        
        return convertToVO(achievement);
    }
    
    @Override
    public Page<ResearchAchievementVO> getSchoolStudentResearchAchievements(Integer userId, int page, int size) {
        // 获取当前教师或管理员所属的组织ID
        User currentUser = userMapper.selectById(userId);
        if (currentUser == null || currentUser.getOrganizationId() == null) {
            throw new BusinessException("用户不存在或未关联组织");
        }
        
        Integer organizationId = currentUser.getOrganizationId();
        
        // 查询该组织下的所有学生科研成果
        Page<ResearchAchievement> pageParam = new Page<>(page, size);
        Page<ResearchAchievement> achievementPage = researchAchievementMapper.selectByOrganizationId(pageParam, organizationId);
        
        // 转换为VO
        Page<ResearchAchievementVO> voPage = new Page<>(achievementPage.getCurrent(), achievementPage.getSize(), achievementPage.getTotal());
        List<ResearchAchievementVO> voList = achievementPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);
        
        return voPage;
    }
    
    @Override
    public Page<ResearchAchievementVO> getSchoolStudentResearchAchievementsByType(Integer userId, String type, int page, int size) {
        // 获取当前教师或管理员所属的组织ID
        User currentUser = userMapper.selectById(userId);
        if (currentUser == null || currentUser.getOrganizationId() == null) {
            throw new BusinessException("用户不存在或未关联组织");
        }
        
        Integer organizationId = currentUser.getOrganizationId();
        
        // 查询该组织下的指定类型的学生科研成果
        Page<ResearchAchievement> pageParam = new Page<>(page, size);
        Page<ResearchAchievement> achievementPage = researchAchievementMapper.selectByOrganizationIdAndType(pageParam, organizationId, type);
        
        // 转换为VO
        Page<ResearchAchievementVO> voPage = new Page<>(achievementPage.getCurrent(), achievementPage.getSize(), achievementPage.getTotal());
        List<ResearchAchievementVO> voList = achievementPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);
        
        return voPage;
    }
    
    @Override
    public List<ResearchAchievementVO> getSchoolStudentResearchAchievementsByStudent(Integer userId, Integer studentId) {
        // 获取当前教师或管理员所属的组织ID
        User currentUser = userMapper.selectById(userId);
        if (currentUser == null || currentUser.getOrganizationId() == null) {
            throw new BusinessException("用户不存在或未关联组织");
        }
        
        Integer organizationId = currentUser.getOrganizationId();
        
        // 验证学生是否属于该组织
        User student = userMapper.selectById(studentId);
        if (student == null) {
            throw new BusinessException("学生不存在");
        }
        
        if (!organizationId.equals(student.getOrganizationId())) {
            throw new BusinessException("无权访问该学生的科研成果");
        }
        
        // 获取学生的科研成果列表
        List<ResearchAchievement> achievements = researchAchievementMapper.selectByUserId(studentId);
        
        return achievements.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }
    
    @Override
    public Map<String, Object> getSchoolResearchStatistics(Integer userId) {
        // 获取当前教师或管理员所属的组织ID
        User currentUser = userMapper.selectById(userId);
        if (currentUser == null || currentUser.getOrganizationId() == null) {
            throw new BusinessException("用户不存在或未关联组织");
        }
        
        Integer organizationId = currentUser.getOrganizationId();
        String organizationName = userMapper.selectOrganizationNameById(organizationId);
        
        // 获取该组织下的所有学生
        List<User> students = userMapper.selectList(null).stream()
                .filter(user -> organizationId.equals(user.getOrganizationId()))
                .filter(user -> userMapper.hasRole(user.getId(), "STUDENT"))
                .toList();
        
        // 获取该组织下的所有科研成果
        List<ResearchAchievement> allAchievements = new ArrayList<>();
        for (User student : students) {
            allAchievements.addAll(researchAchievementMapper.selectByUserId(student.getId()));
        }
        
        // 统计数据
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("organizationId", organizationId);
        statistics.put("organizationName", organizationName);
        statistics.put("totalResearchAchievements", allAchievements.size());
        
        // 统计已认证科研成果数量
        long verifiedAchievementsCount = allAchievements.stream()
                .filter(ResearchAchievement::getIsVerified)
                .count();
        statistics.put("verifiedAchievementsCount", verifiedAchievementsCount);
        
        // 统计科研成果类型分布
        Map<String, Long> typeDistribution = allAchievements.stream()
                .filter(achievement -> achievement.getType() != null && !achievement.getType().isEmpty())
                .collect(Collectors.groupingBy(
                        ResearchAchievement::getType,
                        Collectors.counting()
                ));
        statistics.put("typeDistribution", typeDistribution);
        
        // 统计有科研成果的学生数量
        long studentsWithResearch = students.stream()
                .filter(student -> !researchAchievementMapper.selectByUserId(student.getId()).isEmpty())
                .count();
        statistics.put("studentsWithResearch", studentsWithResearch);
        
        // 计算平均每个学生的科研成果数量
        double avgResearchPerStudent = students.isEmpty() ? 0 : (double) allAchievements.size() / students.size();
        statistics.put("avgResearchPerStudent", Math.round(avgResearchPerStudent * 10) / 10.0);
        
        // 计算科研成果覆盖率（有科研成果的学生占总学生的比例）
        double researchCoverage = students.isEmpty() ? 0 : (double) studentsWithResearch / students.size() * 100;
        statistics.put("researchCoverage", Math.round(researchCoverage));
        
        return statistics;
    }
    
    @Override
    @Transactional
    public ResearchAchievementVO updateResearchAchievementCover(Integer id, Integer userId, MultipartFile coverImage) {
        // 验证科研成果是否存在
        ResearchAchievement achievement = researchAchievementMapper.selectById(id);
        if (achievement == null || achievement.getIsDeleted()) {
            throw new BusinessException("科研成果不存在");
        }
        
        // 验证权限
        if (!Objects.equals(achievement.getUserId(), userId)) {
            throw new BusinessException("无权限修改此科研成果");
        }
        
        // 如果已认证，不允许修改
        if (achievement.getIsVerified()) {
            throw new BusinessException("已认证的科研成果不允许修改");
        }
        
        // 验证封面图片
        if (coverImage == null || coverImage.isEmpty()) {
            throw new BusinessException("封面图片不能为空");
        }
        
        // 如果已有封面图片，先删除旧图片
        if (achievement.getCoverImageUrl() != null && !achievement.getCoverImageUrl().isEmpty()) {
            fileService.deleteFile(achievement.getCoverImageUrl());
        }
        
        // 上传新的封面图片
        String coverImageUrl = fileService.uploadFile(coverImage, "resources");
        achievement.setCoverImageUrl(coverImageUrl);
        
        // 更新时间
        achievement.setUpdatedAt(LocalDateTime.now());
        
        // 更新数据库
        researchAchievementMapper.updateById(achievement);
        
        // 返回更新后的VO
        return convertToVO(achievement);
    }
    
    /**
     * 将科研成果实体转换为VO
     *
     * @param achievement 科研成果实体
     * @return 科研成果VO
     */
    private ResearchAchievementVO convertToVO(ResearchAchievement achievement) {
        if (achievement == null) {
            return null;
        }
        
        ResearchAchievementVO vo = new ResearchAchievementVO();
        BeanUtils.copyProperties(achievement, vo);
        
        // 设置学生姓名
        User user = userMapper.selectById(achievement.getUserId());
        if (user != null) {
            vo.setUserName(user.getNickname());
            
            // 设置组织名称
            if (user.getOrganizationId() != null) {
                vo.setOrganizationName(userMapper.selectOrganizationNameById(user.getOrganizationId()));
            }
        }
        
        // 处理文件URL
        if (achievement.getFileUrl() != null && !achievement.getFileUrl().isEmpty()) {
            vo.setFileUrl(fileService.getFullFileUrl(achievement.getFileUrl()));
        }
        
        // 处理封面图片URL
        if (achievement.getCoverImageUrl() != null && !achievement.getCoverImageUrl().isEmpty()) {
            vo.setCoverImageUrl(fileService.getFullFileUrl(achievement.getCoverImageUrl()));
        }
        
        return vo;
    }
} 