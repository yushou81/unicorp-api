-- =================================================================
-- 数据库名称: unicorp
-- 项目名称: 校企联盟平台
-- 版本: 1.3 (引入实名认证与账户/昵称分离)
-- 说明: 本脚本用于创建完整的数据库、表结构及约束。
-- =================================================================

-- =================================================================
-- 1. 表结构创建 (Table Creation)
-- =================================================================

-- 1.1 组织实体 (Organization Entities)
-- 组织 (超类型)
CREATE TABLE `organizations` (
                                 `id` INT AUTO_INCREMENT PRIMARY KEY,
                                 `organization_name` VARCHAR(255) NOT NULL,
                                 `type` ENUM('School', 'Enterprise') NOT NULL COMMENT '组织类型',
                                 `description` TEXT,
                                 `address` VARCHAR(255),
                                 `website` VARCHAR(255),
                                 `status` ENUM('approved', 'pending') NOT NULL DEFAULT 'pending' COMMENT '审核状态',
                                 `is_deleted` BOOLEAN NOT NULL DEFAULT FALSE COMMENT '逻辑删除标志',
                                 `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                 `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                 UNIQUE (`organization_name`)
) ENGINE=InnoDB COMMENT='组织信息主表 (超类型)';

-- 学校详情 (子类型)
CREATE TABLE `school_details` (
                                  `organization_id` INT PRIMARY KEY,
                                  `school_type` VARCHAR(100) COMMENT '办学类型: e.g., 公办, 民办',
                                  `education_levels` VARCHAR(255) COMMENT '办学层次, e.g., 本科,硕士,博士'
) ENGINE=InnoDB COMMENT='学校专属信息表 (子类型)';

-- 企业详情 (子类型)
CREATE TABLE `enterprise_details` (
                                      `organization_id` INT PRIMARY KEY,
                                      `industry` VARCHAR(100) COMMENT '所属行业: e.g., IT, 金融',
                                      `company_size` VARCHAR(50) COMMENT '公司规模: e.g., 1-50人'
) ENGINE=InnoDB COMMENT='企业专属信息表 (子类型)';

-- 1.2 用户与权限实体 (User and Permission Entities)
-- 用户核心认证表
CREATE TABLE `users` (
                         `id` INT AUTO_INCREMENT PRIMARY KEY,
                         `account` VARCHAR(100) NOT NULL COMMENT '登录账号,唯一',
                         `password` VARCHAR(255) NOT NULL COMMENT '存储加密后的密码',
                         `email` VARCHAR(255) NOT NULL,
                         `phone` VARCHAR(20),
                         `nickname` VARCHAR(100),
                         `status` ENUM('active', 'inactive', 'pending_approval') NOT NULL DEFAULT 'pending_approval',
                         `organization_id` INT,
                         `is_deleted` BOOLEAN NOT NULL DEFAULT FALSE,
                         `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         UNIQUE (`account`),
                         UNIQUE (`email`)
) ENGINE=InnoDB COMMENT='用户核心认证与基本信息表';

-- 用户实名认证信息表 (敏感数据隔离)
CREATE TABLE `user_verifications` (
                                      `user_id` INT PRIMARY KEY,
                                      `real_name` VARCHAR(100) NOT NULL COMMENT '真实姓名',
                                      `id_card` VARCHAR(255) NOT NULL COMMENT '身份证号 (应加密存储)',
                                      `verification_status` ENUM('unverified', 'pending', 'verified', 'failed') NOT NULL DEFAULT 'unverified' COMMENT '实名认证状态',
                                      `verified_at` TIMESTAMP NULL
) ENGINE=InnoDB COMMENT='用户实名认证信息表';


-- 角色
CREATE TABLE `roles` (
                         `id` INT AUTO_INCREMENT PRIMARY KEY,
                         `role_name` VARCHAR(50) NOT NULL,
                         UNIQUE (`role_name`)
) ENGINE=InnoDB COMMENT='角色表';

-- 用户与角色的中间表
CREATE TABLE `user_roles` (
                              `user_id` INT NOT NULL,
                              `role_id` INT NOT NULL,
                              PRIMARY KEY (`user_id`, `role_id`)
) ENGINE=InnoDB COMMENT='用户与角色的多对多关系表';

-- 权限
CREATE TABLE `permissions` (
                               `id` INT AUTO_INCREMENT PRIMARY KEY,
                               `permission_name` VARCHAR(100) NOT NULL,
                               `description` TEXT,
                               UNIQUE (`permission_name`)
) ENGINE=InnoDB COMMENT='权限定义表';

-- 角色与权限的中间表
CREATE TABLE `role_permissions` (
                                    `role_id` INT NOT NULL,
                                    `permission_id` INT NOT NULL,
                                    PRIMARY KEY (`role_id`, `permission_id`)
) ENGINE=InnoDB COMMENT='角色与权限的多对多关系表';

-- 学生档案
CREATE TABLE `student_profiles` (
                                    `id` INT AUTO_INCREMENT PRIMARY KEY,
                                    `user_id` INT NOT NULL,
                                    `major` VARCHAR(100),
                                    `education_level` VARCHAR(50),
                                    `resume_url` VARCHAR(255),
                                    `achievements` TEXT,
                                    UNIQUE (`user_id`)
) ENGINE=InnoDB COMMENT='学生扩展信息表';

-- 1.3 业务核心实体 (Business Core Entities)
-- 招聘岗位
CREATE TABLE `jobs` (
                        `id` INT AUTO_INCREMENT PRIMARY KEY,
                        `organization_id` INT NOT NULL,
                        `posted_by_user_id` INT NOT NULL,
                        `title` VARCHAR(255) NOT NULL,
                        `description` TEXT NOT NULL,
                        `location` VARCHAR(255),
                        `status` ENUM('open', 'closed') NOT NULL DEFAULT 'open',
                        `is_deleted` BOOLEAN NOT NULL DEFAULT FALSE,
                        `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB COMMENT='招聘岗位信息表';

-- 岗位申请
CREATE TABLE `applications` (
                                `id` INT AUTO_INCREMENT PRIMARY KEY,
                                `job_id` INT NOT NULL,
                                `student_id` INT NOT NULL,
                                `status` ENUM('submitted', 'viewed', 'interviewing', 'offered', 'rejected') NOT NULL DEFAULT 'submitted',
                                `applied_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB COMMENT='岗位申请记录表';

-- 合作项目
CREATE TABLE `projects` (
                            `id` INT AUTO_INCREMENT PRIMARY KEY,
                            `organization_id` INT NOT NULL,
                            `title` VARCHAR(255) NOT NULL,
                            `description` TEXT,
                            `status` ENUM('recruiting', 'in_progress', 'completed') NOT NULL DEFAULT 'recruiting',
                            `is_deleted` BOOLEAN NOT NULL DEFAULT FALSE,
                            `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB COMMENT='校企合作项目表';

-- 共享资源
CREATE TABLE `resources` (
                             `id` INT AUTO_INCREMENT PRIMARY KEY,
                             `uploaded_by_user_id` INT NOT NULL,
                             `title` VARCHAR(255) NOT NULL,
                             `description` TEXT,
                             `resource_type` VARCHAR(50) COMMENT 'e.g., document, video, equipment',
                             `file_url` VARCHAR(255),
                             `visibility` ENUM('public') DEFAULT 'public' COMMENT '可见性, 暂时只支持public',
                             `is_deleted` BOOLEAN NOT NULL DEFAULT FALSE,
                             `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB COMMENT='共享资源表';

-- 双师课堂
CREATE TABLE `dual_teacher_courses` (
                                        `id` INT AUTO_INCREMENT PRIMARY KEY,
                                        `title` VARCHAR(255) NOT NULL,
                                        `description` TEXT,
                                        `teacher_id` INT,
                                        `mentor_id` INT,
                                        `scheduled_time` DATETIME,
                                        `is_deleted` BOOLEAN NOT NULL DEFAULT FALSE
) ENGINE=InnoDB COMMENT='双师课堂信息表';

-- 系统日志
CREATE TABLE `audit_logs` (
                              `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
                              `user_id` INT,
                              `action` VARCHAR(255) NOT NULL,
                              `timestamp` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              `details` TEXT
) ENGINE=InnoDB COMMENT='系统操作审计日志表';


-- =================================================================
-- 2. 外键约束创建 (Foreign Key Constraints)
-- =================================================================

-- 组织子类型外键
ALTER TABLE `school_details` ADD CONSTRAINT `fk_schooldetails_org` FOREIGN KEY (`organization_id`) REFERENCES `organizations`(`id`) ON DELETE CASCADE;
ALTER TABLE `enterprise_details` ADD CONSTRAINT `fk_enterprisedetails_org` FOREIGN KEY (`organization_id`) REFERENCES `organizations`(`id`) ON DELETE CASCADE;

-- 用户相关外键
ALTER TABLE `users` ADD CONSTRAINT `fk_users_org` FOREIGN KEY (`organization_id`) REFERENCES `organizations`(`id`) ON DELETE SET NULL;
ALTER TABLE `user_verifications` ADD CONSTRAINT `fk_userverifications_user` FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE;
ALTER TABLE `user_roles` ADD CONSTRAINT `fk_userroles_user` FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE;
ALTER TABLE `user_roles` ADD CONSTRAINT `fk_userroles_role` FOREIGN KEY (`role_id`) REFERENCES `roles`(`id`) ON DELETE CASCADE;
ALTER TABLE `role_permissions` ADD CONSTRAINT `fk_rolepermissions_role` FOREIGN KEY (`role_id`) REFERENCES `roles`(`id`) ON DELETE CASCADE;
ALTER TABLE `role_permissions` ADD CONSTRAINT `fk_rolepermissions_permission` FOREIGN KEY (`permission_id`) REFERENCES `permissions`(`id`) ON DELETE CASCADE;
ALTER TABLE `student_profiles` ADD CONSTRAINT `fk_studentprofiles_user` FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE;

-- 业务实体相关外键
ALTER TABLE `jobs` ADD CONSTRAINT `fk_jobs_org` FOREIGN KEY (`organization_id`) REFERENCES `organizations`(`id`) ON DELETE CASCADE;
ALTER TABLE `jobs` ADD CONSTRAINT `fk_jobs_user` FOREIGN KEY (`posted_by_user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE;
ALTER TABLE `applications` ADD CONSTRAINT `fk_applications_job` FOREIGN KEY (`job_id`) REFERENCES `jobs`(`id`) ON DELETE CASCADE;
ALTER TABLE `applications` ADD CONSTRAINT `fk_applications_student` FOREIGN KEY (`student_id`) REFERENCES `users`(`id`) ON DELETE CASCADE;
ALTER TABLE `projects` ADD CONSTRAINT `fk_projects_org` FOREIGN KEY (`organization_id`) REFERENCES `organizations`(`id`) ON DELETE CASCADE;
ALTER TABLE `resources` ADD CONSTRAINT `fk_resources_user` FOREIGN KEY (`uploaded_by_user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE;
ALTER TABLE `dual_teacher_courses` ADD CONSTRAINT `fk_courses_teacher` FOREIGN KEY (`teacher_id`) REFERENCES `users`(`id`) ON DELETE SET NULL;
ALTER TABLE `dual_teacher_courses` ADD CONSTRAINT `fk_courses_mentor` FOREIGN KEY (`mentor_id`) REFERENCES `users`(`id`) ON DELETE SET NULL;
ALTER TABLE `audit_logs` ADD CONSTRAINT `fk_logs_user` FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE SET NULL;


-- =================================================================
-- 3. 初始数据插入 (Initial Data Insertion)
-- =================================================================

INSERT INTO `roles` (`role_name`) VALUES
                                      ('学生'),
                                      ('教师'),
                                      ('企业管理员'),
                                      ('企业导师'),
                                      ('学校管理员'),
                                      ('系统管理员');

-- =================================================================
-- 脚本结束
-- =================================================================
