package com.csu.unicorp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 岗位与分类的关联实体类
 */
@Data
@TableName("job_category_relations")
public class JobCategoryRelation {

    /**
     * 关联的岗位ID
     */
    @TableField("job_id")
    private Integer jobId;

    /**
     * 关联的分类ID
     */
    @TableField("category_id")
    private Integer categoryId;
} 