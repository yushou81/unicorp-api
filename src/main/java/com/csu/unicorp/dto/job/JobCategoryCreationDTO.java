package com.csu.unicorp.dto.job;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 岗位分类创建DTO
 */
@Data
@Schema(description = "岗位分类创建DTO")
public class JobCategoryCreationDTO {

    /**
     * 分类名称
     */
    @Schema(description = "分类名称", example = "技术")
    @NotBlank(message = "分类名称不能为空")
    @Size(min = 1, max = 100, message = "分类名称长度必须在1-100之间")
    private String name;

    /**
     * 父分类ID，如果为null则表示顶级分类
     */
    @Schema(description = "父分类ID，如果为null则表示顶级分类", example = "1", nullable = true)
    private Integer parentId;
} 