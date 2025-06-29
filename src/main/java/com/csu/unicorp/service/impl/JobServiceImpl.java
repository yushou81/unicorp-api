package com.csu.unicorp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.csu.unicorp.common.constants.RoleConstants;
import com.csu.unicorp.common.exception.BusinessException;
import com.csu.unicorp.common.exception.ResourceNotFoundException;
import com.csu.unicorp.dto.JobCreationDTO;
import com.csu.unicorp.entity.Job;
import com.csu.unicorp.entity.Organization;
import com.csu.unicorp.entity.User;
import com.csu.unicorp.mapper.JobMapper;
import com.csu.unicorp.mapper.OrganizationMapper;
import com.csu.unicorp.mapper.UserMapper;
import com.csu.unicorp.service.JobService;
import com.csu.unicorp.vo.JobVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 岗位服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class JobServiceImpl extends ServiceImpl<JobMapper, Job> implements JobService {
    
    private final JobMapper jobMapper;
    private final UserMapper userMapper;
    private final OrganizationMapper organizationMapper;
    
    /**
     * 分页查询岗位列表
     */
    @Override
    public IPage<JobVO> pageJobs(int page, int size, String keyword) {
        Page<JobVO> pageParam = new Page<>(page, size);
        pageParam.setSearchCount(true);
        IPage<JobVO> jobList = jobMapper.pageJobs(pageParam, keyword);
        log.info("pageParam: {}", jobList);
        return jobList;
    }
    
    /**
     * 创建新岗位
     */
    @Override
    @Transactional
    public Integer createJob(JobCreationDTO dto, Integer userId, Integer orgId) {
        Job job = new Job();
        BeanUtils.copyProperties(dto, job);
        
        job.setOrganizationId(orgId);
        job.setPostedByUserId(userId);
        job.setStatus("open");
        job.setCreatedAt(LocalDateTime.now());
        job.setIsDeleted(false);
        job.setViewCount(0);
        
        save(job);
        return job.getId();
    }
    
    /**
     * 根据ID获取岗位详情
     */
    @Override
    public JobVO getJobDetail(Integer id) {
        JobVO jobVO = jobMapper.getJobDetailById(id);
        if (jobVO == null) {
            throw new ResourceNotFoundException("岗位不存在或已被删除");
        }
        
        // 增加浏览量
        jobMapper.incrementViewCount(id);
        
        return jobVO;
    }
    
    /**
     * 更新岗位信息
     */
    @Override
    @Transactional
    public boolean updateJob(Integer id, JobCreationDTO dto, Integer userId, Integer orgId) {
        // 检查岗位是否存在且属于当前企业
        Job job = getById(id);
        if (job == null || job.getIsDeleted()) {
            throw new ResourceNotFoundException("岗位不存在或已被删除");
        }
        
        if (!job.getOrganizationId().equals(orgId)) {
            throw new BusinessException("无权限更新此岗位");
        }
        
        BeanUtils.copyProperties(dto, job);
        return updateById(job);
    }
    
    /**
     * 删除岗位
     */
    @Override
    @Transactional
    public boolean deleteJob(Integer id, Integer userId, Integer orgId) {
        // 检查岗位是否存在且属于当前企业
        Job job = getById(id);
        if (job == null || job.getIsDeleted()) {
            throw new ResourceNotFoundException("岗位不存在或已被删除");
        }
        
        if (!job.getOrganizationId().equals(orgId)) {
            throw new BusinessException("无权限删除此岗位");
        }
        
        return removeById(id);
    }
    
    /**
     * 检查用户是否有权限操作岗位
     */
    @Override
    public boolean hasJobPermission(Job job, UserDetails userDetails) {
        // 系统管理员有所有权限
        if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_" + RoleConstants.ROLE_SYSTEM_ADMIN))) {
            return true;
        }
        
        // 获取当前登录用户
        User currentUser = getUserByUsername(userDetails.getUsername());
        
        // 岗位发布者可以操作
        if (job.getPostedByUserId().equals(currentUser.getId())) {
            return true;
        }
        
        // 同一企业的企业管理员可以操作
        if (currentUser.getOrganizationId() != null 
                && currentUser.getOrganizationId().equals(job.getOrganizationId())
                && userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_" + RoleConstants.ROLE_ENTERPRISE_ADMIN))) {
            return true;
        }
        
        return false;
    }
    
    /**
     * 将岗位实体转换为VO
     */
    @Override
    public JobVO convertToVO(Job job) {
        JobVO vo = new JobVO();
        BeanUtils.copyProperties(job, vo);
        
        // 获取组织名称
        if (job.getOrganizationId() != null) {
            Organization organization = organizationMapper.selectById(job.getOrganizationId());
            if (organization != null) {
                vo.setOrganizationName(organization.getOrganizationName());
            }
        }
        
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