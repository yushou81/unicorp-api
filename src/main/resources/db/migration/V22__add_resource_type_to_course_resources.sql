-- 为课程资源表添加资源类型字段
ALTER TABLE `course_resources` 
ADD COLUMN `resource_type` ENUM('document', 'video', 'code', 'other') 
COMMENT '资源类型分类(document:文档,video:视频,code:代码,other:其他)' 
AFTER `file_type`;

-- 更新现有记录的资源类型为'document'（默认值）
UPDATE `course_resources` SET `resource_type` = 'document' WHERE `resource_type` IS NULL; 