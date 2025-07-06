-- 设备资源表
CREATE TABLE equipment_resources (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '设备名称',
    description TEXT COMMENT '设备描述',
    image_url VARCHAR(255) COMMENT '设备照片URL',
    location VARCHAR(200) NOT NULL COMMENT '设备位置',
    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE' COMMENT '设备状态：AVAILABLE-可用, MAINTENANCE-维护中, RESERVED-已预约',
    manager_id INT NOT NULL COMMENT '设备管理员ID',
    organization_id INT NOT NULL COMMENT '所属组织ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    version INT DEFAULT 0 COMMENT '乐观锁版本',
    FOREIGN KEY (manager_id) REFERENCES users(id),
    FOREIGN KEY (organization_id) REFERENCES organizations(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='实验设备资源表'; 


-- 添加索引以提高查询性能
CREATE INDEX idx_equipment_organization ON equipment_resources(organization_id);
CREATE INDEX idx_equipment_manager ON equipment_resources(manager_id);

