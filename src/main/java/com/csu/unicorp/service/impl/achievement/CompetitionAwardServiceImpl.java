package com.csu.unicorp.service.impl.achievement;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.unicorp.common.exception.BusinessException;
import com.csu.unicorp.dto.achievement.AchievementVerifyDTO;
import com.csu.unicorp.dto.achievement.CompetitionAwardCreationDTO;
import com.csu.unicorp.entity.User;
import com.csu.unicorp.entity.UserVerification;
import com.csu.unicorp.entity.achievement.CompetitionAward;
import com.csu.unicorp.mapper.UserMapper;
import com.csu.unicorp.mapper.UserVerificationMapper;
import com.csu.unicorp.mapper.achievement.CompetitionAwardMapper;
import com.csu.unicorp.service.CompetitionAwardService;
import com.csu.unicorp.service.FileService;
import com.csu.unicorp.vo.achievement.CompetitionAwardVO;
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
 * 竞赛获奖Service实现类
 */
@Service
@RequiredArgsConstructor
public class CompetitionAwardServiceImpl implements CompetitionAwardService {

    private final CompetitionAwardMapper competitionAwardMapper;
    private final UserMapper userMapper;
    private final UserVerificationMapper userVerificationMapper;
    private final FileService fileService;

    @Override
    public List<CompetitionAwardVO> getCompetitionAwards(Integer userId) {
        List<CompetitionAward> awards = competitionAwardMapper.selectByUserId(userId);
        return awards.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<CompetitionAwardVO> getCompetitionAwardPage(Integer userId, Integer page, Integer size) {
        Page<CompetitionAward> pageParam = new Page<>(page, size);
        Page<CompetitionAward> awardPage = competitionAwardMapper.selectPageByUserId(pageParam, userId);
        
        // 转换为VO
        Page<CompetitionAwardVO> voPage = new Page<>(awardPage.getCurrent(), awardPage.getSize(), awardPage.getTotal());
        List<CompetitionAwardVO> voList = awardPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);
        
        return voPage;
    }

    @Override
    public Page<CompetitionAwardVO> getPublicCompetitionAwardPage(Integer page, Integer size) {
        Page<CompetitionAward> pageParam = new Page<>(page, size);
        Page<CompetitionAward> awardPage = competitionAwardMapper.selectPublicPage(pageParam);
        
        // 转换为VO
        Page<CompetitionAwardVO> voPage = new Page<>(awardPage.getCurrent(), awardPage.getSize(), awardPage.getTotal());
        List<CompetitionAwardVO> voList = awardPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);
        
        return voPage;
    }

    @Override
    public Page<CompetitionAwardVO> getUnverifiedCompetitionAwardPage(Integer organizationId, Integer currentUserId, Integer page, Integer size) {
        // 验证当前用户是否属于该组织
        User currentUser = userMapper.selectById(currentUserId);
        if (currentUser == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 检查用户是否属于请求的组织
        if (currentUser.getOrganizationId() == null || !currentUser.getOrganizationId().equals(organizationId)) {
            throw new BusinessException("无权访问该组织的数据");
        }
        
        Page<CompetitionAward> pageParam = new Page<>(page, size);
        Page<CompetitionAward> awardPage = competitionAwardMapper.selectUnverifiedPageByOrganization(pageParam, organizationId);
        
        // 转换为VO
        Page<CompetitionAwardVO> voPage = new Page<>(awardPage.getCurrent(), awardPage.getSize(), awardPage.getTotal());
        List<CompetitionAwardVO> voList = awardPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);
        
        return voPage;
    }

    @Override
    public CompetitionAwardVO getCompetitionAwardDetail(Integer id, String viewerIp) {
        CompetitionAward award = competitionAwardMapper.selectById(id);
        if (award == null || award.getIsDeleted()) {
            throw new BusinessException("获奖记录不存在");
        }
        
        // TODO: 记录访问记录，可以在这里添加访问统计逻辑
        
        return convertToVO(award);
    }

    @Override
    @Transactional
    public CompetitionAwardVO createCompetitionAward(Integer userId, CompetitionAwardCreationDTO dto) {
        // 检查用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        CompetitionAward award = new CompetitionAward();
        BeanUtils.copyProperties(dto, award);
        
        award.setUserId(userId);
        award.setIsVerified(false);
        award.setCreatedAt(LocalDateTime.now());
        award.setUpdatedAt(LocalDateTime.now());
        award.setIsDeleted(false);
        
        competitionAwardMapper.insert(award);
        
        return convertToVO(award);
    }

    @Override
    @Transactional
    public CompetitionAwardVO updateCompetitionAward(Integer id, Integer userId, CompetitionAwardCreationDTO dto) {
        CompetitionAward award = competitionAwardMapper.selectById(id);
        if (award == null || award.getIsDeleted()) {
            throw new BusinessException("获奖记录不存在");
        }
        
        // 检查权限
        if (!Objects.equals(award.getUserId(), userId)) {
            throw new BusinessException("无权限修改此获奖记录");
        }
        
        // 如果已认证，不允许修改
        if (award.getIsVerified()) {
            throw new BusinessException("已认证的获奖记录不允许修改");
        }
        
        BeanUtils.copyProperties(dto, award);
        award.setUpdatedAt(LocalDateTime.now());
        
        competitionAwardMapper.updateById(award);
        
        return convertToVO(award);
    }

    @Override
    @Transactional
    public boolean deleteCompetitionAward(Integer id, Integer userId) {
        CompetitionAward award = competitionAwardMapper.selectById(id);
        if (award == null || award.getIsDeleted()) {
            throw new BusinessException("获奖记录不存在");
        }
        
        // 检查权限
        if (!Objects.equals(award.getUserId(), userId)) {
            throw new BusinessException("无权限删除此获奖记录");
        }
        
        // 使用逻辑删除
        return competitionAwardMapper.deleteById(id) > 0;
    }

    @Override
    @Transactional
    public CompetitionAwardVO uploadCertificate(Integer id, Integer userId, MultipartFile file) {
        CompetitionAward award = competitionAwardMapper.selectById(id);
        if (award == null || award.getIsDeleted()) {
            throw new BusinessException("获奖记录不存在");
        }
        
        // 检查权限
        if (!Objects.equals(award.getUserId(), userId)) {
            throw new BusinessException("无权限上传此获奖证书");
        }
        
        // 如果已有证书，先删除旧证书
        if (award.getCertificateUrl() != null && !award.getCertificateUrl().isEmpty()) {
            fileService.deleteFile(award.getCertificateUrl());
        }
        
        // 上传新证书
        String certificateUrl = fileService.uploadFile(file, "resources");
        award.setCertificateUrl(certificateUrl);
        award.setUpdatedAt(LocalDateTime.now());
        
        competitionAwardMapper.updateById(award);
        
        return convertToVO(award);
    }

    @Override
    @Transactional
    public CompetitionAwardVO verifyCompetitionAward(Integer id, Integer verifierId, AchievementVerifyDTO verifyDTO) {
        CompetitionAward award = competitionAwardMapper.selectById(id);
        if (award == null || award.getIsDeleted()) {
            throw new BusinessException("获奖记录不存在");
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
        award.setIsVerified(verifyDTO.getIsVerified());
        award.setVerifierId(verifierId);
        award.setVerifierName(verifierName);
        award.setVerifyDate(LocalDateTime.now());
        award.setUpdatedAt(LocalDateTime.now());
        
        competitionAwardMapper.updateById(award);
        
        return convertToVO(award);
    }

    @Override
    public Page<CompetitionAwardVO> getSchoolStudentAwards(Integer userId, int page, int size) {
        // 获取当前教师或管理员所属的组织ID
        User currentUser = userMapper.selectById(userId);
        if (currentUser == null || currentUser.getOrganizationId() == null) {
            throw new BusinessException("用户不存在或未关联组织");
        }
        
        Integer organizationId = currentUser.getOrganizationId();
        
        // 查询该组织下的所有学生获奖记录
        Page<CompetitionAward> pageParam = new Page<>(page, size);
        Page<CompetitionAward> awardPage = competitionAwardMapper.selectByOrganizationId(pageParam, organizationId);
        
        // 转换为VO
        Page<CompetitionAwardVO> voPage = new Page<>(awardPage.getCurrent(), awardPage.getSize(), awardPage.getTotal());
        List<CompetitionAwardVO> voList = awardPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);
        
        return voPage;
    }
    
    @Override
    public Map<String, Object> getSchoolAwardStatistics(Integer userId) {
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
        
        // 获取该组织下的所有获奖记录
        List<CompetitionAward> allAwards = new ArrayList<>();
        for (User student : students) {
            allAwards.addAll(competitionAwardMapper.selectByUserId(student.getId()));
        }
        
        // 统计数据
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("organizationId", organizationId);
        statistics.put("organizationName", organizationName);
        statistics.put("totalAwards", allAwards.size());
        
        // 统计已认证获奖数量
        long verifiedAwardsCount = allAwards.stream()
                .filter(CompetitionAward::getIsVerified)
                .count();
        statistics.put("verifiedAwardsCount", verifiedAwardsCount);
        
        // 统计获奖等级分布
        Map<String, Long> awardLevelDistribution = allAwards.stream()
                .collect(Collectors.groupingBy(
                        CompetitionAward::getAwardLevel,
                        Collectors.counting()
                ));
        statistics.put("awardLevelDistribution", awardLevelDistribution);
        
        // 统计有获奖的学生数量
        long studentsWithAwards = students.stream()
                .filter(student -> !competitionAwardMapper.selectByUserId(student.getId()).isEmpty())
                .count();
        statistics.put("studentsWithAwards", studentsWithAwards);
        
        // 计算平均每个学生的获奖数量
        double avgAwardsPerStudent = students.isEmpty() ? 0 : (double) allAwards.size() / students.size();
        statistics.put("avgAwardsPerStudent", Math.round(avgAwardsPerStudent * 10) / 10.0);
        
        // 计算获奖覆盖率（有获奖的学生占总学生的比例）
        double awardCoverage = students.isEmpty() ? 0 : (double) studentsWithAwards / students.size() * 100;
        statistics.put("awardCoverage", Math.round(awardCoverage));
        
        return statistics;
    }
    
    @Override
    public List<CompetitionAwardVO> getSchoolStudentAwardsByStudent(Integer userId, Integer studentId) {
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
            throw new BusinessException("无权访问该学生的获奖记录");
        }
        
        // 获取学生的获奖记录
        List<CompetitionAward> awards = competitionAwardMapper.selectByUserId(studentId);
        
        return awards.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }
    
    /**
     * 将实体转换为VO
     *
     * @param award 竞赛获奖实体
     * @return 竞赛获奖VO
     */
    private CompetitionAwardVO convertToVO(CompetitionAward award) {
        if (award == null) {
            return null;
        }
        
        CompetitionAwardVO vo = new CompetitionAwardVO();
        BeanUtils.copyProperties(award, vo);
        
        // 设置学生姓名
        User user = userMapper.selectById(award.getUserId());
        if (user != null) {
            vo.setUserName(user.getNickname());
            
            // 设置组织名称
            if (user.getOrganizationId() != null) {
                vo.setOrganizationName(userMapper.selectOrganizationNameById(user.getOrganizationId()));
            }
        }
        
        // 处理证书URL
        if (award.getCertificateUrl() != null && !award.getCertificateUrl().isEmpty()) {
            vo.setCertificateUrl(fileService.getFullFileUrl(award.getCertificateUrl()));
        }
        
        return vo;
    }
} 