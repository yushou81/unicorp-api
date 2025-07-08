-- 为audit_logs表添加新字段
ALTER TABLE audit_logs
ADD COLUMN user_account VARCHAR(100) COMMENT '用户账号',
ADD COLUMN user_name VARCHAR(100) COMMENT '用户名称',
ADD COLUMN ip VARCHAR(50) COMMENT 'IP地址',
ADD COLUMN module VARCHAR(100) COMMENT '所属模块',
ADD COLUMN result VARCHAR(50) COMMENT '操作结果';

-- 添加索引提高查询效率
CREATE INDEX idx_audit_logs_action ON audit_logs(action);
CREATE INDEX idx_audit_logs_timestamp ON audit_logs(timestamp);
CREATE INDEX idx_audit_logs_user_account ON audit_logs(user_account);
CREATE INDEX idx_audit_logs_module ON audit_logs(module); 