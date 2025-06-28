package com.csu.unicorp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.csu.unicorp.dto.ApplicationStatusUpdateDTO;
import com.csu.unicorp.entity.Application;
import com.csu.unicorp.vo.ApplicationDetailVO;
import com.csu.unicorp.vo.MyApplicationDetailVO;

/**
 * 岗位申请服务接口
 */
public interface ApplicationService extends IService<Application> {
    
    /**
     * 学生申请岗位
     *
     * @param jobId     岗位ID
     * @param studentId 学生ID
     * @return 申请ID
     */
    Integer applyJob(Integer jobId, Integer studentId);
    
    /**
     * 分页查询岗位的申请列表
     *
     * @param jobId    岗位ID
     * @param page     页码
     * @param size     每页大小
     * @param orgId    组织ID
     * @return 申请列表分页结果
     */
    IPage<ApplicationDetailVO> pageApplicationsByJobId(Integer jobId, int page, int size, Integer orgId);
    
    /**
     * 更新申请状态
     *
     * @param id       申请ID
     * @param dto      状态更新DTO
     * @param userId   用户ID
     * @param orgId    组织ID
     * @return 更新后的申请详情
     */
    ApplicationDetailVO updateApplicationStatus(Integer id, ApplicationStatusUpdateDTO dto, Integer userId, Integer orgId);
    
    /**
     * 分页查询学生的申请列表
     *
     * @param studentId 学生ID
     * @param page      页码
     * @param size      每页大小
     * @return 申请列表分页结果
     */
    IPage<MyApplicationDetailVO> pageStudentApplications(Integer studentId, int page, int size);
} 