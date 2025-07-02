package com.csu.unicorp.entity.organization;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 学校详情实体类，对应school_details表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("school_details")
public class SchoolDetail {
    /**
     * 组织ID，主键，关联organizations表
     */
    @TableId
    private Integer organizationId;
    
    /**
     * 学校类型，如公办、民办等
     */
    private String schoolType;
    
    /**
     * 学校办学层次，如本科、硕士、博士等
     */
    private String educationLevels;
} 