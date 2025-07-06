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
 * 岗位特征实体类
 * 用于存储岗位的特征向量，供推荐系统使用
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("job_features")
public class JobFeature {
    
    /**
     * 特征ID，自增主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /**
     * 岗位ID
     */
    private Integer jobId;
    
    /**
     * 特征向量（JSON格式存储）
     */
    private String featureVector;
    
    /**
     * 技能要求（JSON数组格式存储）
     */
    private String requiredSkills;
    
    /**
     * 岗位关键词（JSON数组格式存储）
     */
    private String keywords;
    
    /**
     * 岗位分类ID
     */
    private Integer categoryId;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
} 