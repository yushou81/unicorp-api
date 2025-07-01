-- 添加岗位具体要求和工作福利描述字段
ALTER TABLE jobs
    ADD COLUMN job_requirements TEXT COMMENT '岗位具体要求' AFTER tags,
    ADD COLUMN job_benefits TEXT COMMENT '工作福利描述' AFTER job_requirements; 