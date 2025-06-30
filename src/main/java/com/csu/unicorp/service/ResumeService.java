package com.csu.unicorp.service;

import com.csu.unicorp.dto.ResumeCreationDTO;
import com.csu.unicorp.dto.ResumeUpdateDTO;
import com.csu.unicorp.vo.ResumeVO;

import java.util.List;

/**
 * 简历服务接口
 */
public interface ResumeService {
    
    /**
     * 获取用户简历
     *
     * @param userId 用户ID
     * @return 简历信息
     * @deprecated 一个用户可以拥有多份简历，请使用 {@link #getUserResumes(Integer)} 方法
     */
    @Deprecated
    ResumeVO getUserResume(Integer userId);
    
    /**
     * 获取用户所有简历
     *
     * @param userId 用户ID
     * @return 简历列表
     */
    List<ResumeVO> getUserResumes(Integer userId);
    
    /**
     * 通过ID获取简历
     *
     * @param resumeId 简历ID
     * @return 简历信息
     */
    ResumeVO getResumeById(Integer resumeId);
    
    /**
     * 创建简历
     *
     * @param userId 用户ID
     * @param dto 简历创建DTO
     * @return 创建的简历信息
     */
    ResumeVO createResume(Integer userId, ResumeCreationDTO dto);
    
    /**
     * 更新简历
     *
     * @param userId 用户ID
     * @param resumeId 简历ID
     * @param dto 简历更新DTO
     * @return 更新后的简历信息
     */
    ResumeVO updateResume(Integer userId, Integer resumeId, ResumeUpdateDTO dto);
    
    /**
     * 删除简历
     *
     * @param userId 用户ID
     * @param resumeId 简历ID
     */
    void deleteResume(Integer userId, Integer resumeId);
} 