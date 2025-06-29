package com.csu.unicorp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.csu.unicorp.dto.JobCreationDTO;
import com.csu.unicorp.entity.Job;
import com.csu.unicorp.vo.JobCategoryVO;
import com.csu.unicorp.vo.JobVO;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

/**
 * 岗位服务接口
 */
public interface JobService extends IService<Job> {
    
    /**
     * 分页查询岗位列表
     *
     * @param page    页码
     * @param size    每页大小
     * @param keyword 搜索关键词
     * @return 岗位列表分页结果
     */
    IPage<JobVO> pageJobs(int page, int size, String keyword);
    
    /**
     * 分页查询岗位列表（增强版）
     *
     * @param page                 页码
     * @param size                 每页大小
     * @param keyword              搜索关键词
     * @param location             工作地点
     * @param jobType              工作类型
     * @param educationRequirement 学历要求
     * @param salaryMin            最低薪资
     * @param salaryMax            最高薪资
     * @param sortBy               排序方式（latest-最新发布，salary_asc-薪资从低到高，salary_desc-薪资从高到低）
     * @return 岗位列表分页结果
     */
    IPage<JobVO> pageJobs(int page, int size, String keyword, String location, 
                          String jobType, String educationRequirement, 
                          Integer salaryMin, Integer salaryMax, String sortBy);
    
    /**
     * 根据分类ID分页查询岗位列表
     *
     * @param page       页码
     * @param size       每页大小
     * @param categoryId 分类ID
     * @return 岗位列表分页结果
     */
    IPage<JobVO> pageJobsByCategory(int page, int size, Integer categoryId);
    
    /**
     * 创建岗位
     *
     * @param dto      岗位创建DTO
     * @param userId   用户ID
     * @param orgId    组织ID
     * @return 创建的岗位ID
     */
    Integer createJob(JobCreationDTO dto, Integer userId, Integer orgId);
    
    /**
     * 根据ID查询岗位详情
     *
     * @param id 岗位ID
     * @return 岗位详情
     */
    JobVO getJobDetail(Integer id);
    
    /**
     * 更新岗位信息
     *
     * @param id       岗位ID
     * @param dto      岗位更新DTO
     * @param userId   用户ID
     * @param orgId    组织ID
     * @return 是否更新成功
     */
    boolean updateJob(Integer id, JobCreationDTO dto, Integer userId, Integer orgId);
    
    /**
     * 删除岗位
     *
     * @param id       岗位ID
     * @param userId   用户ID
     * @param orgId    组织ID
     * @return 是否删除成功
     */
    boolean deleteJob(Integer id, Integer userId, Integer orgId);
    
    /**
     * 检查用户是否有权限操作岗位
     * 
     * @param job 岗位实体
     * @param userDetails 当前登录用户
     * @return 是否有权限
     */
    boolean hasJobPermission(Job job, UserDetails userDetails);
    
    /**
     * 将岗位实体转换为VO
     * 
     * @param job 岗位实体
     * @return 岗位VO
     */
    JobVO convertToVO(Job job);
    
    /**
     * 获取岗位关联的分类列表
     *
     * @param jobId 岗位ID
     * @return 分类列表
     */
    List<JobCategoryVO> getJobCategories(Integer jobId);
    
    /**
     * 更新岗位关联的分类
     *
     * @param jobId       岗位ID
     * @param categoryIds 分类ID列表
     * @return 是否更新成功
     */
    boolean updateJobCategories(Integer jobId, List<Integer> categoryIds);
} 