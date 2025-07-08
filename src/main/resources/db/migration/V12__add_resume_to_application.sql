-- =================================================================
-- 数据库变更脚本 (Database Migration Script)
-- 版本: V16
-- 目标: unicorp
-- 说明: 为岗位申请表添加简历关联字段，使企业导师能够查看申请者的简历
-- =================================================================

-- 为applications表添加resume_id字段
ALTER TABLE `applications`
    ADD COLUMN `resume_id` INT NULL COMMENT '关联的简历ID' AFTER `student_id`;

-- 添加外键约束，关联到student_profiles表
ALTER TABLE `applications`
    ADD CONSTRAINT `fk_applications_resume` 
    FOREIGN KEY (`resume_id`) 
    REFERENCES `student_profiles`(`id`) 
    ON DELETE SET NULL;

-- 添加索引以提高查询性能
CREATE INDEX `idx_applications_resume_id` ON `applications` (`resume_id`);

-- =================================================================
-- 脚本结束
-- ================================================================= 