-- 添加用户头像字段
ALTER TABLE users
ADD COLUMN avatar_url VARCHAR(255) DEFAULT NULL COMMENT '用户头像URL'; 
-- =================================================================
-- 数据库变更脚本 (Database Migration Script)
-- 版本: 1.4
-- 目标: unicorp
-- 说明: 引入多简历管理功能。
-- 1. 创建新的 resumes 表。
-- 2. 从 student_profiles 表中移除旧的 resume_url 字段。
-- 3. 修改 applications 表以关联到特定的简历。
-- =================================================================

-- 使用目标数据库
-- USE unicorp;

-- -----------------------------------------------------------------
-- 步骤 1: 创建新的 `resumes` 表
-- -----------------------------------------------------------------
-- 这张表将允许每个学生用户存储多份不同的简历。
CREATE TABLE `resumes` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `user_id` INT NOT NULL COMMENT '所属学生的用户ID',
    `resume_name` VARCHAR(255) NOT NULL COMMENT '简历名称 (e.g., 我的Java后端简历)',
    `file_url` VARCHAR(255) NOT NULL COMMENT '简历文件的URL',
    `is_default` BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否为默认简历',
    `is_deleted` BOOLEAN NOT NULL DEFAULT FALSE,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB COMMENT='学生简历表';

-- 为新表添加索引
CREATE INDEX `idx_resumes_user_id` ON `resumes`(`user_id`);

-- -----------------------------------------------------------------
-- 步骤 2: 从 `student_profiles` 表中移除旧的 `resume_url` 字段
-- -----------------------------------------------------------------
-- 由于简历现在由新的 resumes 表统一管理，旧的字段需要被移除。
ALTER TABLE `student_profiles`
DROP COLUMN `resume_url`;

-- -----------------------------------------------------------------
-- 步骤 3: 修改 `applications` 表以记录使用的简历
-- -----------------------------------------------------------------
-- 为岗位申请表添加一个 resume_id 字段，以精确记录每次投递所使用的简历。
ALTER TABLE `applications`
ADD COLUMN `resume_id` INT NOT NULL COMMENT '本次申请使用的简历ID' AFTER `student_id`;

-- 为新字段添加外键约束
ALTER TABLE `applications`
ADD CONSTRAINT `fk_applications_resume` FOREIGN KEY (`resume_id`) REFERENCES `resumes`(`id`) ON DELETE RESTRICT;


-- =================================================================
-- 脚本结束
-- =================================================================
