package com.csu.linkneiapi.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
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

    /**
     * 登录用户名，唯一
     */
    private String username;

    /**
     * 加密后的密码
     */
    private String password;
    
    /**
     * 用户昵称
     */
    private String nickname;
    
    /**
     * 用户头像URL
     */
    private String avatarUrl;
    
    /**
     * 手机号码，唯一
     */
    private String phone;
    
    /**
     * 账户状态: 0-正常, 1-锁定
     */
    private Integer status;
    
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
     * 关联的用户档案（非数据库字段）
     */
    @TableField(exist = false)
    private UserProfile profile;
}
