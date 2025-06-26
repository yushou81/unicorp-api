package com.csu.unicorp.entity;

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
 * 招聘岗位实体类，对应jobs表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("jobs")
public class Job {
    /**
     * 岗位ID，自增主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /**
     * 发布组织ID
     */
    private Integer organizationId;
    
    /**
     * 发布用户ID
     */
    private Integer postedByUserId;
    
    /**
     * 岗位标题
     */
    private String title;
    
    /**
     * 岗位描述
     */
    private String description;
    
    /**
     * 工作地点
     */
    private String location;
    
    /**
     * 岗位状态：open-开放，closed-关闭
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
} 