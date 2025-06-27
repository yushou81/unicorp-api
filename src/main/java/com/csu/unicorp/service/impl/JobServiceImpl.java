package com.csu.unicorp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.unicorp.common.exception.BusinessException;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 岗位服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class JobServiceImpl implements JobService {
    
    private final JobMapper jobMapper;
    private final UserMapper userMapper;
    private final OrganizationMapper organizationMapper;
    
    /**
     * 分页查询岗位列表
     */
    @Override
    public IPage<JobVO> getJobList(int page, int size, String keyword) {
        Page<Job> pageParam = new Page<>(page, size);
        IPage<Job> jobPage = jobMapper.selectJobsWithOrgName(pageParam, keyword);
        
        // 获取正确的总记录数
        Long total = jobMapper.countJobs(keyword);
        jobPage.setTotal(total);
        
        // 重新计算总页数
        long pages = total % size == 0 ? total / size : total / size + 1;
        ((Page<Job>)jobPage).setPages(pages);
        
        // 转换为VO
        return jobPage.convert(this::convertToVO);
    }
    
    /**
     * 创建新岗位
     */
    @Override
    @Transactional
    public JobVO createJob(JobCreationDTO jobCreationDTO, UserDetails userDetails) {
        // 获取当前登录用户
        User currentUser = getUserByUsername(userDetails.getUsername());
        
        // 检查用户是否有企业关联
        if (currentUser.getOrganizationId() == null) {
            throw new BusinessException("当前用户未关联任何企业，无法发布岗位");
        }
        
        // 检查用户是否有权限发布岗位（企业管理员或企业导师）
        boolean hasPermission = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_EN_ADMIN") || a.getAuthority().equals("ROLE_EN_TEACHER"));
        
        if (!hasPermission) {
            throw new BusinessException("当前用户无权发布岗位");
        }
        
        // 创建岗位实体
        Job job = new Job();
        BeanUtils.copyProperties(jobCreationDTO, job);
        job.setOrganizationId(currentUser.getOrganizationId());
        job.setPostedByUserId(currentUser.getId());
        job.setStatus("open");
        job.setCreatedAt(LocalDateTime.now());
        
        // 保存岗位
        jobMapper.insert(job);
        
        return convertToVO(job);
    }
    
    /**
     * 根据ID获取岗位详情
     */
    @Override
    public JobVO getJobById(Integer id) {
        Job job = jobMapper.selectById(id);
        if (job == null || job.getIsDeleted()) {
            throw new BusinessException("岗位不存在");
        }
        
        return convertToVO(job);
    }
    
    /**
     * 更新岗位信息
     */
    @Override
    @Transactional
    public JobVO updateJob(Integer id, JobCreationDTO jobCreationDTO, UserDetails userDetails) {
        // 获取岗位
        Job job = jobMapper.selectById(id);
        if (job == null || job.getIsDeleted()) {
            throw new BusinessException("岗位不存在");
        }
        
        // 检查权限
        if (!hasJobPermission(job, userDetails)) {
            throw new BusinessException("无权操作此岗位");
        }
        
        // 更新岗位信息
        BeanUtils.copyProperties(jobCreationDTO, job);
        jobMapper.updateById(job);
        
        return convertToVO(job);
    }
    
    /**
     * 删除岗位
     */
    @Override
    @Transactional
    public void deleteJob(Integer id, UserDetails userDetails) {
        // 获取岗位
        Job job = jobMapper.selectById(id);
        if (job == null || job.getIsDeleted()) {
            throw new BusinessException("岗位不存在");
        }
        
        // 检查权限
        if (!hasJobPermission(job, userDetails)) {
            throw new BusinessException("无权操作此岗位");
        }
        
        // 逻辑删除岗位
        jobMapper.deleteById(id);
    }
    
    /**
     * 检查用户是否有权限操作岗位
     */
    @Override
    public boolean hasJobPermission(Job job, UserDetails userDetails) {
        // 系统管理员有所有权限
        if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_SYSADMIN"))) {
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
                && userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_EN_ADMIN"))) {
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