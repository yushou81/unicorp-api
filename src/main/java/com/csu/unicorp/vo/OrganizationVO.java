package com.csu.unicorp.vo;

import com.csu.unicorp.entity.organization.Organization;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 组织视图对象
 */
@Data
@Schema(description = "组织信息")
public class OrganizationVO {
    
    /**
     * 组织ID
     */
    @Schema(description = "组织ID")
    private Integer id;
    
    /**
     * 组织名称
     */
    @Schema(description = "组织名称")
    private String organizationName;
    
    /**
     * 组织类型
     */
    @Schema(description = "组织类型")
    private String type;
    
    /**
     * 组织描述
     */
    @Schema(description = "组织描述")
    private String description;
    
    /**
     * 组织网站
     */
    @Schema(description = "组织网站")
    private String website;
    
    /**
     * 组织logo
     */
    @Schema(description = "组织logo图片URL")
    private String logoUrl;
    
    /**
     * 组织地址
     */
    @Schema(description = "组织地址")
    private String address;

    /**
     * 纬度
     */
    @Schema(description = "纬度")
    private Double latitude;
    
    /**
     * 经度
     */
    @Schema(description = "经度")
    private Double longitude;
    
    /**
     * 管理员邮箱
     */
    @Schema(description = "管理员邮箱")
    private String adminEmail;
    
    
    
    /**
     * 将Organization实体转换为OrganizationVO
     * 
     * @param organization 组织实体
     * @return 组织视图对象
     */
    public static OrganizationVO fromEntity(Organization organization) {
        if (organization == null) {
            return null;
        }
        
        OrganizationVO vo = new OrganizationVO();
        vo.setId(organization.getId());
        vo.setOrganizationName(organization.getOrganizationName());
        vo.setType(organization.getType());
        vo.setDescription(organization.getDescription());
        vo.setWebsite(organization.getWebsite());
        vo.setLogoUrl(organization.getLogoUrl());
        vo.setAddress(organization.getAddress());
        vo.setLatitude(organization.getLatitude());
        vo.setLongitude(organization.getLongitude());
        // 注意：这里没有设置adminEmail，因为Organization实体中没有这个字段
        // 在从数据库直接查询到OrganizationVO时，adminEmail会被自动映射
        return vo;
    }
    
    /**
     * 将Organization实体转换为OrganizationVO，并设置管理员邮箱
     * 
     * @param organization 组织实体
     * @param adminEmail 管理员邮箱
     * @return 组织视图对象
     */
    public static OrganizationVO fromEntity(Organization organization, String adminEmail) {
        OrganizationVO vo = fromEntity(organization);
        if (vo != null) {
            vo.setAdminEmail(adminEmail);
        }
        return vo;
    }
} 