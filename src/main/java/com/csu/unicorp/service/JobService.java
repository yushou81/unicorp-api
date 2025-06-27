package com.csu.unicorp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.unicorp.dto.JobCreationDTO;
import com.csu.unicorp.entity.Job;
import com.csu.unicorp.vo.JobVO;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 岗位服务接口
 */
public interface JobService {
    
    /**
     * 分页查询岗位列表，支持关键词搜索
     * 
     * @param page 页码
     * @param size 每页大小
     * @param keyword 搜索关键词
     * @return 岗位列表分页结果
     */
    IPage<JobVO> getJobList(int page, int size, String keyword);
    
    /**
     * 创建新岗位
     * 
     * @param jobCreationDTO 岗位创建DTO
     * @param userDetails 当前登录用户
     * @return 创建成功的岗位
     */
    JobVO createJob(JobCreationDTO jobCreationDTO, UserDetails userDetails);
    
    /**
     * 根据ID获取岗位详情
     * 
     * @param id 岗位ID
     * @return 岗位详情
     */
    JobVO getJobById(Integer id);
    
    /**
     * 更新岗位信息
     * 
     * @param id 岗位ID
     * @param jobCreationDTO 岗位更新DTO
     * @param userDetails 当前登录用户
     * @return 更新后的岗位
     */
    JobVO updateJob(Integer id, JobCreationDTO jobCreationDTO, UserDetails userDetails);
    
    /**
     * 删除岗位
     * 
     * @param id 岗位ID
     * @param userDetails 当前登录用户
     */
    void deleteJob(Integer id, UserDetails userDetails);
    
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
} 