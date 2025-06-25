package com.csu.linkneiapi.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.linkneiapi.dto.JobApplicationDTO;
import com.csu.linkneiapi.entity.JobApplication;
import com.csu.linkneiapi.vo.JobApplicationVO;

import java.util.List;

/**
 * 投递记录服务接口
 */
public interface JobApplicationService {

    /**
     * 用户投递岗位
     * @param userId 用户ID
     * @param applicationDTO 投递信息
     * @return 是否投递成功
     */
    boolean applyJob(Long userId, JobApplicationDTO applicationDTO);
    
    /**
     * 查询用户的投递记录
     * @param userId 用户ID
     * @return 投递记录列表
     */
    List<JobApplicationVO> getUserApplications(Long userId);
    
    /**
     * 分页查询企业收到的投递记录
     * @param enterpriseId 企业ID
     * @param status 状态筛选
     * @param page 分页参数
     * @return 分页结果
     */
    IPage<JobApplicationVO> pageEnterpriseApplications(Long enterpriseId, String status, Page<JobApplication> page);
    
    /**
     * 企业更新投递状态
     * @param applicationId 投递记录ID
     * @param status 新状态
     * @param operatorId 操作人ID
     * @return 是否更新成功
     */
    boolean updateApplicationStatus(Long applicationId, String status, Long operatorId);
    
    /**
     * 获取投递记录详情
     * @param applicationId 投递记录ID
     * @return 详情视图
     */
    JobApplicationVO getApplicationDetail(Long applicationId);
} 