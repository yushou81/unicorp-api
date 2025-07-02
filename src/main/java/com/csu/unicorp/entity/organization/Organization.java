package com.csu.unicorp.entity.organization;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 组织实体类，对应organizations表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("organizations")
public class Organization {
    /**
     * 组织ID，自增主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /**
     * 组织名称
     */
    private String organizationName;
    
    /**
     * 组织类型：School-学校，Enterprise-企业
     */
    private String type;
    
    /**
     * 组织描述
     */
    private String description;
    
    /**
     * 组织地址
     */
    private String address;
    
    /**
     * 组织网站
     */
    private String website;
    
    /**
     * 组织logo图片相对路径
     */
    private String logoUrl;
    
    /**
     * 审核状态：approved-已审核，pending-待审核
     */
    private String status;
    
    /**
     * 逻辑删除标志
     */
    @TableLogic
    private Boolean isDeleted;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
} 