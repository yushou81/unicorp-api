-- -----------------------------------------------------
-- 数据库物理设计: 校企联盟平台 V2.0 (专业版)
-- 目标: 引入用户简历(Profile)和企业多成员管理，实现向真实招聘平台的演进。
-- -----------------------------------------------------

-- 表: `user` (用户表 - 核心身份)
-- 职责: 保持精简，只负责用户的核心身份认证和基础信息。
CREATE TABLE IF NOT EXISTS `user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` VARCHAR(50) NOT NULL COMMENT '登录用户名, 唯一',
  `password` VARCHAR(100) NOT NULL COMMENT '加密后的密码',
  `nickname` VARCHAR(50) NULL COMMENT '用户昵称',
  `avatar_url` VARCHAR(255) NULL COMMENT '用户头像URL (相对路径)',
  `phone` VARCHAR(20) NULL COMMENT '手机号码, 唯一',
  `status` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '账户状态: 0-正常, 1-锁定',
  `is_deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
  `create_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uk_username` (`username` ASC) VISIBLE,
  UNIQUE INDEX `uk_phone` (`phone` ASC) VISIBLE
) ENGINE = InnoDB COMMENT = '平台用户核心身份表';


-- -----------------------------------------------------
-- 表: `user_profile` (用户简历/档案表) - 【全新设计】
-- 职责: 存储用户的详细求职信息，与user表一对一关联。
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `user_profile` (
  `user_id` BIGINT NOT NULL COMMENT '关联的用户ID, 同时也是主键',
  `full_name` VARCHAR(50) NULL COMMENT '真实姓名',
  `education_level` VARCHAR(50) NULL COMMENT '最高学历, e.g., 本科, 硕士',
  `university` VARCHAR(100) NULL COMMENT '毕业院校',
  `major` VARCHAR(100) NULL COMMENT '所学专业',
  `graduation_year` INT NULL COMMENT '毕业年份',
  `target_job_title` VARCHAR(100) NULL COMMENT '期望职位',
  `target_salary_range` VARCHAR(50) NULL COMMENT '期望薪资范围',
  `resume_url` VARCHAR(255) NULL COMMENT '简历附件URL (相对路径)',
  `self_introduction` TEXT NULL COMMENT '自我介绍',
  `update_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`),
  CONSTRAINT `fk_profile_user`
    FOREIGN KEY (`user_id`)
    REFERENCES `user` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE = InnoDB COMMENT = '用户简历档案表';


-- -----------------------------------------------------
-- 表: `enterprise` (企业信息表 - 重构)
-- 职责: 只存储企业本身的静态资料，与用户解耦。
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `enterprise` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  -- 【重构】: 移除了 user_id，企业不再与单一用户绑定
  `name` VARCHAR(100) NOT NULL COMMENT '企业全称',
  `short_name` VARCHAR(50) NULL COMMENT '企业简称',
  `logo_url` VARCHAR(255) NULL COMMENT '企业Logo (相对路径)',
  `address` VARCHAR(255) NULL COMMENT '企业地址',
  `industry` VARCHAR(50) NULL COMMENT '所属行业',
  `scale` VARCHAR(50) NULL COMMENT '企业规模, e.g., "50-100人"',
  `website_url` VARCHAR(255) NULL COMMENT '官方网站',
  `description` TEXT NULL COMMENT '企业简介',
  `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING_REVIEW' COMMENT '企业状态: PENDING_REVIEW-待审核, APPROVED-已认证',
  `is_deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  `create_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE = InnoDB COMMENT = '企业信息表';


-- -----------------------------------------------------
-- 表: `enterprise_member` (企业成员表) - 【全新设计】
-- 职责: 建立用户和企业的多对多关系，并定义成员角色。
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `enterprise_member` (
  `enterprise_id` BIGINT NOT NULL COMMENT '企业ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `role` VARCHAR(20) NOT NULL COMMENT '成员角色: ADMIN-企业管理员, HR-招聘人员',
  `join_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
  PRIMARY KEY (`enterprise_id`, `user_id`),
  INDEX `idx_user_id` (`user_id` ASC) VISIBLE,
  CONSTRAINT `fk_member_enterprise`
    FOREIGN KEY (`enterprise_id`)
    REFERENCES `enterprise` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_member_user`
    FOREIGN KEY (`user_id`)
    REFERENCES `user` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE = InnoDB COMMENT = '企业成员关系表';


-- -----------------------------------------------------
-- 表: `job_post` (招聘岗位表 - 微调)
-- 职责: 增加了发布人信息，便于追溯。
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `job_post` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `enterprise_id` BIGINT NOT NULL COMMENT '发布岗位的企业ID',
  `posted_by_user_id` BIGINT NOT NULL COMMENT '发布该岗位的HR或管理员ID',
  `title` VARCHAR(100) NOT NULL COMMENT '岗位名称',
  `job_type` VARCHAR(20) NOT NULL COMMENT '工作类型: FULL_TIME, PART_TIME, INTERNSHIP',
  `location` VARCHAR(100) NULL COMMENT '工作地点',
  `salary_range` VARCHAR(50) NULL COMMENT '薪资范围, e.g., "10-15K"',
  `responsibilities` TEXT NULL COMMENT '岗位职责',
  `requirements` TEXT NULL COMMENT '任职要求',
  `status` VARCHAR(20) NOT NULL DEFAULT 'HIRING' COMMENT '招聘状态: HIRING-招聘中, CLOSED-已关闭',
  `is_deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  `create_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_enterprise_id` (`enterprise_id` ASC) VISIBLE,
  INDEX `idx_posted_by_user_id` (`posted_by_user_id` ASC) VISIBLE,
  CONSTRAINT `fk_job_enterprise_v2`
    FOREIGN KEY (`enterprise_id`)
    REFERENCES `enterprise` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_job_user_v2`
    FOREIGN KEY (`posted_by_user_id`)
    REFERENCES `user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
) ENGINE = InnoDB COMMENT = '招聘岗位表';

-- job_application 表保持不变，其设计已足够灵活。
