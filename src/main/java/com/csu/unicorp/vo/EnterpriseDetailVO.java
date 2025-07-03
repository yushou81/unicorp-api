package com.csu.unicorp.vo;

import com.csu.unicorp.entity.EnterpriseDetail;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 企业详情视图对象
 */
@Data
@Schema(description = "企业详情信息")
public class EnterpriseDetailVO {
    
    /**
     * 组织ID
     */
    @Schema(description = "组织ID")
    private Integer organizationId;
    
    /**
     * 所属行业
     */
    @Schema(description = "所属行业")
    private String industry;
    
    /**
     * 公司规模
     */
    @Schema(description = "公司规模")
    private String companySize;
    
    /**
     * 营业执照图片URL
     */
    @Schema(description = "营业执照图片URL")
    private String businessLicenseUrl;
    
    /**
     * 将EnterpriseDetail实体转换为EnterpriseDetailVO
     * 
     * @param enterpriseDetail 企业详情实体
     * @return 企业详情视图对象
     */
    public static EnterpriseDetailVO fromEntity(EnterpriseDetail enterpriseDetail) {
        if (enterpriseDetail == null) {
            return null;
        }
        
        EnterpriseDetailVO vo = new EnterpriseDetailVO();
        vo.setOrganizationId(enterpriseDetail.getOrganizationId());
        vo.setIndustry(enterpriseDetail.getIndustry());
        vo.setCompanySize(enterpriseDetail.getCompanySize());
        vo.setBusinessLicenseUrl(enterpriseDetail.getBusinessLicenseUrl());
        return vo;
    }
} 