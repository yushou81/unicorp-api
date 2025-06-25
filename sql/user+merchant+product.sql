-- -----------------------------------------------------
-- 数据库物理设计: 迭代一
-- 目标: 创建支持用户、商户、产品核心功能的数据表
-- 数据库: linknei_db
-- -----------------------------------------------------

-- -----------------------------------------------------
-- 表: `user` (用户表)
-- 职责: 存储所有用户的基本信息，包括个人用户和商户用户
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名, 唯一',
  `password` VARCHAR(100) NOT NULL COMMENT '加密后的密码',
  `role` VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '用户角色: USER, MERCHANT, ADMIN',
  `create_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uk_username` (`username` ASC) VISIBLE)
ENGINE = InnoDB
COMMENT = '用户信息表';


-- -----------------------------------------------------
-- 表: `merchant` (商户信息表)
-- 职责: 存储商户的详细信息，与用户表通过user_id进行一对一关联
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `merchant` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '关联的用户ID',
  `name` VARCHAR(100) NOT NULL COMMENT '商户/店铺名称',
  `address` VARCHAR(255) NULL COMMENT '商户地址',
  `phone` VARCHAR(20) NULL COMMENT '联系电话',
  `description` TEXT NULL COMMENT '商户简介/描述',
  `logo_url` VARCHAR(255) NULL COMMENT '商户Logo图片地址',
  `create_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uk_user_id` (`user_id` ASC) VISIBLE,
  CONSTRAINT `fk_merchant_user`
    FOREIGN KEY (`user_id`)
    REFERENCES `user` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
COMMENT = '商户信息表';


-- -----------------------------------------------------
-- 表: `product` (产品/服务表)
-- 职责: 存储商户发布的具体产品或服务
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `product` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `merchant_id` BIGINT NOT NULL COMMENT '所属商户ID',
  `name` VARCHAR(100) NOT NULL COMMENT '产品/服务名称',
  `description` TEXT NULL COMMENT '产品描述',
  `price` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '价格',
  `image_url` VARCHAR(255) NULL COMMENT '产品主图地址',
  `stock` INT NULL DEFAULT 0 COMMENT '库存数量',
  `create_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_merchant_id` (`merchant_id` ASC) VISIBLE,
  CONSTRAINT `fk_product_merchant`
    FOREIGN KEY (`merchant_id`)
    REFERENCES `merchant` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
COMMENT = '产品/服务表';