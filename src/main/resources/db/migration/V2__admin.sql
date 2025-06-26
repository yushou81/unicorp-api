-- =================================================================
-- 数据填充脚本 (Data Seeding Script)
-- 项目名称: 校企联盟平台 (unicorp)
-- 版本: 1.0
-- 说明: 本脚本用于创建5个初始的系统管理员账号。
-- =================================================================

-- 使用目标数据库
-- USE unicorp;

-- =================================================================
-- 前提：请确保 `roles` 表已存在，且'系统管理员'角色的id为6。
-- SET @admin_role_id = (SELECT id FROM roles WHERE role_name = '系统管理员');
-- 为了脚本的通用性，我们这里直接假设 id 为 6。
-- =================================================================


-- 1. 创建总管理员 (SysAdmin Prime)
-- -----------------------------------------------------------------
-- !! 安全警告: 请将下面的密码哈希值替换为你自己生成的真实BCrypt哈希值 !!
INSERT INTO `users` (`account`, `password`, `email`, `phone`, `nickname`, `status`, `organization_id`, `is_deleted`)
VALUES
    ('00000001', '$2a$10$cMh4JztUYyu7h6mV0KLHuOD2Cny5s2WLooiP8fxg185S1vWnKpM0e', 'sysadmin@unicorp.com', '10000000001', '总管理员', 'active', NULL, 0);

-- 将 "系统管理员" 角色赋予该用户 (假设角色ID为6)
INSERT INTO `user_roles` (`user_id`, `role_id`)
VALUES
    (LAST_INSERT_ID(), 6);


-- 2. 创建运维管理员 (Operations Admin)
-- -----------------------------------------------------------------
-- !! 安全警告: 请将下面的密码哈希值替换为你自己生成的真实BCrypt哈希值 !!
INSERT INTO `users` (`account`, `password`, `email`, `phone`, `nickname`, `status`, `organization_id`, `is_deleted`)
VALUES
    ('00000002', '$2a$10$cMh4JztUYyu7h6mV0KLHuOD2Cny5s2WLooiP8fxg185S1vWnKpM0e', 'ops@unicorp.com', '10000000002', '运维管理员', 'active', NULL, 0);

-- 将 "系统管理员" 角色赋予该用户 (假设角色ID为6)
INSERT INTO `user_roles` (`user_id`, `role_id`)
VALUES
    (LAST_INSERT_ID(), 6);


-- 3. 创建安全管理员 (Security Admin)
-- -----------------------------------------------------------------
-- !! 安全警告: 请将下面的密码哈希值替换为你自己生成的真实BCrypt哈希值 !!
INSERT INTO `users` (`account`, `password`, `email`, `phone`, `nickname`, `status`, `organization_id`, `is_deleted`)
VALUES
    ('00000003', '$2a$10$cMh4JztUYyu7h6mV0KLHuOD2Cny5s2WLooiP8fxg185S1vWnKpM0e', 'security@unicorp.com', '10000000003', '安全管理员', 'active', NULL, 0);

-- 将 "系统管理员" 角色赋予该用户 (假设角色ID为6)
INSERT INTO `user_roles` (`user_id`, `role_id`)
VALUES
    (LAST_INSERT_ID(), 6);


-- 4. 创建数据管理员 (Data Admin)
-- -----------------------------------------------------------------
-- !! 安全警告: 请将下面的密码哈希值替换为你自己生成的真实BCrypt哈希值 !!
INSERT INTO `users` (`account`, `password`, `email`, `phone`, `nickname`, `status`, `organization_id`, `is_deleted`)
VALUES
    ('00000004', '$2a$10$cMh4JztUYyu7h6mV0KLHuOD2Cny5s2WLooiP8fxg185S1vWnKpM0e', 'data@unicorp.com', '10000000004', '数据管理员', 'active', NULL, 0);

-- 将 "系统管理员" 角色赋予该用户 (假设角色ID为6)
INSERT INTO `user_roles` (`user_id`, `role_id`)
VALUES
    (LAST_INSERT_ID(), 6);


-- 5. 创建开发管理员 (Development Admin)
-- -----------------------------------------------------------------
-- !! 安全警告: 请将下面的密码哈希值替换为你自己生成的真实BCrypt哈希值 !!
INSERT INTO `users` (`account`, `password`, `email`, `phone`, `nickname`, `status`, `organization_id`, `is_deleted`)
VALUES
    ('00000005', '$2a$10$cMh4JztUYyu7h6mV0KLHuOD2Cny5s2WLooiP8fxg185S1vWnKpM0e', 'dev@unicorp.com', '10000000005', '开发管理员', 'active', NULL, 0);

-- 将 "系统管理员" 角色赋予该用户 (假设角色ID为6)
INSERT INTO `user_roles` (`user_id`, `role_id`)
VALUES
    (LAST_INSERT_ID(), 6);


-- =================================================================
-- 脚本结束
-- =================================================================
