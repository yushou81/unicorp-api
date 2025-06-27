package com.csu.unicorp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.unicorp.common.exception.BusinessException;
import com.csu.unicorp.entity.Application;
import com.csu.unicorp.entity.Job;
import com.csu.unicorp.entity.StudentProfile;
import com.csu.unicorp.entity.User;
import com.csu.unicorp.entity.UserVerification;
import com.csu.unicorp.mapper.ApplicationMapper;
import com.csu.unicorp.mapper.JobMapper;
import com.csu.unicorp.mapper.StudentProfileMapper;
import com.csu.unicorp.mapper.UserMapper;
import com.csu.unicorp.mapper.UserVerificationMapper;
import com.csu.unicorp.service.ApplicationService;
import com.csu.unicorp.service.JobService;
import com.csu.unicorp.vo.ApplicationDetailVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 岗位申请服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationServiceImpl implements ApplicationService {
    
    private final ApplicationMapper applicationMapper;
    private final JobMapper jobMapper;
    private final UserMapper userMapper;
    private final StudentProfileMapper studentProfileMapper;
    private final UserVerificationMapper userVerificationMapper;
    private final JobService jobService;
    
    /**
     * 学生申请岗位
     */
    @Override
    @Transactional
    public Integer applyJob(Integer jobId, UserDetails userDetails) {
        // 获取当前登录用户
        User currentUser = getUserByUsername(userDetails.getUsername());
        
        // 检查用户是否是学生
        boolean isStudent = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"));
        
        if (!isStudent) {
            throw new BusinessException("只有学生用户才能申请岗位");
        }
        
        // 检查岗位是否存在且开放
        Job job = jobMapper.selectById(jobId);
        if (job == null || job.getIsDeleted()) {
            throw new BusinessException("岗位不存在");
        }
        
        if (!"open".equals(job.getStatus())) {
            throw new BusinessException("岗位已关闭，无法申请");
        }
        
        // 检查是否已申请过该岗位
        Integer count = applicationMapper.countStudentApplication(jobId, currentUser.getId());
        if (count > 0) {
            throw new BusinessException("您已申请过该岗位");
        }
        
        // 创建申请记录
        Application application = new Application();
        application.setJobId(jobId);
        application.setStudentId(currentUser.getId());
        application.setStatus("submitted");
        application.setAppliedAt(LocalDateTime.now());
        
        applicationMapper.insert(application);
        
        return application.getId();
    }
    
    /**
     * 获取岗位的申请列表
     */
    @Override
    public IPage<ApplicationDetailVO> getApplicationsByJobId(Integer jobId, int page, int size, UserDetails userDetails) {
        // 检查岗位是否存在
        Job job = jobMapper.selectById(jobId);
        if (job == null || job.getIsDeleted()) {
            throw new BusinessException("岗位不存在");
        }
        
        // 检查权限
        if (!hasPermissionToViewApplications(job, userDetails)) {
            throw new BusinessException("无权查看该岗位的申请列表");
        }
        
        // 查询申请列表
        Page<Application> pageParam = new Page<>(page, size);
        IPage<Application> applicationPage = applicationMapper.selectApplicationsWithStudentInfo(pageParam, jobId);
        
        // 转换为VO
        return applicationPage.convert(this::convertToVO);
    }
    
    /**
     * 检查用户是否有权限查看岗位申请
     */
    private boolean hasPermissionToViewApplications(Job job, UserDetails userDetails) {
        // 系统管理员有所有权限
        if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_SYSADMIN"))) {
            return true;
        }
        
        // 岗位所属企业的管理员和导师可以查看
        User currentUser = getUserByUsername(userDetails.getUsername());
        if (currentUser.getOrganizationId() != null 
                && currentUser.getOrganizationId().equals(job.getOrganizationId())
                && (userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_EN_ADMIN"))
                || userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_EN_TEACHER")))) {
            return true;
        }
        
        // 岗位发布者可以查看
        return job.getPostedByUserId().equals(currentUser.getId());
    }
    
    /**
     * 将申请实体转换为VO
     */
    private ApplicationDetailVO convertToVO(Application application) {
        ApplicationDetailVO vo = new ApplicationDetailVO();
        BeanUtils.copyProperties(application, vo);
        
        // 设置学生资料
        ApplicationDetailVO.StudentProfileVO profileVO = new ApplicationDetailVO.StudentProfileVO();
        
        // 获取用户基本信息
        User student = userMapper.selectById(application.getStudentId());
        if (student != null) {
            profileVO.setNickname(student.getNickname());
            
            // 获取实名认证信息
            UserVerification verification = userVerificationMapper.selectById(application.getStudentId());
            if (verification != null) {
                profileVO.setRealName(verification.getRealName());
            }
            
            // 获取学生档案信息
            StudentProfile profile = studentProfileMapper.selectByUserId(application.getStudentId());
            if (profile != null) {
                profileVO.setMajor(profile.getMajor());
                profileVO.setEducationLevel(profile.getEducationLevel());
                profileVO.setResumeUrl(profile.getResumeUrl());
            }
        }
        
        vo.setStudentProfile(profileVO);
        return vo;
    }
    
    /**
     * 根据用户名获取用户
     */
    private User getUserByUsername(String username) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getAccount, username);
        return userMapper.selectOne(queryWrapper);
    }
} 