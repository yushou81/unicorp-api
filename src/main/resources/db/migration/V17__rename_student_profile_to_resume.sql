-- =================================================================
-- 数据库变更脚本 (Database Migration Script)
-- 版本: V17
-- 目标: unicorp
-- 说明: 将student_profiles表重命名为resumes，使命名更加直观
-- =================================================================

-- 首先删除外键约束
ALTER TABLE `applications` DROP FOREIGN KEY `fk_applications_resume`;

-- 重命名表
RENAME TABLE `student_profiles` TO `resumes`;

-- 重新添加外键约束，指向新表名
ALTER TABLE `applications`
    ADD CONSTRAINT `fk_applications_resume` 
    FOREIGN KEY (`resume_id`) 
    REFERENCES `resumes`(`id`) 
    ON DELETE SET NULL;

-- =================================================================
-- 脚本结束
-- ================================================================= 