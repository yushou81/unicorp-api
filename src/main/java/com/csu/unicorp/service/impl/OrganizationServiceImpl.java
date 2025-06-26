package com.csu.unicorp.service.impl;

import com.csu.unicorp.common.exception.BusinessException;
import com.csu.unicorp.dto.SchoolCreationDTO;
import com.csu.unicorp.entity.Organization;
import com.csu.unicorp.entity.SchoolDetail;
import com.csu.unicorp.entity.User;
import com.csu.unicorp.mapper.OrganizationMapper;
import com.csu.unicorp.mapper.UserMapper;
import com.csu.unicorp.service.OrganizationService;
import com.csu.unicorp.service.UserService;
import com.csu.unicorp.vo.OrganizationSimpleVO;
import com.csu.unicorp.vo.OrganizationVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 组织服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrganizationServiceImpl implements OrganizationService {
    
    private final OrganizationMapper organizationMapper;
    private final UserMapper userMapper;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public List<OrganizationSimpleVO> getAllSchools() {
        return organizationMapper.selectAllApprovedSchools();
    }
    
    @Override
    @Transactional
    public OrganizationVO createSchool(SchoolCreationDTO schoolCreationDTO) {
        // 检查学校名称是否已存在
        Organization existingOrg = organizationMapper.selectOne(
            query -> query.eq("organization_name", schoolCreationDTO.getOrganizationName())
        );
        
        if (existingOrg != null) {
            throw new BusinessException("学校名称已存在");
        }
        
        // 检查管理员账号是否已存在
        User existingUser = userMapper.selectByAccount(schoolCreationDTO.getAdminAccount());
        if (existingUser != null) {
            throw new BusinessException("管理员账号已存在");
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
        // 略
        
        // 创建学校管理员账号
        User admin = new User();
        admin.setAccount(schoolCreationDTO.getAdminAccount());
        admin.setPassword(passwordEncoder.encode(schoolCreationDTO.getAdminPassword()));
        admin.setEmail(schoolCreationDTO.getAdminEmail());
        admin.setNickname(schoolCreationDTO.getAdminNickname() != null 
                ? schoolCreationDTO.getAdminNickname() 
                : schoolCreationDTO.getAdminAccount());
        admin.setOrganizationId(organization.getId());
        admin.setStatus("active"); // 管理员创建的账号直接激活
        
        userMapper.insert(admin);
        
        // 分配学校管理员角色
        // 此处需要调用UserService中的角色分配方法，但可能会造成循环依赖
        // 暂时略过，实际项目中可以通过其他方式解决
        
        return convertToVO(organization);
    }
    
    @Override
    public Organization getById(Integer id) {
        return organizationMapper.selectById(id);
    }
    
    @Override
    public OrganizationVO convertToVO(Organization organization) {
        if (organization == null) {
            return null;
        }
        
        OrganizationVO vo = new OrganizationVO();
        vo.setId(organization.getId());
        vo.setOrganizationName(organization.getOrganizationName());
        vo.setType(organization.getType());
        vo.setDescription(organization.getDescription());
        vo.setWebsite(organization.getWebsite());
        vo.setAddress(organization.getAddress());
        
        return vo;
    }
} 