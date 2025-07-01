package com.csu.unicorp.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.unicorp.dto.MentorCreationDTO;
import com.csu.unicorp.dto.MentorUpdateDTO;
import com.csu.unicorp.dto.UserUpdateDTO;
import com.csu.unicorp.vo.UserVO;

/**
 * 企业管理员服务接口
 */
public interface EnterpriseAdminService {
    
    /**
     * 获取本企业所有用户
     * 
     * @param organizationId 企业ID
     * @param page 页码
     * @param size 每页大小
     * @return 用户列表（分页）
     */
    Page<UserVO> getAllUsers(Integer organizationId, Integer page, Integer size);
    
    /**
     * 创建企业导师账号
     * 
     * @param organizationId 企业ID
     * @param mentorCreationDTO 导师创建信息
     * @return 创建成功的导师信息
     */
    UserVO createMentor(Integer organizationId, MentorCreationDTO mentorCreationDTO);
    
    /**
     * 更新导师信息
     * 
     * @param organizationId 企业ID
     * @param mentorId 导师ID
     * @param mentorUpdateDTO 导师更新信息
     * @return 更新后的导师信息
     */
    UserVO updateMentor(Integer organizationId, Integer mentorId, MentorUpdateDTO mentorUpdateDTO);
    
    /**
     * 更新导师状态
     * 
     * @param organizationId 企业ID
     * @param mentorId 导师ID
     * @param status 新状态，可选值：active, inactive, pending_approval
     */
    void updateMentorStatus(Integer organizationId, Integer mentorId, String status);
    
    /**
     * 更新导师基本信息
     * 
     * @param organizationId 企业ID
     * @param mentorId 导师ID
     * @param userUpdateDTO 用户更新信息
     * @return 更新后的导师信息
     */
    UserVO updateMentorInfo(Integer organizationId, Integer mentorId, UserUpdateDTO userUpdateDTO);
    
    /**
     * 禁用导师账号
     * 
     * @param organizationId 企业ID
     * @param mentorId 导师ID
     * @deprecated 请使用 {@link #updateMentorStatus(Integer, Integer, String)} 替代
     */
    @Deprecated
    void disableMentor(Integer organizationId, Integer mentorId);
} 