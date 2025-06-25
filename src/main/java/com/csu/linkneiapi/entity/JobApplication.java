package com.csu.linkneiapi.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 投递记录实体类
 * 记录用户对岗位的投递行为，是招聘流程的核心
 */
@Data
@TableName("job_application")
public class JobApplication {

    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 投递用户的ID
     */
    private Long userId;
    
    /**
     * 被投递的岗位ID
     */
    private Long jobPostId;
    
    /**
     * 投递状态: SUBMITTED-已投递, VIEWED-已查看, INTERVIEWING-面试中, OFFERED-已录用, REJECTED-不合适
     */
    private String status;
    
    /**
     * 投递时间
     */
    private LocalDateTime applicationTime;
    
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    /**
     * 申请人信息（非数据库字段）
     */
    @TableField(exist = false)
    private User user;
    
    /**
     * 申请人简历（非数据库字段）
     */
    @TableField(exist = false)
    private UserProfile userProfile;
    
    /**
     * 申请的职位信息（非数据库字段）
     */
    @TableField(exist = false)
    private JobPost jobPost;
} 