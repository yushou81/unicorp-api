package com.csu.unicorp.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.unicorp.dto.TeacherCreationDTO;
import com.csu.unicorp.dto.TeacherUpdateDTO;
import com.csu.unicorp.dto.UserUpdateDTO;
import com.csu.unicorp.vo.UserVO;

/**
 * 学校管理员服务接口
 */
public interface SchoolAdminService {
    
    /**
     * 获取本校所有用户
     * 
     * @param organizationId 学校ID
     * @param page 页码
     * @param size 每页大小
     * @param roleId 角色ID（可选）
     * @return 用户列表（分页）
     */
    Page<UserVO> getAllUsers(Integer organizationId, Integer page, Integer size, Integer roleId);
    
    /**
     * 创建教师账号
     * 
     * @param organizationId 学校ID
     * @param teacherCreationDTO 教师创建信息
     * @return 创建成功的教师信息
     */
    UserVO createTeacher(Integer organizationId, TeacherCreationDTO teacherCreationDTO);
    
    /**
     * 更新教师信息
     * 
     * @param organizationId 学校ID
     * @param teacherId 教师ID
     * @param teacherUpdateDTO 教师更新信息
     * @return 更新后的教师信息
     */
    UserVO updateTeacher(Integer organizationId, Integer teacherId, TeacherUpdateDTO teacherUpdateDTO);
    
    /**
     * 更新用户状态
     * 
     * @param organizationId 学校ID
     * @param userId 用户ID
     * @param status 新状态，可选值：active, inactive, pending_approval
     */
    void updateUserStatus(Integer organizationId, Integer userId, String status);
    
    /**
     * 更新用户基本信息
     * 
     * @param organizationId 学校ID
     * @param userId 用户ID
     * @param userUpdateDTO 用户更新信息
     * @return 更新后的用户信息
     */
    UserVO updateUserInfo(Integer organizationId, Integer userId, UserUpdateDTO userUpdateDTO);
} 