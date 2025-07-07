  
  CREATE TABLE project_contract (
    contract_id    INT PRIMARY KEY AUTO_INCREMENT COMMENT '合同ID',
    project_id     INT NOT NULL COMMENT '关联项目ID',
    contract_url   VARCHAR(255) NOT NULL COMMENT '合同文件URL',
    status         VARCHAR(32) NOT NULL DEFAULT 'pending' COMMENT '合同状态（draft/pending/active/finished/rejected）',
    initiator_id   INT NOT NULL COMMENT '发起方用户ID',
    receiver_id    INT NOT NULL COMMENT '接收方用户ID',
    sign_time      DATETIME NULL COMMENT '签署时间',
    create_time    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark         VARCHAR(255) NULL COMMENT '备注'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目合同表';
ALTER TABLE project_contract ADD COLUMN contract_name VARCHAR(128) NOT NULL DEFAULT '' COMMENT '合同名称' AFTER project_id;




ALTER TABLE project_fund
ADD COLUMN create_time DATETIME NULL COMMENT '申请创建时间',
ADD COLUMN approved_time DATETIME NULL COMMENT '同意时间',
ADD COLUMN rejected_time DATETIME NULL COMMENT '拒绝时间';