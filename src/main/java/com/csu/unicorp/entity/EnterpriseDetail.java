package com.csu.unicorp.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 企业详情实体类，对应enterprise_details表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("enterprise_details")
public class EnterpriseDetail {
    /**
     * 组织ID，主键，关联organizations表
     */
    @TableId
    private Integer organizationId;
    
    /**
     * 所属行业，如IT、金融等
     */
    private String industry;
    
    /**
     * 公司规模，如1-50人等
     */
    private String companySize;
    
    /**
     * 营业执照图片URL
     */
    private String businessLicenseUrl;
} 