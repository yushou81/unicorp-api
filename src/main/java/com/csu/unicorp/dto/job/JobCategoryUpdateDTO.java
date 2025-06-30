package com.csu.unicorp.dto.job;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 岗位分类更新DTO
 */
@Data
@Schema(description = "岗位分类更新DTO")
public class JobCategoryUpdateDTO {

    /**
     * 分类名称
     */
    @Schema(description = "分类名称", example = "技术", nullable = true)
    @Size(min = 1, max = 100, message = "分类名称长度必须在1-100之间")
    private String name;
} 