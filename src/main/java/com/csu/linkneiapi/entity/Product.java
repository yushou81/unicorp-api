package com.csu.linkneiapi.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 产品/服务实体类
 */
@Data
@TableName("product") // 指定该实体类对应数据库中的'product'表
public class Product {

    @TableId(type = IdType.AUTO) // 声明主键，并设置为自增
    private Long id;

    /**
     * 所属商户ID
     */
    private Long merchantId;
    
    /**
     * 产品分类ID (未来可扩展)
     */
    private Long categoryId;

    /**
     * 产品/服务名称
     */
    private String name;

    /**
     * 产品描述
     */
    private String description;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 产品主图地址
     */
    private String imageUrl;

    /**
     * 库存数量
     */
    private Integer stock;
    
    /**
     * 销量, 用于热门推荐排序
     */
    private Integer salesCount;
    
    /**
     * 产品状态: ON_SALE-在售, SOLD_OUT-售罄, OFF_SHELF-已下架
     */
    private String status;
    
    /**
     * 逻辑删除: 0-未删除, 1-已删除
     */
    @TableLogic
    private Integer isDeleted;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    /**
     * 所属商户信息（非数据库字段）
     */
    @TableField(exist = false)
    private Merchant merchantInfo;
} 