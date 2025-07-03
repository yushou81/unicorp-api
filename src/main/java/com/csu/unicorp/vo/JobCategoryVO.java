package com.csu.unicorp.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 岗位分类VO
 */
@Data
@Schema(description = "岗位分类VO")
public class JobCategoryVO {
    
    /**
     * 分类ID
     */
    @Schema(description = "分类ID")
    private Integer id;
    
    /**
     * 分类名称
     */
    @Schema(description = "分类名称")
    private String name;
    
    /**
     * 父级分类ID
     */
    @Schema(description = "父级分类ID")
    private Integer parentId;
    
    /**
     * 层级 (1, 2, 3)
     */
    @Schema(description = "层级 (1, 2, 3)")
    private Integer level;
    
    /**
     * 子分类列表
     */
    @Schema(description = "子分类列表")
    private List<JobCategoryVO> children;
} 