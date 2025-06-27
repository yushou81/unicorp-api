package com.csu.unicorp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.csu.unicorp.vo.ApplicationDetailVO;
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
} 