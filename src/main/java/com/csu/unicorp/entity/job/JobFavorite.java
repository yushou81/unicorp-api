package com.csu.unicorp.entity.job;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * 岗位收藏实体类
 */
@Data
@TableName("job_favorites")
public class JobFavorite {
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /**
     * 收藏该岗位的学生用户ID
     */
    private Integer userId;
    
    /**
     * 被收藏的岗位ID
     */
    private Integer jobId;
    
    /**
     * 收藏时间
     */
    private LocalDateTime createdAt;
} 