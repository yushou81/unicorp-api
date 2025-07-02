package com.csu.unicorp.service.impl.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.csu.unicorp.common.constants.RoleConstants;
import com.csu.unicorp.common.exception.BusinessException;
import com.csu.unicorp.common.exception.ResourceNotFoundException;
import com.csu.unicorp.dto.JobCreationDTO;
import com.csu.unicorp.entity.job.Job;
import com.csu.unicorp.entity.job.JobCategory;
import com.csu.unicorp.entity.job.JobCategoryRelation;
import com.csu.unicorp.entity.organization.Organization;
import com.csu.unicorp.entity.user.User;
import com.csu.unicorp.entity.organization.EnterpriseDetail;
import com.csu.unicorp.mapper.JobCategoryMapper;
import com.csu.unicorp.mapper.JobCategoryRelationMapper;
import com.csu.unicorp.mapper.JobMapper;
import com.csu.unicorp.mapper.OrganizationMapper;
import com.csu.unicorp.mapper.UserMapper;
import com.csu.unicorp.mapper.EnterpriseDetailMapper;
import com.csu.unicorp.service.JobService;
import com.csu.unicorp.vo.JobCategoryVO;
import com.csu.unicorp.vo.JobVO;
import com.csu.unicorp.vo.UserVO;
import com.csu.unicorp.vo.OrganizationVO;
import com.csu.unicorp.vo.EnterpriseDetailVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

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
    private final EnterpriseDetailMapper enterpriseDetailMapper;
    
    /**
     * 分页查询岗位列表
     */
    @Override
    public IPage<JobVO> pageJobs(int page, int size, String keyword) {
        Page<JobVO> pageParam = new Page<>(page, size);
        pageParam.setSearchCount(true);
        IPage<JobVO> jobList = jobMapper.pageJobs(pageParam, keyword);
        
        // 为每个岗位加载分类信息
        if (!CollectionUtils.isEmpty(jobList.getRecords())) {
            for (JobVO job : jobList.getRecords()) {
                // 获取岗位分类（三级分类）
                List<Integer> categoryIds = jobCategoryRelationMapper.selectCategoryIdsByJobId(job.getId());
                if (!CollectionUtils.isEmpty(categoryIds)) {
                    // 查询所有分类
                    List<JobCategory> allCategories = jobCategoryMapper.selectBatchIds(categoryIds);
                    // 找到三级分类
                    JobCategory jobCategory = allCategories.stream()
                            .filter(category -> category.getLevel() == 3)
                            .findFirst()
                            .orElse(null);
                    
                    if (jobCategory != null) {
                        job.setCategory(convertToCategoryVO(jobCategory));
                    }
                }
            }
        }
        
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
        if (!CollectionUtils.isEmpty(jobList.getRecords())) {
            for (JobVO job : jobList.getRecords()) {
                loadCategoryForJob(job);
            }
        }
        
        log.info("pageParam with filters and sorting: {}, sortBy: {}", jobList, sortBy);
        return jobList;
    }
    
    /**
     * 分页查询岗位列表（增强版，支持多条件筛选，包含组织ID和发布者ID筛选）
     */
    @Override
    public IPage<JobVO> pageJobs(int page, int size, String keyword, String location, 
                                String jobType, String educationRequirement, 
                                Integer salaryMin, Integer salaryMax, String sortBy,
                                Integer organizeId, Integer posterId) {
        Page<JobVO> pageParam = new Page<>(page, size);
        pageParam.setSearchCount(true);
        IPage<JobVO> jobList = jobMapper.pageJobsWithAdvancedFilters(
                pageParam, keyword, location, jobType, educationRequirement, 
                salaryMin, salaryMax, sortBy, organizeId, posterId);
        
        // 为每个岗位加载分类信息
        if (!CollectionUtils.isEmpty(jobList.getRecords())) {
            for (JobVO job : jobList.getRecords()) {
                loadCategoryForJob(job);
            }
        }
        
        log.info("pageParam with advanced filters: {}, organizeId: {}, posterId: {}", 
                jobList, organizeId, posterId);
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
        if (!CollectionUtils.isEmpty(jobList.getRecords())) {
            for (JobVO job : jobList.getRecords()) {
                // 获取岗位分类（三级分类）
                List<Integer> categoryIds = jobCategoryRelationMapper.selectCategoryIdsByJobId(job.getId());
                if (!CollectionUtils.isEmpty(categoryIds)) {
                    // 查询所有分类
                    List<JobCategory> allCategories = jobCategoryMapper.selectBatchIds(categoryIds);
                    // 找到三级分类
                    JobCategory jobCategory = allCategories.stream()
                            .filter(category -> category.getLevel() == 3)
                            .findFirst()
                            .orElse(null);
                    
                    if (jobCategory != null) {
                        job.setCategory(convertToCategoryVO(jobCategory));
                    }
                }
            }
        }
        
        return jobList;
    }
    
    /**
     * 创建新岗位
     */
    @Override
    @Transactional
    public Integer createJob(JobCreationDTO dto, Integer userId, Integer orgId) {
        // 验证分类是否为三级分类
        if (dto.getCategoryId() != null) {
            JobCategory category = jobCategoryMapper.selectById(dto.getCategoryId());
            if (category == null) {
                throw new BusinessException("所选岗位分类不存在");
            }
            if (category.getLevel() != 3) {
                throw new BusinessException("岗位分类必须是三级分类");
            }
        } else {
            throw new BusinessException("岗位分类不能为空");
        }

        // 验证学历要求值是否合法
        String educationRequirement = dto.getEducationRequirement();
        if (educationRequirement != null && !Arrays.asList("bachelor", "master", "doctorate", "any").contains(educationRequirement)) {
            throw new BusinessException("学历要求必须是以下值之一: bachelor, master, doctorate, any");
        }
        
        // 验证经验要求值是否合法
        String experienceRequirement = dto.getExperienceRequirement();
        if (experienceRequirement != null && !Arrays.asList("fresh_graduate", "less_than_1_year", "1_to_3_years", "any").contains(experienceRequirement)) {
            throw new BusinessException("经验要求必须是以下值之一: fresh_graduate, less_than_1_year, 1_to_3_years, any");
        }
        
        Job job = new Job();
        BeanUtils.copyProperties(dto, job);
        
        job.setOrganizationId(orgId);
        job.setPostedByUserId(userId);
        job.setStatus("open");
        job.setCreatedAt(LocalDateTime.now());
        job.setIsDeleted(false);
        job.setViewCount(0);
        
        save(job);
        
        // 保存岗位分类关系（只保存一个三级分类）
        JobCategoryRelation relation = new JobCategoryRelation();
        relation.setJobId(job.getId());
        relation.setCategoryId(dto.getCategoryId());
        jobCategoryRelationMapper.insert(relation);
        
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
        
        // 获取岗位分类（三级分类）
        // 查询关联的分类ID
        List<Integer> categoryIds = jobCategoryRelationMapper.selectCategoryIdsByJobId(id);
        if (!CollectionUtils.isEmpty(categoryIds)) {
            // 查询所有分类
            List<JobCategory> allCategories = jobCategoryMapper.selectBatchIds(categoryIds);
            // 找到三级分类
            JobCategory jobCategory = allCategories.stream()
                    .filter(category -> category.getLevel() == 3)
                    .findFirst()
                    .orElse(null);
            
            if (jobCategory != null) {
                jobVO.setCategory(convertToCategoryVO(jobCategory));
            }
        }
        
        // 加载发布者信息
        if (jobVO.getPostedByUserId() != null) {
            User postedUser = userMapper.selectById(jobVO.getPostedByUserId());
            if (postedUser != null) {
                UserVO userVO = new UserVO();
                userVO.setId(postedUser.getId());
                userVO.setAccount(postedUser.getAccount());
                userVO.setNickname(postedUser.getNickname());
                userVO.setEmail(postedUser.getEmail());
                userVO.setPhone(postedUser.getPhone());
                userVO.setStatus(postedUser.getStatus());
                userVO.setOrganizationId(postedUser.getOrganizationId());
                userVO.setCreatedAt(postedUser.getCreatedAt());
                // 获取用户角色
                String role = userMapper.selectRoleByUserId(postedUser.getId());
                userVO.setRole(role);
                
                jobVO.setPostedByUser(userVO);
            }
        }
        
        // 加载组织详情
        if (jobVO.getOrganizationId() != null) {
            // 获取组织信息
            Organization organization = organizationMapper.selectById(jobVO.getOrganizationId());
            if (organization != null) {
                OrganizationVO organizationVO = OrganizationVO.fromEntity(organization);
                jobVO.setOrganization(organizationVO);
                
                // 如果是企业类型，获取企业详情
                if ("Enterprise".equals(organization.getType())) {
                    EnterpriseDetail enterpriseDetail = enterpriseDetailMapper.selectById(organization.getId());
                    if (enterpriseDetail != null) {
                        EnterpriseDetailVO enterpriseDetailVO = EnterpriseDetailVO.fromEntity(enterpriseDetail);
                        jobVO.setEnterpriseDetail(enterpriseDetailVO);
                    }
                }
            }
        }
        
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
        
        // 验证分类是否为三级分类
        if (dto.getCategoryId() != null) {
            JobCategory category = jobCategoryMapper.selectById(dto.getCategoryId());
            if (category == null) {
                throw new BusinessException("所选岗位分类不存在");
            }
            if (category.getLevel() != 3) {
                throw new BusinessException("岗位分类必须是三级分类");
            }
        } else {
            throw new BusinessException("岗位分类不能为空");
        }

        // 验证学历要求值是否合法
        String educationRequirement = dto.getEducationRequirement();
        if (educationRequirement != null && !Arrays.asList("bachelor", "master", "doctorate", "any").contains(educationRequirement)) {
            throw new BusinessException("学历要求必须是以下值之一: bachelor, master, doctorate, any");
        }
        
        // 验证经验要求值是否合法
        String experienceRequirement = dto.getExperienceRequirement();
        if (experienceRequirement != null && !Arrays.asList("fresh_graduate", "less_than_1_year", "1_to_3_years", "any").contains(experienceRequirement)) {
            throw new BusinessException("经验要求必须是以下值之一: fresh_graduate, less_than_1_year, 1_to_3_years, any");
        }
        
        BeanUtils.copyProperties(dto, job);
        boolean updated = updateById(job);
        
        // 先删除现有关联
        LambdaQueryWrapper<JobCategoryRelation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(JobCategoryRelation::getJobId, id);
        jobCategoryRelationMapper.delete(queryWrapper);
        
        // 添加新的关联（只添加一个三级分类）
        JobCategoryRelation relation = new JobCategoryRelation();
        relation.setJobId(id);
        relation.setCategoryId(dto.getCategoryId());
        jobCategoryRelationMapper.insert(relation);
        
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
        
        // 获取岗位分类（三级分类）
        List<Integer> categoryIds = jobCategoryRelationMapper.selectCategoryIdsByJobId(job.getId());
        if (!CollectionUtils.isEmpty(categoryIds)) {
            // 查询所有分类
            List<JobCategory> allCategories = jobCategoryMapper.selectBatchIds(categoryIds);
            // 找到三级分类
            JobCategory jobCategory = allCategories.stream()
                    .filter(category -> category.getLevel() == 3)
                    .findFirst()
                    .orElse(null);
        
            if (jobCategory != null) {
                vo.setCategory(convertToCategoryVO(jobCategory));
            }
        }
        
        return vo;
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
    
    // 添加辅助方法，提取公共代码，减少重复
    private void loadCategoryForJob(JobVO job) {
        // 获取岗位分类（三级分类）
        List<Integer> categoryIds = jobCategoryRelationMapper.selectCategoryIdsByJobId(job.getId());
        if (!CollectionUtils.isEmpty(categoryIds)) {
            // 查询所有分类
            List<JobCategory> allCategories = jobCategoryMapper.selectBatchIds(categoryIds);
            // 找到三级分类
            JobCategory jobCategory = allCategories.stream()
                    .filter(category -> category.getLevel() == 3)
                    .findFirst()
                    .orElse(null);
            
            if (jobCategory != null) {
                job.setCategory(convertToCategoryVO(jobCategory));
            }
        }
    }
} 