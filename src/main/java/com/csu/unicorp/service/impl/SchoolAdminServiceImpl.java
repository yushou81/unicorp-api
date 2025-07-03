package com.csu.unicorp.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.unicorp.common.constants.RoleConstants;
import com.csu.unicorp.common.exception.BusinessException;
import com.csu.unicorp.common.utils.AccountGenerator;
import com.csu.unicorp.dto.TeacherCreationDTO;
import com.csu.unicorp.dto.TeacherUpdateDTO;
import com.csu.unicorp.dto.UserUpdateDTO;
import com.csu.unicorp.entity.organization.Organization;
import com.csu.unicorp.entity.User;
import com.csu.unicorp.mapper.OrganizationMapper;
import com.csu.unicorp.mapper.UserMapper;
import com.csu.unicorp.service.RoleService;
import com.csu.unicorp.service.SchoolAdminService;
import com.csu.unicorp.vo.UserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 学校管理员服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SchoolAdminServiceImpl implements SchoolAdminService {

    private final UserMapper userMapper;
    private final OrganizationMapper organizationMapper;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final AccountGenerator accountGenerator;
    
    @Override
    public Page<UserVO> getAllUsers(Integer organizationId, Integer page, Integer size, Integer roleId) {
        // 创建分页对象
        Page<User> pageParam = new Page<>(page, size);
        
        // 查询该学校的所有用户
        Page<User> userPage;
        if (roleId != null) {
            // 按角色筛选
            userPage = userMapper.selectUsersByOrganizationIdAndRoleId(organizationId, roleId, pageParam);
        } else {
            // 查询所有用户
            userPage = userMapper.selectUsersByOrganizationId(organizationId, pageParam);
        }
        
        // 转换为VO
        Page<UserVO> voPage = new Page<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
        List<UserVO> voList = userPage.getRecords().stream()
                .map(this::convertToVO)
                .toList();
        voPage.setRecords(voList);
        
        return voPage;
    }
    
    @Override
    @Transactional
    public UserVO createTeacher(Integer organizationId, TeacherCreationDTO teacherCreationDTO) {
        // 检查邮箱是否已存在
        User existingUserByEmail = userMapper.selectByEmail(teacherCreationDTO.getEmail());
        if (existingUserByEmail != null) {
            throw new BusinessException("该邮箱已被注册");
        }
        
        // 如果提供了手机号，检查是否已存在
        if (teacherCreationDTO.getPhone() != null && !teacherCreationDTO.getPhone().isEmpty()) {
            User existingUserByPhone = userMapper.selectByPhone(teacherCreationDTO.getPhone());
            if (existingUserByPhone != null) {
                throw new BusinessException("该手机号已被注册");
            }
        }
        
        // 获取学校信息
        Organization school = organizationMapper.selectById(organizationId);
        if (school == null) {
            throw new BusinessException("学校不存在");
        }
        
        // 生成教师账号
        String generatedAccount = accountGenerator.generateTeacherAccount(school);
        
        // 创建教师用户
        User teacher = new User();
        teacher.setAccount(generatedAccount);
        teacher.setEmail(teacherCreationDTO.getEmail());
        teacher.setPassword(passwordEncoder.encode(teacherCreationDTO.getPassword()));
        teacher.setNickname(teacherCreationDTO.getNickname());
        teacher.setPhone(teacherCreationDTO.getPhone());
        teacher.setOrganizationId(organizationId);
        teacher.setStatus("active");
        
        userMapper.insert(teacher);
        
        // 分配教师角色
        roleService.assignRoleToUser(teacher.getId(), RoleConstants.DB_ROLE_TEACHER);
        
        return convertToVO(teacher);
    }
    
    @Override
    public UserVO updateTeacher(Integer organizationId, Integer teacherId, TeacherUpdateDTO teacherUpdateDTO) {
        // 获取要更新的教师
        User teacher = userMapper.selectById(teacherId);
        if (teacher == null) {
            throw new BusinessException("教师不存在");
        }
        
        // 验证教师是否属于该学校
        if (!teacher.getOrganizationId().equals(organizationId)) {
            throw new BusinessException("权限不足，只能更新本校教师信息");
        }
        
        // 验证是否为教师角色
        String teacherRole = userMapper.selectRoleByUserId(teacher.getId());
        if (!RoleConstants.DB_ROLE_TEACHER.equals(teacherRole)) {
            throw new BusinessException("该用户不是教师");
        }
        
        // 更新教师信息
        if (teacherUpdateDTO.getNickname() != null) {
            teacher.setNickname(teacherUpdateDTO.getNickname());
        }
        if (teacherUpdateDTO.getPhone() != null) {
            // 如果要更新手机号，检查新手机号是否已被使用
            User existingUserByPhone = userMapper.selectByPhone(teacherUpdateDTO.getPhone());
            if (existingUserByPhone != null && !existingUserByPhone.getId().equals(teacherId)) {
                throw new BusinessException("该手机号已被其他用户使用");
            }
            teacher.setPhone(teacherUpdateDTO.getPhone());
        }
        
        userMapper.updateById(teacher);
        
        return convertToVO(teacher);
    }
    
    @Override
    public void updateUserStatus(Integer organizationId, Integer userId, String status) {
        // 验证状态值是否合法
        if (!isValidStatus(status)) {
            throw new BusinessException("无效的状态值，可选值：active, inactive, pending_approval");
        }
        
        // 获取要更新状态的用户
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 验证用户是否属于该学校
        if (!user.getOrganizationId().equals(organizationId)) {
            throw new BusinessException("权限不足，只能管理本校用户账号");
        }
        
        // 获取用户角色
        String userRole = userMapper.selectRoleByUserId(user.getId());
        
        // 验证是否为教师或学生角色
        if (!RoleConstants.DB_ROLE_TEACHER.equals(userRole) && 
            !RoleConstants.DB_ROLE_STUDENT.equals(userRole)) {
            throw new BusinessException("只能管理教师或学生账号");
        }
        
        // 检查状态是否已经是目标状态
        if (status.equals(user.getStatus())) {
            throw new BusinessException("用户账号已经处于" + getStatusDisplayName(status) + "状态");
        }
        
        // 更新用户状态
        user.setStatus(status);
        userMapper.updateById(user);
    }
    
    @Override
    public UserVO updateUserInfo(Integer organizationId, Integer userId, UserUpdateDTO userUpdateDTO) {
        // 获取要更新的用户
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 验证用户是否属于该学校
        if (!user.getOrganizationId().equals(organizationId)) {
            throw new BusinessException("权限不足，只能管理本校用户账号");
        }
        
        // 获取用户角色
        String userRole = userMapper.selectRoleByUserId(user.getId());
        
        // 验证是否为教师或学生角色
        if (!RoleConstants.DB_ROLE_TEACHER.equals(userRole) && 
            !RoleConstants.DB_ROLE_STUDENT.equals(userRole)) {
            throw new BusinessException("只能管理教师或学生账号");
        }
        
        // 检查邮箱唯一性
        if (userUpdateDTO.getEmail() != null && !userUpdateDTO.getEmail().isEmpty() && 
            !userUpdateDTO.getEmail().equals(user.getEmail())) {
            User existingUserByEmail = userMapper.selectByEmail(userUpdateDTO.getEmail());
            if (existingUserByEmail != null) {
                throw new BusinessException("该邮箱已被其他用户使用");
            }
            user.setEmail(userUpdateDTO.getEmail());
        }
        
        // 检查手机号唯一性
        if (userUpdateDTO.getPhone() != null && !userUpdateDTO.getPhone().isEmpty() && 
            !userUpdateDTO.getPhone().equals(user.getPhone())) {
            User existingUserByPhone = userMapper.selectByPhone(userUpdateDTO.getPhone());
            if (existingUserByPhone != null) {
                throw new BusinessException("该手机号已被其他用户使用");
            }
            user.setPhone(userUpdateDTO.getPhone());
        }
        
        // 更新昵称
        if (userUpdateDTO.getNickname() != null && !userUpdateDTO.getNickname().isEmpty()) {
            user.setNickname(userUpdateDTO.getNickname());
        }
        
        // 保存更新
        userMapper.updateById(user);
        
        // 返回更新后的用户信息
        return convertToVO(user);
    }
    
    /**
     * 检查状态值是否有效
     */
    private boolean isValidStatus(String status) {
        return "active".equals(status) || 
               "inactive".equals(status) || 
               "pending_approval".equals(status);
    }
    
    /**
     * 获取状态的显示名称
     */
    private String getStatusDisplayName(String status) {
        switch (status) {
            case "active":
                return "启用";
            case "inactive":
                return "禁用";
            case "pending_approval":
                return "待审核";
            default:
                return status;
        }
    }
    
    /**
     * 将用户实体转换为VO
     */
    private UserVO convertToVO(User user) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        
        // 获取用户角色
        String role = userMapper.selectRoleByUserId(user.getId());
        vo.setRole(role);
        
        return vo;
    }
} 