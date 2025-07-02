-- 添加image_url字段到resources表，用于存储专利和著作权类型资源的图片URL

ALTER TABLE resources ADD COLUMN image_url VARCHAR(255) COMMENT '图片URL，主要用于专利和著作权类型资源的图片展示';

-- 更新已有的专利和著作权类型资源，将file_url复制到image_url
UPDATE resources 
SET image_url = file_url 
WHERE resource_type IN ('patent', 'copyright') AND image_url IS NULL; 