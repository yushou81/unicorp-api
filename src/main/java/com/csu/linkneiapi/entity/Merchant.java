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
import java.util.List;

/**
 * 商户信息实体类
 */
@Data
@TableName("merchant") // 指定该实体类对应数据库中的'merchant'表
public class Merchant {

    @TableId(type = IdType.AUTO) // 声明主键，并设置为自增
    private Long id;

    /**
     * 关联的用户ID
     */
    private Long userId;

    /**
     * 商户/店铺名称
     */
    private String name;

    /**
     * 商户地址
     */
    private String address;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 商户简介/描述
     */
    private String description;

    /**
     * 商户Logo图片地址
     */
    private String logoUrl;
    
    /**
     * 营业时间, e.g., "10:00-22:00"
     */
    private String businessHours;
    
    /**
     * 平均评分, 用于列表页快速展示和排序
     */
    private BigDecimal averageRating;
    
    /**
     * 纬度, 用于地图定位
     */
    private BigDecimal latitude;
    
    /**
     * 经度, 用于地图定位
     */
    private BigDecimal longitude;
    
    /**
     * 商户状态: PENDING_REVIEW-待审核, OPEN-营业中, CLOSED-已关闭, REJECTED-已拒绝
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
     * 关联的用户信息（非数据库字段）
     */
    @TableField(exist = false)
    private User userInfo;
    
    /**
     * 商户的产品列表（非数据库字段）
     */
    @TableField(exist = false)
    private List<Product> products;
} 