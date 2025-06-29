package com.csu.unicorp.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.unicorp.common.constants.RoleConstants;
import com.csu.unicorp.common.exception.BusinessException;
import com.csu.unicorp.common.utils.AccountGenerator;
import com.csu.unicorp.dto.MentorCreationDTO;
import com.csu.unicorp.dto.MentorUpdateDTO;
import com.csu.unicorp.entity.Organization;
import com.csu.unicorp.entity.User;
import com.csu.unicorp.mapper.OrganizationMapper;
import com.csu.unicorp.mapper.UserMapper;
import com.csu.unicorp.service.EnterpriseAdminService;
import com.csu.unicorp.service.RoleService;
import com.csu.unicorp.vo.UserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 企业管理员服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EnterpriseAdminServiceImpl implements EnterpriseAdminService {

    private final UserMapper userMapper;
    private final OrganizationMapper organizationMapper;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final AccountGenerator accountGenerator;
    
    @Override
    public Page<UserVO> getAllUsers(Integer organizationId, Integer page, Integer size) {
        // 创建分页对象
        Page<User> pageParam = new Page<>(page, size);
        
        // 查询该企业的所有用户
        Page<User> userPage = userMapper.selectUsersByOrganizationId(organizationId, pageParam);
        
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
    public UserVO createMentor(Integer organizationId, MentorCreationDTO mentorCreationDTO) {
        // 检查邮箱是否已存在
        User existingUserByEmail = userMapper.selectByEmail(mentorCreationDTO.getEmail());
        if (existingUserByEmail != null) {
            throw new BusinessException("该邮箱已被注册");
        }
        
        // 如果提供了手机号，检查是否已存在
        if (mentorCreationDTO.getPhone() != null && !mentorCreationDTO.getPhone().isEmpty()) {
            User existingUserByPhone = userMapper.selectByPhone(mentorCreationDTO.getPhone());
            if (existingUserByPhone != null) {
                throw new BusinessException("该手机号已被注册");
            }
        }
        
        // 获取企业信息
        Organization enterprise = organizationMapper.selectById(organizationId);
        if (enterprise == null) {
            throw new BusinessException("企业不存在");
        }
        
        // 生成导师账号
        String generatedAccount = accountGenerator.generateMentorAccount(enterprise);
        
        // 创建导师用户
        User mentor = new User();
        mentor.setAccount(generatedAccount);
        mentor.setEmail(mentorCreationDTO.getEmail());
        mentor.setPassword(passwordEncoder.encode(mentorCreationDTO.getPassword()));
        mentor.setNickname(mentorCreationDTO.getNickname());
        mentor.setPhone(mentorCreationDTO.getPhone());
        mentor.setOrganizationId(organizationId);
        mentor.setStatus("active");
        
        userMapper.insert(mentor);
        
        // 分配导师角色
        roleService.assignRoleToUser(mentor.getId(), RoleConstants.DB_ROLE_ENTERPRISE_MENTOR);
        
        return convertToVO(mentor);
    }
    
    @Override
    public UserVO updateMentor(Integer organizationId, Integer mentorId, MentorUpdateDTO mentorUpdateDTO) {
        // 获取要更新的导师
        User mentor = userMapper.selectById(mentorId);
        if (mentor == null) {
            throw new BusinessException("导师不存在");
        }
        
        // 验证导师是否属于该企业
        if (!mentor.getOrganizationId().equals(organizationId)) {
            throw new BusinessException("权限不足，只能更新本企业导师信息");
        }
        
        // 验证是否为导师角色
        String mentorRole = userMapper.selectRoleByUserId(mentor.getId());
        if (!RoleConstants.DB_ROLE_ENTERPRISE_MENTOR.equals(mentorRole)) {
            throw new BusinessException("该用户不是企业导师");
        }
        
        // 更新导师信息
        if (mentorUpdateDTO.getNickname() != null) {
            mentor.setNickname(mentorUpdateDTO.getNickname());
        }
        if (mentorUpdateDTO.getPhone() != null) {
            // 如果要更新手机号，检查新手机号是否已被使用
            User existingUserByPhone = userMapper.selectByPhone(mentorUpdateDTO.getPhone());
            if (existingUserByPhone != null && !existingUserByPhone.getId().equals(mentorId)) {
                throw new BusinessException("该手机号已被其他用户使用");
            }
            mentor.setPhone(mentorUpdateDTO.getPhone());
        }
        
        userMapper.updateById(mentor);
        
        return convertToVO(mentor);
    }
    
    @Override
    public void disableMentor(Integer organizationId, Integer mentorId) {
        // 获取要禁用的导师
        User mentor = userMapper.selectById(mentorId);
        if (mentor == null) {
            throw new BusinessException("导师不存在");
        }
        
        // 验证导师是否属于该企业
        if (!mentor.getOrganizationId().equals(organizationId)) {
            throw new BusinessException("权限不足，只能禁用本企业导师账号");
        }
        
        // 验证是否为导师角色
        String mentorRole = userMapper.selectRoleByUserId(mentor.getId());
        if (!RoleConstants.DB_ROLE_ENTERPRISE_MENTOR.equals(mentorRole)) {
            throw new BusinessException("该用户不是企业导师");
        }
        
        // 禁用导师账号
        mentor.setStatus("inactive");
        userMapper.updateById(mentor);
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