package com.csu.unicorp.entity.recommendation;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 岗位推荐实体类
 * 用于存储系统为学生推荐的岗位信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("job_recommendations")
public class JobRecommendation {
    
    /**
     * 推荐ID，自增主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /**
     * 被推荐的学生用户ID
     */
    private Integer userId;
    
    /**
     * 推荐的岗位ID
     */
    private Integer jobId;
    
    /**
     * 推荐分数，用于排序（分数越高表示匹配度越高）
     */
    private Double score;
    
    /**
     * 推荐原因
     */
    private String reason;
    
    /**
     * 推荐状态（viewed-已查看, ignored-已忽略, applied-已申请）
     */
    private String status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
} 