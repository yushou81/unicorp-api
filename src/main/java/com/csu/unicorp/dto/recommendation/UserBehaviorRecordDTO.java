package com.csu.unicorp.dto.recommendation;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 用户行为记录DTO类
 * 用于接收用户行为记录请求
 */
@Data
public class UserBehaviorRecordDTO {
    
    /**
     * 行为类型（view-浏览, search-搜索, apply-申请, favorite-收藏）
     */
    @NotBlank(message = "行为类型不能为空")
    private String behaviorType;
    
    /**
     * 目标类型（job-岗位, category-分类）
     */
    @NotBlank(message = "目标类型不能为空")
    private String targetType;
    
    /**
     * 目标ID（如岗位ID、分类ID等）
     */
    @NotNull(message = "目标ID不能为空")
    private Integer targetId;
    
    /**
     * 搜索关键词（当行为类型为search时使用）
     */
    private String searchKeyword;
} 