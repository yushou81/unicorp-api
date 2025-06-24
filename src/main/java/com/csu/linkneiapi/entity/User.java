package com.csu.linkneiapi.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
@TableName("user") // 指定该实体类对应数据库中的'user'表
public class User {

    @TableId(type = IdType.AUTO) // 声明主键，并设置为自增
    private Long id;

    private String username;

    private String password;

    // 如果数据库字段与实体属性名不一致，可以使用@TableField注解
    // @TableField("create_time")

    // 配置创建时间自动填充
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    // 配置更新时间自动填充
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    // 逻辑删除字段 (0-未删除, 1-已删除)，MyBatis-Plus会自动处理
    // 在yml中我们已配置过，所以这里只需要注解即可
    // @TableLogic
    // private Integer isDeleted;
}
