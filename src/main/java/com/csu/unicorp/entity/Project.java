package com.csu.unicorp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * 合作项目实体类，对应projects表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("projects")
public class Project {
    /**
     * 项目ID，自增主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /**
     * 所属组织ID
     */
    private Integer organizationId;
    
    /**
     * 项目标题
     */
    private String title;
    
    /**
     * 项目描述
     */
    private String description;
    
    /**
     * 项目状态：recruiting-招募中，in_progress-进行中，completed-已完成
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
    private Timestamp createdAt;

    /**
     * 计划人数
     */
    private Integer planMemberCount;

    /**
     * 项目难度
     */
    private String difficulty;

    /**
     * 支持语言（逗号分隔字符串）
     */
    private String supportLanguages;

    /**
     * 技术领域（逗号分隔字符串）
     */
    private String techFields;

    /**
     * 编程语言（逗号分隔字符串）
     */
    private String programmingLanguages;

    /**
     * 项目计划书文件URL
     */
    private String projectProposalUrl;
} 