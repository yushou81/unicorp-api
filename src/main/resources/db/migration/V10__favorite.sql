-- =================================================================
-- 数据库变更脚本 (Database Migration Script)
-- 版本: 1.8
-- 目标: unicorp
-- 说明: 创建 job_favorites 表，用于学生收藏招聘岗位。
-- =================================================================

-- 使用目标数据库
-- USE unicorp;

-- -----------------------------------------------------------------
-- 创建 job_favorites (岗位收藏) 表
-- -----------------------------------------------------------------
-- 这张表用于记录学生用户收藏的招聘岗位，是学生和岗位之间的多对多关系。
CREATE TABLE `job_favorites` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `user_id` INT NOT NULL COMMENT '收藏该岗位的学生用户ID',
    `job_id` INT NOT NULL COMMENT '被收藏的岗位ID',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
    -- 确保一个学生对同一个岗位只能收藏一次
    UNIQUE KEY `uk_user_job` (`user_id`, `job_id`),
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`job_id`) REFERENCES `jobs`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB COMMENT='岗位收藏表';

-- =================================================================
-- 脚本结束
-- =================================================================
