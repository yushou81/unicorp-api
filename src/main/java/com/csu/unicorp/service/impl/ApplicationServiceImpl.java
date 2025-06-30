package com.csu.unicorp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.csu.unicorp.common.exception.BusinessException;
import com.csu.unicorp.common.exception.ResourceNotFoundException;
import com.csu.unicorp.dto.ApplicationStatusUpdateDTO;
import com.csu.unicorp.entity.Application;
import com.csu.unicorp.entity.Job;
import com.csu.unicorp.entity.Resume;
import com.csu.unicorp.entity.User;
import com.csu.unicorp.entity.UserVerification;
import com.csu.unicorp.mapper.ApplicationMapper;
import com.csu.unicorp.mapper.JobMapper;
import com.csu.unicorp.mapper.ResumeMapper;
import com.csu.unicorp.mapper.UserMapper;
import com.csu.unicorp.mapper.UserVerificationMapper;
import com.csu.unicorp.service.ApplicationService;
import com.csu.unicorp.vo.ApplicationDetailVO;
import com.csu.unicorp.vo.MyApplicationDetailVO;
import com.csu.unicorp.vo.ResumeVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * 岗位申请服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationServiceImpl extends ServiceImpl<ApplicationMapper, Application> implements ApplicationService {
    
    private final ApplicationMapper applicationMapper;
    private final JobMapper jobMapper;
    private final UserMapper userMapper;
    private final ResumeMapper resumeMapper;
    private final UserVerificationMapper userVerificationMapper;
    
    // 有效的申请状态列表
    private static final List<String> VALID_STATUS = Arrays.asList("viewed", "interviewing", "offered", "rejected");
    
    @Override
    @Transactional
    public Integer applyJob(Integer jobId, Integer studentId) {
        // 检查岗位是否存在
        Job job = jobMapper.selectById(jobId);
        if (job == null || job.getIsDeleted() || !"open".equals(job.getStatus())) {
            throw new ResourceNotFoundException("岗位不存在或已关闭");
        }
        
        // 检查是否已申请过该岗位
        int count = applicationMapper.countApplicationByJobIdAndStudentId(jobId, studentId);
        if (count > 0) {
            throw new BusinessException("您已申请过该岗位");
        }
        
        // 创建申请记录
        Application application = new Application();
        application.setJobId(jobId);
        application.setStudentId(studentId);
        application.setStatus("submitted");
        application.setAppliedAt(LocalDateTime.now());
        
        save(application);
        return application.getId();
    }
    
    @Override
    public IPage<ApplicationDetailVO> pageApplicationsByJobId(Integer jobId, int page, int size, Integer orgId) {
        // 检查岗位是否存在且属于当前企业
        Job job = jobMapper.selectById(jobId);
        if (job == null || job.getIsDeleted()) {
            throw new ResourceNotFoundException("岗位不存在或已被删除");
        }
        
        if (!job.getOrganizationId().equals(orgId)) {
            throw new BusinessException("无权限查看此岗位的申请");
        }
        
        Page<ApplicationDetailVO> pageParam = new Page<>(page, size);
        return applicationMapper.pageApplicationsByJobId(pageParam, jobId);
    }
    
    @Override
    @Transactional
    public ApplicationDetailVO updateApplicationStatus(Integer id, ApplicationStatusUpdateDTO dto, Integer userId, Integer orgId) {
        // 检查申请是否存在
        Application application = getById(id);
        if (application == null) {
            throw new ResourceNotFoundException("申请记录不存在");
        }
        
        // 检查状态值是否有效
        if (!VALID_STATUS.contains(dto.getStatus())) {
            throw new BusinessException("无效的状态值");
        }
        
        // 检查岗位是否属于当前企业
        Job job = jobMapper.selectById(application.getJobId());
        if (job == null || job.getIsDeleted()) {
            throw new ResourceNotFoundException("岗位不存在或已被删除");
        }
        
        if (!job.getOrganizationId().equals(orgId)) {
            throw new BusinessException("无权限更新此申请状态");
        }
        
        // 更新状态
        application.setStatus(dto.getStatus());
        updateById(application);
        
        // 返回更新后的申请详情
        return applicationMapper.getApplicationDetailById(id);
    }
    
    @Override
    public IPage<MyApplicationDetailVO> pageStudentApplications(Integer studentId, int page, int size) {
        Page<MyApplicationDetailVO> pageParam = new Page<>(page, size);
        return applicationMapper.pageApplicationsByStudentId(pageParam, studentId);
    }
    
    /**
     * 将申请实体转换为VO
     */
    private ApplicationDetailVO convertToVO(Application application) {
        ApplicationDetailVO vo = new ApplicationDetailVO();
        BeanUtils.copyProperties(application, vo);
        
        // 设置简历信息
        ResumeVO resumeVO = new ResumeVO();
        
        // 获取用户基本信息
        User student = userMapper.selectById(application.getStudentId());
        if (student != null) {
            // 获取简历信息
            Resume resume = resumeMapper.selectByUserId(application.getStudentId());
            if (resume != null) {
                resumeVO.setId(resume.getId());
                resumeVO.setMajor(resume.getMajor());
                resumeVO.setEducationLevel(resume.getEducationLevel());
                resumeVO.setResumeUrl(resume.getResumeUrl());
                resumeVO.setAchievements(resume.getAchievements());
            }
        }
        
        vo.setResume(resumeVO);
        return vo;
    }
    
    /**
     * 将申请实体转换为我的申请VO
     */
    private MyApplicationDetailVO convertToMyApplicationVO(Application application) {
        MyApplicationDetailVO vo = new MyApplicationDetailVO();
        vo.setApplicationId(application.getId());
        vo.setStatus(application.getStatus());
        vo.setAppliedAt(application.getAppliedAt());
        
        // 设置岗位信息
        MyApplicationDetailVO.JobInfoVO jobInfo = new MyApplicationDetailVO.JobInfoVO();
        jobInfo.setJobId(application.getJobId());
        
        // 获取岗位信息
        Job job = jobMapper.selectById(application.getJobId());
        if (job != null) {
            jobInfo.setJobTitle(job.getTitle());
            
            // 获取组织名称
            // 这里需要查询组织表获取名称，但为了简化，我们暂时不实现
            // jobInfo.setOrganizationName("组织名称");
        }
        
        vo.setJobInfo(jobInfo);
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