package com.csu.unicorp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.unicorp.common.constants.RoleConstants;
import com.csu.unicorp.common.exception.BusinessException;
import com.csu.unicorp.common.utils.AccountGenerator;
import com.csu.unicorp.dto.SchoolCreationDTO;
import com.csu.unicorp.entity.Organization;
import com.csu.unicorp.entity.SchoolDetail;
import com.csu.unicorp.entity.User;
import com.csu.unicorp.mapper.OrganizationMapper;
import com.csu.unicorp.mapper.UserMapper;
import com.csu.unicorp.service.OrganizationService;
import com.csu.unicorp.service.RoleService;
import com.csu.unicorp.vo.OrganizationSimpleVO;
import com.csu.unicorp.vo.OrganizationVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 组织服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrganizationServiceImpl implements OrganizationService {
    
    private final OrganizationMapper organizationMapper;
    private final UserMapper userMapper;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final AccountGenerator accountGenerator;
    
    @Override
    public List<OrganizationSimpleVO> getAllSchools() {
        return organizationMapper.selectAllApprovedSchools();
    }
    
    @Override
    @Transactional
    public OrganizationVO createSchool(SchoolCreationDTO schoolCreationDTO) {
        // 检查学校名称是否已存在
        LambdaQueryWrapper<Organization> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Organization::getOrganizationName, schoolCreationDTO.getOrganizationName());
        Organization existingOrg = organizationMapper.selectOne(queryWrapper);
        
        if (existingOrg != null) {
            throw new BusinessException("学校名称已存在");
        }
        
        // 检查管理员邮箱是否已存在
        User existingUserByEmail = userMapper.selectByEmail(schoolCreationDTO.getAdminEmail());
        if (existingUserByEmail != null) {
            throw new BusinessException("管理员邮箱已被使用");
        }
        
        // 创建学校
        Organization organization = new Organization();
        organization.setOrganizationName(schoolCreationDTO.getOrganizationName());
        organization.setDescription(schoolCreationDTO.getDescription());
        organization.setAddress(schoolCreationDTO.getAddress());
        organization.setWebsite(schoolCreationDTO.getWebsite());
        organization.setType("School");
        organization.setStatus("approved"); // 管理员创建的学校直接审核通过
        
        organizationMapper.insert(organization);
        
        // 创建学校详情（如有需要）
        // SchoolDetail schoolDetail = new SchoolDetail();
        // schoolDetail.setOrganizationId(organization.getId());
        // ... 设置其他属性
        // schoolDetailMapper.insert(schoolDetail);
        
        // 生成学校管理员账号
        String adminAccount = "admin_" + accountGenerator.generateStudentAccount(organization).substring(0, 8);
        
        // 创建学校管理员账号
        User admin = new User();
        admin.setAccount(adminAccount);
        admin.setPassword(passwordEncoder.encode(schoolCreationDTO.getAdminPassword()));
        admin.setEmail(schoolCreationDTO.getAdminEmail());
        admin.setNickname(schoolCreationDTO.getAdminNickname() != null 
                ? schoolCreationDTO.getAdminNickname() 
                : "管理员-" + schoolCreationDTO.getOrganizationName()); // 如果未提供昵称，使用学校名称
        admin.setOrganizationId(organization.getId());
        admin.setStatus("active"); // 管理员创建的账号直接激活
        
        userMapper.insert(admin);
        
        // 分配学校管理员角色
        roleService.assignRoleToUser(admin.getId(), RoleConstants.DB_ROLE_STUDENT);
        
        return convertToVO(organization);
    }
    
    @Override
    public Organization getById(Integer id) {
        return organizationMapper.selectById(id);
    }
    
    @Override
    public Organization getByName(String name) {
        LambdaQueryWrapper<Organization> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Organization::getOrganizationName, name);
        return organizationMapper.selectOne(queryWrapper);
    }
    
    @Override
    public OrganizationVO convertToVO(Organization organization) {
        OrganizationVO vo = new OrganizationVO();
        BeanUtils.copyProperties(organization, vo);
        return vo;
    }
    
    @Override
    public List<OrganizationVO> getPendingOrganizations() {
        // 查询状态为pending的组织
        QueryWrapper<Organization> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", "pending")
                    .eq("type", "enterprise");
        
        List<Organization> pendingOrganizations = organizationMapper.selectList(queryWrapper);
        
        // 转换为VO列表
        return pendingOrganizations.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }
} 