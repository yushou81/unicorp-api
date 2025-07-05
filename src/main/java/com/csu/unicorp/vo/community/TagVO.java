package com.csu.unicorp.vo.community;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 社区标签视图对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "社区标签视图对象")
public class TagVO {
    
    /**
     * 标签ID
     */
    @Schema(description = "标签ID", example = "1")
    private Long id;
    
    /**
     * 标签名称
     */
    @Schema(description = "标签名称", example = "Java")
    private String name;
    
    /**
     * 标签描述
     */
    @Schema(description = "标签描述", example = "Java编程语言相关讨论")
    private String description;
    
    /**
     * 使用次数
     */
    @Schema(description = "使用次数", example = "100")
    private Integer usageCount;
    
    /**
     * 创建时间
     */
    @Schema(description = "创建时间", example = "2023-01-01 12:00:00")
    private LocalDateTime createdAt;
} 