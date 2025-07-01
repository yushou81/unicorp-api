ALTER TABLE projects
    ADD COLUMN plan_member_count INT COMMENT '计划人数',
ADD COLUMN difficulty VARCHAR(50) COMMENT '项目难度',
ADD COLUMN support_languages VARCHAR(255) COMMENT '支持语言，逗号分隔',
ADD COLUMN tech_fields VARCHAR(255) COMMENT '技术领域，逗号分隔',
ADD COLUMN programming_languages VARCHAR(255) COMMENT '编程语言，逗号分隔',
ADD COLUMN project_proposal_url VARCHAR(255)
    COMMENT '项目计划书文件URL';