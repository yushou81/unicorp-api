package com.csu.unicorp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.csu.unicorp.common.constants.RoleConstants;
import com.csu.unicorp.common.exception.BusinessException;
import com.csu.unicorp.common.exception.ResourceNotFoundException;
import com.csu.unicorp.common.utils.AccountGenerator;
import com.csu.unicorp.dto.SchoolCreationDTO;
import com.csu.unicorp.entity.organization.Organization;
import com.csu.unicorp.entity.User;
import com.csu.unicorp.mapper.OrganizationMapper;
import com.csu.unicorp.mapper.UserMapper;
import com.csu.unicorp.service.OrganizationService;
import com.csu.unicorp.service.RoleService;
import com.csu.unicorp.service.FileService;
import com.csu.unicorp.vo.OrganizationSimpleVO;
import com.csu.unicorp.vo.OrganizationVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    private final FileService fileService;
    
    @Override
    public List<?> getAllSchools(String view) {
        // 根据view参数返回不同详细程度的学校列表
        if ("detailed".equals(view)) {
            List<OrganizationVO> schools = organizationMapper.selectAllApprovedSchoolsDetailed();
            
            // 处理logo URL
            for (OrganizationVO school : schools) {
                if (school.getLogoUrl() != null && !school.getLogoUrl().isEmpty()) {
                    school.setLogoUrl(fileService.getFullFileUrl(school.getLogoUrl()));
                }
            }
            
            return schools;
        } else {
            // 默认返回简化版
            List<OrganizationSimpleVO> schools = organizationMapper.selectAllApprovedSchools();
            
            // 处理logo URL
            for (OrganizationSimpleVO school : schools) {
                if (school.getLogoUrl() != null && !school.getLogoUrl().isEmpty()) {
                    school.setLogoUrl(fileService.getFullFileUrl(school.getLogoUrl()));
                }
            }
            
            return schools;
        }
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
        
        // 如果有logo文件，处理并设置logo_url
        if (schoolCreationDTO.getLogoFile() != null && !schoolCreationDTO.getLogoFile().isEmpty()) {
            try {
                // 上传文件，获取存储路径
                String logoPath = fileService.uploadFile(schoolCreationDTO.getLogoFile(), "logos");
                organization.setLogoUrl(logoPath);
            } catch (Exception e) {
                throw new BusinessException("Logo上传失败: " + e.getMessage());
            }
        }
        
        organizationMapper.insert(organization);
        
        // 生成学校管理员账号
        String adminAccount = accountGenerator.generateStudentAccount(organization);
        
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

        log.info("创建学校管理员：{}", admin);

        // 分配学校管理员角色
        roleService.assignRoleToUser(admin.getId(), RoleConstants.DB_ROLE_SCHOOL_ADMIN);

        log.info("分配学校管理员角色：{}", admin);
        OrganizationVO organizationVO = convertToVO(organization);
        organizationVO.setAdminEmail(schoolCreationDTO.getAdminEmail());
        return organizationVO;
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
        
        // 转换为VO列表，并处理logo URL
        List<OrganizationVO> result = pendingOrganizations.stream()
                .map(this::convertToVO)
                .peek(vo -> {
                    if (vo.getLogoUrl() != null && !vo.getLogoUrl().isEmpty()) {
                        vo.setLogoUrl(fileService.getFullFileUrl(vo.getLogoUrl()));
                    }
                })
                .collect(Collectors.toList());
        
        return result;
    }
    
    @Override
    public List<?> getAllEnterprises(String view) {
        // 根据view参数返回不同详细程度的企业列表
        if ("detailed".equals(view)) {
            List<OrganizationVO> enterprises = organizationMapper.selectAllApprovedEnterprisesDetailed();
            
            // 处理logo URL
            for (OrganizationVO enterprise : enterprises) {
                if (enterprise.getLogoUrl() != null && !enterprise.getLogoUrl().isEmpty()) {
                    enterprise.setLogoUrl(fileService.getFullFileUrl(enterprise.getLogoUrl()));
                }
            }
            
            return enterprises;
        } else {
            // 默认返回简化版
            List<OrganizationSimpleVO> enterprises = organizationMapper.selectAllApprovedEnterprises();
            
            // 处理logo URL
            for (OrganizationSimpleVO enterprise : enterprises) {
                if (enterprise.getLogoUrl() != null && !enterprise.getLogoUrl().isEmpty()) {
                    enterprise.setLogoUrl(fileService.getFullFileUrl(enterprise.getLogoUrl()));
                }
            }
            
            return enterprises;
        }
    }
    
    @Override
    public OrganizationVO getSchoolById(Integer id) {
        // 查询学校并验证类型
        Organization organization = organizationMapper.selectById(id);
        if (organization == null || !"School".equals(organization.getType()) || !"approved".equals(organization.getStatus())) {
            throw new ResourceNotFoundException("学校不存在或未通过审核");
        }
        
        // 查询学校管理员邮箱
        String adminEmail = findAdminEmail(organization.getId(), RoleConstants.DB_ROLE_SCHOOL_ADMIN);
        
        // 转换为VO
        OrganizationVO vo = OrganizationVO.fromEntity(organization);
        vo.setAdminEmail(adminEmail);
        
        // 处理logo URL，转换为完整路径
        if (vo.getLogoUrl() != null && !vo.getLogoUrl().isEmpty()) {
            vo.setLogoUrl(fileService.getFullFileUrl(vo.getLogoUrl()));
        }
        
        return vo;
    }
    
    @Override
    public OrganizationVO getEnterpriseById(Integer id) {
        // 查询企业并验证类型
        Organization organization = organizationMapper.selectById(id);
        if (organization == null || !"Enterprise".equals(organization.getType()) || !"approved".equals(organization.getStatus())) {
            throw new ResourceNotFoundException("企业不存在或未通过审核");
        }
        
        // 查询企业管理员邮箱
        String adminEmail = findAdminEmail(organization.getId(), RoleConstants.DB_ROLE_ENTERPRISE_ADMIN);
        
        // 转换为VO
        OrganizationVO vo = OrganizationVO.fromEntity(organization);
        vo.setAdminEmail(adminEmail);
        
        // 处理logo URL，转换为完整路径
        if (vo.getLogoUrl() != null && !vo.getLogoUrl().isEmpty()) {
            vo.setLogoUrl(fileService.getFullFileUrl(vo.getLogoUrl()));
        }
        
        return vo;
    }
    
    /**
     * 查找组织管理员的邮箱
     * 
     * @param organizationId 组织ID
     * @param roleName 角色名称
     * @return 管理员邮箱，如果未找到则返回null
     */
    private String findAdminEmail(Integer organizationId, String roleName) {
        // 查询该组织下的所有用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("organization_id", organizationId)
                    .eq("status", "active");
        
        List<User> users = userMapper.selectList(queryWrapper);
        
        // 过滤出管理员角色的用户
        for (User user : users) {
            String userRole = userMapper.selectRoleByUserId(user.getId());
            if (roleName.equals(userRole)) {
                return user.getEmail();
            }
        }
        
        return null;
    }
    
    @Override
    public String updateOrganizationLogo(Integer id, MultipartFile logoFile) {
        // 检查组织是否存在
        Organization organization = organizationMapper.selectById(id);
        if (organization == null) {
            throw new ResourceNotFoundException("组织不存在");
        }
        
        // 上传logo文件
        try {
            String logoPath = fileService.uploadFile(logoFile, "logo");
            
            // 更新组织logo路径
            organization.setLogoUrl(logoPath);
            organizationMapper.updateById(organization);
            
            // 返回完整的logo URL
            return fileService.getFullFileUrl(logoPath);
        } catch (Exception e) {
            throw new BusinessException("Logo上传失败: " + e.getMessage());
        }
    }
} 