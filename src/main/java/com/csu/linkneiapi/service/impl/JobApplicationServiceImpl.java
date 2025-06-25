package com.csu.linkneiapi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.csu.linkneiapi.common.exception.BusinessException;
import com.csu.linkneiapi.dto.JobApplicationDTO;
import com.csu.linkneiapi.entity.Enterprise;
import com.csu.linkneiapi.entity.JobApplication;
import com.csu.linkneiapi.entity.JobPost;
import com.csu.linkneiapi.entity.User;
import com.csu.linkneiapi.mapper.JobApplicationMapper;
import com.csu.linkneiapi.mapper.JobPostMapper;
import com.csu.linkneiapi.mapper.EnterpriseMapper;
import com.csu.linkneiapi.service.JobApplicationService;
import com.csu.linkneiapi.vo.JobApplicationVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 投递记录服务实现类
 */
@Service
public class JobApplicationServiceImpl extends ServiceImpl<JobApplicationMapper, JobApplication> implements JobApplicationService {

    @Autowired
    private JobPostMapper jobPostMapper;
    
    @Autowired
    private EnterpriseMapper enterpriseMapper;

    /**
     * 状态文本映射
     */
    private static final Map<String, String> STATUS_TEXT_MAP = new HashMap<>();
    
    static {
        STATUS_TEXT_MAP.put("SUBMITTED", "已投递");
        STATUS_TEXT_MAP.put("VIEWED", "已查看");
        STATUS_TEXT_MAP.put("INTERVIEWING", "面试中");
        STATUS_TEXT_MAP.put("OFFERED", "已录用");
        STATUS_TEXT_MAP.put("REJECTED", "不合适");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean applyJob(Long userId, JobApplicationDTO applicationDTO) {
        // 检查岗位是否存在
        JobPost jobPost = jobPostMapper.selectById(applicationDTO.getJobPostId());
        if (jobPost == null || !"HIRING".equals(jobPost.getStatus())) {
            throw new BusinessException("岗位不存在或已关闭", 400);
        }
        
        // 检查是否已经投递过
        JobApplication existingApplication = baseMapper.findByUserIdAndJobPostId(userId, applicationDTO.getJobPostId());
        if (existingApplication != null) {
            throw new BusinessException("您已经投递过该岗位", 400);
        }
        
        // 创建投递记录
        JobApplication application = new JobApplication();
        application.setUserId(userId);
        application.setJobPostId(applicationDTO.getJobPostId());
        application.setStatus("SUBMITTED");
        application.setApplicationTime(LocalDateTime.now());
        
        return this.save(application);
    }

    @Override
    public List<JobApplicationVO> getUserApplications(Long userId) {
        List<JobApplication> applications = baseMapper.findByUserId(userId);
        if (applications.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 获取岗位信息
        List<Long> jobPostIds = applications.stream()
                .map(JobApplication::getJobPostId)
                .collect(Collectors.toList());
        
        List<JobPost> jobPosts = jobPostMapper.selectBatchIds(jobPostIds);
        Map<Long, JobPost> jobPostMap = jobPosts.stream()
                .collect(Collectors.toMap(JobPost::getId, jobPost -> jobPost));
        
        // 获取企业信息
        List<Long> enterpriseIds = jobPosts.stream()
                .map(JobPost::getEnterpriseId)
                .collect(Collectors.toList());
        
        List<Enterprise> enterprises = enterpriseMapper.selectBatchIds(enterpriseIds);
        Map<Long, Enterprise> enterpriseMap = enterprises.stream()
                .collect(Collectors.toMap(Enterprise::getId, enterprise -> enterprise));
        
        // 组装VO
        return applications.stream().map(app -> {
            JobApplicationVO vo = new JobApplicationVO();
            BeanUtils.copyProperties(app, vo);
            
            JobPost jobPost = jobPostMap.get(app.getJobPostId());
            if (jobPost != null) {
                vo.setJobTitle(jobPost.getTitle());
                vo.setSalaryRange(jobPost.getSalaryRange());
                vo.setLocation(jobPost.getLocation());
                
                Enterprise enterprise = enterpriseMap.get(jobPost.getEnterpriseId());
                if (enterprise != null) {
                    vo.setEnterpriseName(enterprise.getName());
                    vo.setEnterpriseLogo(enterprise.getLogoUrl());
                }
            }
            
            // 设置状态文本
            vo.setStatusText(STATUS_TEXT_MAP.getOrDefault(app.getStatus(), app.getStatus()));
            
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public IPage<JobApplicationVO> pageEnterpriseApplications(Long enterpriseId, String status, Page<JobApplication> page) {
        IPage<JobApplication> applicationPage = baseMapper.pageByEnterpriseId(page, enterpriseId, status);
        
        return applicationPage.convert(app -> {
            JobApplicationVO vo = new JobApplicationVO();
            BeanUtils.copyProperties(app, vo);
            
            // 获取岗位信息
            JobPost jobPost = jobPostMapper.selectById(app.getJobPostId());
            if (jobPost != null) {
                vo.setJobTitle(jobPost.getTitle());
                vo.setSalaryRange(jobPost.getSalaryRange());
                vo.setLocation(jobPost.getLocation());
                
                // 获取企业信息
                Enterprise enterprise = enterpriseMapper.selectById(jobPost.getEnterpriseId());
                if (enterprise != null) {
                    vo.setEnterpriseName(enterprise.getName());
                    vo.setEnterpriseLogo(enterprise.getLogoUrl());
                }
            }
            
            // 设置状态文本
            vo.setStatusText(STATUS_TEXT_MAP.getOrDefault(app.getStatus(), app.getStatus()));
            
            return vo;
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateApplicationStatus(Long applicationId, String status, Long operatorId) {
        // 检查状态是否合法
        if (!STATUS_TEXT_MAP.containsKey(status)) {
            throw new BusinessException("无效的状态值", 400);
        }
        
        JobApplication application = this.getById(applicationId);
        if (application == null) {
            throw new BusinessException("投递记录不存在", 404);
        }
        
        // 检查操作权限 (检查操作人是否为该企业成员，此处省略权限检查逻辑)
        
        // 更新状态
        application.setStatus(status);
        application.setUpdateTime(LocalDateTime.now());
        
        return this.updateById(application);
    }

    @Override
    public JobApplicationVO getApplicationDetail(Long applicationId) {
        JobApplication application = this.getById(applicationId);
        if (application == null) {
            throw new BusinessException("投递记录不存在", 404);
        }
        
        JobApplicationVO vo = new JobApplicationVO();
        BeanUtils.copyProperties(application, vo);
        
        // 获取岗位信息
        JobPost jobPost = jobPostMapper.selectById(application.getJobPostId());
        if (jobPost != null) {
            vo.setJobTitle(jobPost.getTitle());
            vo.setSalaryRange(jobPost.getSalaryRange());
            vo.setLocation(jobPost.getLocation());
            
            // 获取企业信息
            Enterprise enterprise = enterpriseMapper.selectById(jobPost.getEnterpriseId());
            if (enterprise != null) {
                vo.setEnterpriseName(enterprise.getName());
                vo.setEnterpriseLogo(enterprise.getLogoUrl());
            }
        }
        
        // 设置状态文本
        vo.setStatusText(STATUS_TEXT_MAP.getOrDefault(application.getStatus(), application.getStatus()));
        
        return vo;
    }
} 