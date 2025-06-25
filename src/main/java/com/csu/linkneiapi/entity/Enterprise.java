package com.csu.linkneiapi.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 企业信息实体类
 */
@Data
@TableName("enterprise")
public class Enterprise {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 企业全称
     */
    private String name;
    
    /**
     * 企业简称
     */
    private String shortName;

    /**
     * 企业Logo (相对路径)
     */
    private String logoUrl;

    /**
     * 企业地址
     */
    private String address;

    /**
     * 所属行业
     */
    private String industry;
    
    /**
     * 企业规模, e.g., "50-100人"
     */
    private String scale;
    
    /**
     * 官方网站
     */
    private String websiteUrl;

    /**
     * 企业简介
     */
    private String description;

    /**
     * 企业状态: PENDING_REVIEW-待审核, APPROVED-已认证
     */
    private String status;
    
    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer isDeleted;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    /**
     * 企业成员列表（非数据库字段）
     */
    @TableField(exist = false)
    private List<EnterpriseMember> members;
    
    /**
     * 企业岗位列表（非数据库字段）
     */
    @TableField(exist = false)
    private List<JobPost> jobPosts;
} 