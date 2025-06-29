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
import com.csu.unicorp.entity.JobCategory;
import com.csu.unicorp.entity.JobCategoryRelation;
import com.csu.unicorp.entity.Organization;
import com.csu.unicorp.entity.User;
import com.csu.unicorp.mapper.JobCategoryMapper;
import com.csu.unicorp.mapper.JobCategoryRelationMapper;
import com.csu.unicorp.mapper.JobMapper;
import com.csu.unicorp.mapper.OrganizationMapper;
import com.csu.unicorp.mapper.UserMapper;
import com.csu.unicorp.service.JobService;
import com.csu.unicorp.vo.JobCategoryVO;
import com.csu.unicorp.vo.JobVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private final JobCategoryMapper jobCategoryMapper;
    private final JobCategoryRelationMapper jobCategoryRelationMapper;
    
    /**
     * 分页查询岗位列表
     */
    @Override
    public IPage<JobVO> pageJobs(int page, int size, String keyword) {
        Page<JobVO> pageParam = new Page<>(page, size);
        pageParam.setSearchCount(true);
        IPage<JobVO> jobList = jobMapper.pageJobs(pageParam, keyword);
        
        // 为每个岗位加载分类信息
        loadCategoriesForJobs(jobList.getRecords());
        
        log.info("pageParam: {}", jobList);
        return jobList;
    }
    
    /**
     * 分页查询岗位列表（增强版，支持多条件筛选）
     */
    @Override
    public IPage<JobVO> pageJobs(int page, int size, String keyword, String location, 
                                String jobType, String educationRequirement, 
                                Integer salaryMin, Integer salaryMax, String sortBy) {
        Page<JobVO> pageParam = new Page<>(page, size);
        pageParam.setSearchCount(true);
        IPage<JobVO> jobList = jobMapper.pageJobsWithFilters(
                pageParam, keyword, location, jobType, educationRequirement, salaryMin, salaryMax, sortBy);
        
        // 为每个岗位加载分类信息
        loadCategoriesForJobs(jobList.getRecords());
        
        log.info("pageParam with filters and sorting: {}, sortBy: {}", jobList, sortBy);
        return jobList;
    }
    
    /**
     * 根据分类ID分页查询岗位列表
     */
    @Override
    public IPage<JobVO> pageJobsByCategory(int page, int size, Integer categoryId) {
        Page<JobVO> pageParam = new Page<>(page, size);
        pageParam.setSearchCount(true);
        IPage<JobVO> jobList = jobMapper.pageJobsByCategory(pageParam, categoryId);
        
        // 为每个岗位加载分类信息
        loadCategoriesForJobs(jobList.getRecords());
        
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
        
        // 保存岗位分类关系
        if (!CollectionUtils.isEmpty(dto.getCategoryIds())) {
            updateJobCategories(job.getId(), dto.getCategoryIds());
        }
        
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
        
        // 加载分类信息
        jobVO.setCategories(getJobCategories(id));
        
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
        boolean updated = updateById(job);
        
        // 更新岗位分类关系
        if (!CollectionUtils.isEmpty(dto.getCategoryIds())) {
            updateJobCategories(id, dto.getCategoryIds());
        }
        
        return updated;
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
        
        // 删除岗位分类关系
        LambdaQueryWrapper<JobCategoryRelation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(JobCategoryRelation::getJobId, id);
        jobCategoryRelationMapper.delete(queryWrapper);
        
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
        
        // 加载分类信息
        vo.setCategories(getJobCategories(job.getId()));
        
        return vo;
    }
    
    /**
     * 获取岗位关联的分类列表
     */
    @Override
    public List<JobCategoryVO> getJobCategories(Integer jobId) {
        // 获取岗位关联的分类ID列表
        List<Integer> categoryIds = jobCategoryRelationMapper.selectCategoryIdsByJobId(jobId);
        if (categoryIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 查询分类详情
        List<JobCategory> categories = jobCategoryMapper.selectBatchIds(categoryIds);
        return categories.stream().map(this::convertToCategoryVO).collect(Collectors.toList());
    }
    
    /**
     * 更新岗位关联的分类
     */
    @Override
    @Transactional
    public boolean updateJobCategories(Integer jobId, List<Integer> categoryIds) {
        // 先删除现有关联
        LambdaQueryWrapper<JobCategoryRelation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(JobCategoryRelation::getJobId, jobId);
        jobCategoryRelationMapper.delete(queryWrapper);
        
        // 添加新的关联
        if (!CollectionUtils.isEmpty(categoryIds)) {
            for (Integer categoryId : categoryIds) {
                JobCategoryRelation relation = new JobCategoryRelation();
                relation.setJobId(jobId);
                relation.setCategoryId(categoryId);
                jobCategoryRelationMapper.insert(relation);
            }
        }
        
        return true;
    }
    
    /**
     * 为岗位列表加载分类信息
     */
    private void loadCategoriesForJobs(List<JobVO> jobs) {
        if (CollectionUtils.isEmpty(jobs)) {
            return;
        }
        
        // 获取所有岗位ID
        List<Integer> jobIds = jobs.stream().map(JobVO::getId).collect(Collectors.toList());
        
        // 批量查询岗位分类关系
        LambdaQueryWrapper<JobCategoryRelation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(JobCategoryRelation::getJobId, jobIds);
        List<JobCategoryRelation> relations = jobCategoryRelationMapper.selectList(queryWrapper);
        
        // 按岗位ID分组
        Map<Integer, List<Integer>> jobCategoryMap = relations.stream()
                .collect(Collectors.groupingBy(JobCategoryRelation::getJobId,
                        Collectors.mapping(JobCategoryRelation::getCategoryId, Collectors.toList())));
        
        // 查询所有相关的分类
        List<Integer> allCategoryIds = relations.stream()
                .map(JobCategoryRelation::getCategoryId)
                .distinct()
                .collect(Collectors.toList());
        
        if (!CollectionUtils.isEmpty(allCategoryIds)) {
            LambdaQueryWrapper<JobCategory> categoryQueryWrapper = new LambdaQueryWrapper<>();
            categoryQueryWrapper.in(JobCategory::getId, allCategoryIds);
            List<JobCategory> allCategories = jobCategoryMapper.selectList(categoryQueryWrapper);
            
            // 转换为VO并按ID映射
            Map<Integer, JobCategoryVO> categoryMap = allCategories.stream()
                    .map(this::convertToCategoryVO)
                    .collect(Collectors.toMap(JobCategoryVO::getId, category -> category));
            
            // 为每个岗位设置分类
            for (JobVO job : jobs) {
                List<Integer> categoryIds = jobCategoryMap.get(job.getId());
                if (!CollectionUtils.isEmpty(categoryIds)) {
                    List<JobCategoryVO> categories = new ArrayList<>();
                    for (Integer categoryId : categoryIds) {
                        JobCategoryVO category = categoryMap.get(categoryId);
                        if (category != null) {
                            categories.add(category);
                        }
                    }
                    job.setCategories(categories);
                } else {
                    job.setCategories(new ArrayList<>());
                }
            }
        }
    }
    
    /**
     * 将分类实体转换为VO
     */
    private JobCategoryVO convertToCategoryVO(JobCategory category) {
        JobCategoryVO vo = new JobCategoryVO();
        BeanUtils.copyProperties(category, vo);
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