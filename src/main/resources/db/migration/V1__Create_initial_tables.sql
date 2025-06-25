-- Flyway Migration: Version 1
-- 描述: 创建项目初始的核心表结构，包括user, merchant, 和 product表。

-- 表: `user` (用户表)
CREATE TABLE `user` (
                        `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                        `username` VARCHAR(50) NOT NULL COMMENT '用户名, 唯一',
                        `password` VARCHAR(100) NOT NULL COMMENT '加密后的密码',
                        `nickname` VARCHAR(50) NULL COMMENT '用户昵称, 用于显示',
                        `avatar_url` VARCHAR(255) NULL COMMENT '用户头像地址',
                        `phone` VARCHAR(20) NULL COMMENT '手机号码, 可用于登录或通知',
                        `role` VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '用户角色: USER, MERCHANT, ADMIN',
                        `status` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '账户状态: 0-正常, 1-锁定, 2-已注销',
                        `last_login_time` DATETIME NULL COMMENT '最后登录时间',
                        `is_deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
                        `create_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                        `update_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                        PRIMARY KEY (`id`),
                        UNIQUE INDEX `uk_username` (`username` ASC) VISIBLE,
                        UNIQUE INDEX `uk_phone` (`phone` ASC) VISIBLE
) ENGINE = InnoDB COMMENT = '用户信息表';

-- 表: `merchant` (商户信息表)
CREATE TABLE `merchant` (
                            `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                            `user_id` BIGINT NOT NULL COMMENT '关联的用户ID',
                            `name` VARCHAR(100) NOT NULL COMMENT '商户/店铺名称',
                            `address` VARCHAR(255) NULL COMMENT '商户地址',
                            `phone` VARCHAR(20) NULL COMMENT '联系电话',
                            `description` TEXT NULL COMMENT '商户简介/描述',
                            `logo_url` VARCHAR(255) NULL COMMENT '商户Logo图片地址',
                            `business_hours` VARCHAR(100) NULL COMMENT '营业时间, e.g., "10:00-22:00"',
                            `average_rating` DECIMAL(3,2) NOT NULL DEFAULT 0.00 COMMENT '平均评分, 用于列表页快速展示和排序',
                            `latitude` DECIMAL(10,8) NULL COMMENT '纬度, 用于地图定位',
                            `longitude` DECIMAL(11,8) NULL COMMENT '经度, 用于地图定位',
                            `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING_REVIEW' COMMENT '商户状态: PENDING_REVIEW-待审核, OPEN-营业中, CLOSED-已关闭, REJECTED-已拒绝',
                            `is_deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
                            `create_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `update_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                            PRIMARY KEY (`id`),
                            UNIQUE INDEX `uk_user_id` (`user_id` ASC) VISIBLE,
                            CONSTRAINT `fk_merchant_user`
                                FOREIGN KEY (`user_id`)
                                    REFERENCES `user` (`id`)
                                    ON DELETE CASCADE
                                    ON UPDATE CASCADE
) ENGINE = InnoDB COMMENT = '商户信息表';

-- 表: `product` (产品/服务表)
CREATE TABLE `product` (
                           `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                           `merchant_id` BIGINT NOT NULL COMMENT '所属商户ID',
                           `category_id` BIGINT NULL COMMENT '产品分类ID (未来可扩展)',
                           `name` VARCHAR(100) NOT NULL COMMENT '产品/服务名称',
                           `description` TEXT NULL COMMENT '产品描述',
                           `price` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '价格',
                           `image_url` VARCHAR(255) NULL COMMENT '产品主图地址',
                           `stock` INT NULL DEFAULT 0 COMMENT '库存数量',
                           `sales_count` INT NOT NULL DEFAULT 0 COMMENT '销量, 用于热门推荐排序',
                           `status` VARCHAR(20) NOT NULL DEFAULT 'ON_SALE' COMMENT '产品状态: ON_SALE-在售, SOLD_OUT-售罄, OFF_SHELF-已下架',
                           `is_deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
                           `create_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                           `update_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                           PRIMARY KEY (`id`),
                           INDEX `idx_merchant_id` (`merchant_id` ASC) VISIBLE,
                           CONSTRAINT `fk_product_merchant`
                               FOREIGN KEY (`merchant_id`)
                                   REFERENCES `merchant` (`id`)
                                   ON DELETE CASCADE
                                   ON UPDATE CASCADE
) ENGINE = InnoDB COMMENT = '产品/服务表';