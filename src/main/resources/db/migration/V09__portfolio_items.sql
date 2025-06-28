-- =================================================================
-- 数据库变更脚本 (Database Migration Script)
-- 版本: 1.5
-- 目标: unicorp
-- 说明: 创建 portfolio_items 表，用于学生管理其个人作品集。
-- =================================================================

-- 使用目标数据库
-- USE unicorp;

-- -----------------------------------------------------------------
-- 创建 portfolio_items (作品集项目) 表
-- -----------------------------------------------------------------
-- 这张表用于存储学生展示在个人主页上的每一个独立作品或成就。
CREATE TABLE `portfolio_items` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `user_id` INT NOT NULL COMMENT '所属学生的用户ID',
    `title` VARCHAR(255) NOT NULL COMMENT '作品或项目标题',
    `description` TEXT COMMENT '详细描述',
    `project_url` VARCHAR(255) COMMENT '项目链接 (如GitHub、在线演示地址)',
    `cover_image_url` VARCHAR(255) COMMENT '封面图URL',
    `is_deleted` BOOLEAN NOT NULL DEFAULT FALSE,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB COMMENT='学生作品集项目表';

-- 为新表添加索引
-- 这个索引至关重要，当查询某个学生的所有作品时，可以极大地提升性能。
CREATE INDEX `idx_portfolio_items_user_id` ON `portfolio_items`(`user_id`);


-- =================================================================
-- 脚本结束
-- =================================================================
