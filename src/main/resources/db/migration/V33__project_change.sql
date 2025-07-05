
-- 1. 重命名 projects 表
RENAME TABLE projects TO old_projects;

-- 2. 重命名 project_applications 表
RENAME TABLE project_applications TO old_project_applications;

-- 3. 重命名 project_members 表
RENAME TABLE project_members TO old_project_members;






-- 项目主表
CREATE TABLE project (
  project_id INT PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(255) NOT NULL,
  description TEXT,
  initiator_type VARCHAR(32),
  initiator_id INT,
  field VARCHAR(64),
  budget DECIMAL(12,2),
  contact VARCHAR(128),
  status VARCHAR(32),
  attachments TEXT
);

-- 项目对接/合作申请
CREATE TABLE project_application (
  application_id INT PRIMARY KEY AUTO_INCREMENT,
  project_id INT,
  applicant_type VARCHAR(32),
  applicant_id INT,
  message VARCHAR(255),
  status VARCHAR(32)
);

-- 项目进度
CREATE TABLE project_progress (
  progress_id INT PRIMARY KEY AUTO_INCREMENT,
  project_id INT,
  stage VARCHAR(64),
  content TEXT,
  attachments TEXT
);

-- 项目结项
CREATE TABLE project_closure (
  id INT PRIMARY KEY AUTO_INCREMENT,
  project_id INT,
  summary TEXT,
  attachments TEXT,
  status VARCHAR(32)
);

-- 项目资料/合同
CREATE TABLE project_document (
  document_id INT PRIMARY KEY AUTO_INCREMENT,
  project_id INT,
  type VARCHAR(32),
  url VARCHAR(512),
  description VARCHAR(255)
);

-- 项目经费申请
CREATE TABLE project_fund (
  fund_id INT PRIMARY KEY AUTO_INCREMENT,
  project_id INT,
  amount DECIMAL(12,2),
  purpose VARCHAR(255),
  applicant_id INT,
  attachments TEXT,
  status VARCHAR(32)
);

-- 经费使用记录
CREATE TABLE project_fund_record (
  id INT PRIMARY KEY AUTO_INCREMENT,
  fund_id INT,
  amount DECIMAL(12,2),
  purpose VARCHAR(255),
  status VARCHAR(32)
);

-- 项目操作日志
CREATE TABLE project_log (
  log_id INT PRIMARY KEY AUTO_INCREMENT,
  project_id INT,
  action VARCHAR(64),
  operator VARCHAR(64),
  time DATETIME
);


CREATE TABLE project_member_permission (
  id INT PRIMARY KEY AUTO_INCREMENT,
  project_id INT NOT NULL,                -- 项目ID
  user_id INT NOT NULL,                   -- 用户ID
  role VARCHAR(64) NOT NULL,              -- 分配的角色/权限（如progress_manager、fund_approver等）
  expire_at DATETIME DEFAULT NULL,        -- 权限到期时间（可为空，表示永久）
  assigned_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, -- 分配时间
  UNIQUE KEY uk_project_user_role (project_id, user_id, role) -- 保证同一项目同一用户同一角色唯一
);

CREATE TABLE project_permission_log (
  log_id INT PRIMARY KEY AUTO_INCREMENT,
  project_id INT NOT NULL,                -- 项目ID
  user_id INT NOT NULL,                   -- 被操作用户ID
  action VARCHAR(16) NOT NULL,            -- 操作类型（assign/revoke）
  role VARCHAR(64) NOT NULL,              -- 变更的角色/权限
  operator VARCHAR(64) NOT NULL,          -- 操作人（可存用户名或ID）
  time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, -- 操作时间
  expire_at DATETIME DEFAULT NULL         -- 分配时的到期时间（回收时可为空）
);


CREATE TABLE project_role_dict (
  role VARCHAR(64) PRIMARY KEY,           -- 角色标识
  name VARCHAR(64) NOT NULL,              -- 角色名称（如“进度负责人”）
  description VARCHAR(255)                -- 角色说明
);

ALTER TABLE project
    ADD COLUMN reason VARCHAR(255) COMMENT '状态变更原因';
    
    ALTER TABLE project
    ADD COLUMN create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    ADD COLUMN update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间';
    
    
    ALTER TABLE project_application
    ADD COLUMN create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    ADD COLUMN update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间';
    
 ALTER TABLE project_application
ADD COLUMN approved_time DATETIME NULL COMMENT '同意时间';