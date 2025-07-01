package com.csu.unicorp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 岗位分类实体类
 */
@Data
@TableName("job_categories")
public class JobCategory {

    /**
     * 分类ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 分类/标签名称
     */
    @TableField("name")
    private String name;

    /**
     * 父级分类的ID，第一级分类此字段为NULL
     */
    @TableField("parent_id")
    private Integer parentId;

    /**
     * 层级 (1, 2, 3)，便于查询
     */
    @TableField("level")
    private Integer level;

    /**
     * 逻辑删除标志
     */
    @TableField("is_deleted")
    @TableLogic
    private Boolean isDeleted;

    /**
     * 创建时间
     */
    @TableField("created_at")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField("updated_at")
    private LocalDateTime updatedAt;
} 