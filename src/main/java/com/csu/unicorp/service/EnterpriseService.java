package com.csu.unicorp.service;

import com.csu.unicorp.entity.EnterpriseDetail;
import com.csu.unicorp.entity.Organization;
import com.csu.unicorp.vo.OrganizationVO;

/**
 * 企业服务接口
 */
public interface EnterpriseService {
    
    /**
     * 创建企业
     *
     * @param organization 组织信息
     * @param enterpriseDetail 企业详情
     * @return 创建的企业ID
     */
    Integer createEnterprise(Organization organization, EnterpriseDetail enterpriseDetail);
    
    /**
     * 根据ID获取企业详情
     *
     * @param organizationId 组织ID
     * @return 企业详情
     */
    EnterpriseDetail getEnterpriseDetailById(Integer organizationId);
    
    /**
     * 批准企业注册
     *
     * @param organizationId 企业组织ID
     * @return 更新后的企业组织信息
     */
    OrganizationVO approveEnterprise(Integer organizationId);
} 