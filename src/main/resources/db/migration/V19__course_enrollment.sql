-- =================================================================
-- 双师课堂选课表 (Course Enrollment)
-- =================================================================

-- 创建课程报名表
CREATE TABLE `course_enrollments` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `course_id` INT NOT NULL COMMENT '关联的双师课堂ID',
    `student_id` INT NOT NULL COMMENT '学生ID',
    `status` ENUM('enrolled', 'cancelled', 'completed') NOT NULL DEFAULT 'enrolled' COMMENT '选课状态',
    `enrollment_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '报名时间',
    `is_deleted` BOOLEAN NOT NULL DEFAULT FALSE COMMENT '逻辑删除标志'
) ENGINE=InnoDB COMMENT='双师课堂学生选课记录表';

-- 添加外键约束
ALTER TABLE `course_enrollments` ADD CONSTRAINT `fk_enrollment_course` 
    FOREIGN KEY (`course_id`) REFERENCES `dual_teacher_courses`(`id`) ON DELETE CASCADE;
    
ALTER TABLE `course_enrollments` ADD CONSTRAINT `fk_enrollment_student` 
    FOREIGN KEY (`student_id`) REFERENCES `users`(`id`) ON DELETE CASCADE;
    
-- 添加唯一约束，防止学生重复选课
ALTER TABLE `course_enrollments` ADD UNIQUE KEY `uk_student_course` (`student_id`, `course_id`);

-- 为双师课堂表添加人数限制和课程地点字段
ALTER TABLE `dual_teacher_courses` 
    ADD COLUMN `max_students` INT DEFAULT 50 COMMENT '最大学生人数',
    ADD COLUMN `location` VARCHAR(255) COMMENT '课程地点',
    ADD COLUMN `course_type` ENUM('online', 'offline', 'hybrid') NOT NULL DEFAULT 'offline' COMMENT '课程类型',
    ADD COLUMN `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    ADD COLUMN `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    ADD COLUMN `status` ENUM('planning', 'open', 'in_progress', 'completed', 'cancelled') NOT NULL DEFAULT 'planning' COMMENT '课程状态'; 