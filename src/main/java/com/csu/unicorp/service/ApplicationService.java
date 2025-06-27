package com.csu.unicorp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.csu.unicorp.dto.ApplicationStatusUpdateDTO;
import com.csu.unicorp.vo.ApplicationDetailVO;
import com.csu.unicorp.vo.MyApplicationDetailVO;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 岗位申请服务接口
 */
public interface ApplicationService {
    
    /**
     * 学生申请岗位
     * 
     * @param jobId 岗位ID
     * @param userDetails 当前登录用户
     * @return 申请ID
     */
    Integer applyJob(Integer jobId, UserDetails userDetails);
    
    /**
     * 获取岗位的申请列表
     * 
     * @param jobId 岗位ID
     * @param page 页码
     * @param size 每页大小
     * @param userDetails 当前登录用户
     * @return 申请列表分页结果
     */
    IPage<ApplicationDetailVO> getApplicationsByJobId(Integer jobId, int page, int size, UserDetails userDetails);
    
    /**
     * 更新岗位申请状态
     * 
     * @param id 申请ID
     * @param statusUpdateDTO 状态更新DTO
     * @param userDetails 当前登录用户
     * @return 更新后的申请详情
     */
    ApplicationDetailVO updateApplicationStatus(Integer id, ApplicationStatusUpdateDTO statusUpdateDTO, UserDetails userDetails);
    
    /**
     * 获取当前学生用户的申请列表
     * 
     * @param page 页码
     * @param size 每页大小
     * @param userDetails 当前登录用户
     * @return 申请列表分页结果
     */
    IPage<MyApplicationDetailVO> getMyApplications(int page, int size, UserDetails userDetails);
} 