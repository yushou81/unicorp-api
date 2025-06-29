-- =================================================================
-- 数据库变更脚本 (Database Migration Script)
-- 版本: 1.4
-- 目标: unicorp
-- 说明: 为项目合作模块添加“项目申请”和“项目成员”两张新表。
-- =================================================================

-- 使用目标数据库
-- USE unicorp;

-- 1. 创建项目申请表 (project_applications)
-- -----------------------------------------------------------------
-- 这张表用于记录哪个用户申请了哪个项目，以及申请的状态。
CREATE TABLE `project_applications` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `project_id` INT NOT NULL COMMENT '关联的项目ID',
    `user_id` INT NOT NULL COMMENT '申请人的用户ID',
    `status` ENUM('submitted', 'viewed', 'approved', 'rejected') NOT NULL DEFAULT 'submitted' COMMENT '申请状态',
    `application_statement` TEXT COMMENT '申请陈述或备注',
    `is_deleted` BOOLEAN NOT NULL DEFAULT FALSE,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    -- 确保一个用户对同一个项目只能申请一次
    UNIQUE KEY `uk_project_user` (`project_id`, `user_id`)
) ENGINE=InnoDB COMMENT='项目申请记录表';


-- 2. 创建项目成员表 (project_members)
-- -----------------------------------------------------------------
-- 这张表用于记录一个项目中所有正式的成员及其角色。
CREATE TABLE `project_members` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `project_id` INT NOT NULL COMMENT '关联的项目ID',
    `user_id` INT NOT NULL COMMENT '项目成员的用户ID',
    `role_in_project` VARCHAR(100) COMMENT '成员在项目中的角色 (e.g., 负责人, 核心成员)',
    `is_deleted` BOOLEAN NOT NULL DEFAULT FALSE,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    -- 确保一个用户在一个项目中只能有一个成员身份
    UNIQUE KEY `uk_project_member` (`project_id`, `user_id`)
) ENGINE=InnoDB COMMENT='项目成员关系表';


-- =================================================================
-- 3. 为新表添加外键约束 (Foreign Key Constraints)
-- =================================================================

-- 为 project_applications 表添加外键
ALTER TABLE `project_applications`
ADD CONSTRAINT `fk_projapp_project` FOREIGN KEY (`project_id`) REFERENCES `projects`(`id`) ON DELETE CASCADE,
ADD CONSTRAINT `fk_projapp_user` FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE;

-- 为 project_members 表添加外键
ALTER TABLE `project_members`
ADD CONSTRAINT `fk_projmembers_project` FOREIGN KEY (`project_id`) REFERENCES `projects`(`id`) ON DELETE CASCADE,
ADD CONSTRAINT `fk_projmembers_user` FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE;

-- =================================================================
-- 脚本结束
-- =================================================================
