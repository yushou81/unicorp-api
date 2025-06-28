-- 更新角色名称为英文标识符
UPDATE `roles` SET `role_name` = 'STUDENT' WHERE `role_name` = '学生';
UPDATE `roles` SET `role_name` = 'TEACHER' WHERE `role_name` = '教师';
UPDATE `roles` SET `role_name` = 'EN_ADMIN' WHERE `role_name` = '企业管理员';
UPDATE `roles` SET `role_name` = 'EN_TEACHER' WHERE `role_name` = '企业导师';
UPDATE `roles` SET `role_name` = 'SCH_ADMIN' WHERE `role_name` = '学校管理员';
UPDATE `roles` SET `role_name` = 'SYSADMIN' WHERE `role_name` = '系统管理员'; 