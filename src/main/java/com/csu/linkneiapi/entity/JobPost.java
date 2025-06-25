package com.csu.linkneiapi.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 招聘岗位实体类
 */
@Data
@TableName("job_post")
public class JobPost {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 发布岗位的企业ID
     */
    private Long enterpriseId;
    
    /**
     * 发布该岗位的HR或管理员ID
     */
    private Long postedByUserId;

    /**
     * 岗位名称
     */
    private String title;
    
    /**
     * 工作类型: FULL_TIME, PART_TIME, INTERNSHIP
     */
    private String jobType;
    
    /**
     * 工作地点
     */
    private String location;
    
    /**
     * 薪资范围, e.g., "10-15K"
     */
    private String salaryRange;
    
    /**
     * 岗位职责
     */
    private String responsibilities;
    
    /**
     * 任职要求
     */
    private String requirements;
    
    /**
     * 招聘状态: HIRING-招聘中, CLOSED-已关闭
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
     * 发布企业信息（非数据库字段）
     */
    @TableField(exist = false)
    private Enterprise enterprise;
    
    /**
     * 发布人信息（非数据库字段）
     */
    @TableField(exist = false)
    private User postedByUser;
} 