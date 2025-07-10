package com.csu.unicorp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csu.unicorp.entity.organization.Organization;
import com.csu.unicorp.vo.OrganizationSimpleVO;
import com.csu.unicorp.vo.OrganizationVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 组织Mapper接口
 */
@Mapper
public interface OrganizationMapper extends BaseMapper<Organization> {
    
    /**
     * 查询所有已批准的学校列表（简化版）
     * 
     * @return 学校简化列表
     */
    @Select("SELECT id, organization_name, logo_url FROM organizations " +
            "WHERE type = 'School' AND status = 'approved' AND is_deleted = 0 " +
            "ORDER BY organization_name")
    List<OrganizationSimpleVO> selectAllApprovedSchools();
    
    /**
     * 查询所有已批准的学校列表（详细版）
     * 
     * @return 学校详细列表
     */
    @Select("SELECT o.id, o.organization_name, o.type, o.description, o.website, o.logo_url, o.address, " +
            "o.latitude, o.longitude, u.email as admin_email " +
            "FROM organizations o " +
            "LEFT JOIN users u ON u.organization_id = o.id " + 
            "LEFT JOIN user_roles ur ON ur.user_id = u.id " +
            "LEFT JOIN roles r ON r.id = ur.role_id " +
            "WHERE o.type = 'School' AND o.status = 'approved' AND o.is_deleted = 0 " +
            "AND r.role_name = 'SCH_ADMIN' " +
            "ORDER BY o.organization_name")
    List<OrganizationVO> selectAllApprovedSchoolsDetailed();
    
    /**
     * 查询所有已批准的企业列表（简化版）
     * 
     * @return 企业简化列表
     */
    @Select("SELECT id, organization_name, logo_url FROM organizations " +
            "WHERE type = 'Enterprise' AND status = 'approved' AND is_deleted = 0 " +
            "ORDER BY organization_name")
    List<OrganizationSimpleVO> selectAllApprovedEnterprises();
    
    /**
     * 查询所有已批准的企业列表（详细版）
     * 
     * @return 企业详细列表
     */
    @Select("SELECT o.id, o.organization_name, o.type, o.description, o.website, o.logo_url, o.address, " +
            "o.latitude, o.longitude, u.email as admin_email " +
            "FROM organizations o " +
            "LEFT JOIN users u ON u.organization_id = o.id " +
            "LEFT JOIN user_roles ur ON ur.user_id = u.id " +
            "LEFT JOIN roles r ON r.id = ur.role_id " +
            "WHERE o.type = 'Enterprise' AND o.status = 'approved' AND o.is_deleted = 0 " +
            "AND r.role_name = 'EN_ADMIN' " +
            "ORDER BY o.organization_name")
    List<OrganizationVO> selectAllApprovedEnterprisesDetailed();
} 