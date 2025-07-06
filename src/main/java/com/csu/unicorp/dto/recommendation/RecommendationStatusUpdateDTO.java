package com.csu.unicorp.dto.recommendation;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 推荐状态更新DTO类
 * 用于接收推荐状态更新请求
 */
@Data
public class RecommendationStatusUpdateDTO {
    
    /**
     * 新状态
     * 岗位推荐状态：viewed-已查看, ignored-已忽略, applied-已申请
     * 人才推荐状态：viewed-已查看, contacted-已联系, ignored-已忽略
     */
    @NotBlank(message = "状态不能为空")
    private String status;
} 