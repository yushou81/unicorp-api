-- 创建课程资源表
CREATE TABLE IF NOT EXISTS `course_resources` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '资源ID',
  `course_id` int(11) NOT NULL COMMENT '课程ID',
  `title` varchar(100) NOT NULL COMMENT '资源标题',
  `description` varchar(500) DEFAULT NULL COMMENT '资源描述',
  `file_path` varchar(255) NOT NULL COMMENT '文件路径',
  `file_size` bigint(20) DEFAULT NULL COMMENT '文件大小(字节)',
  `file_type` varchar(50) DEFAULT NULL COMMENT '文件类型',
  `uploader_id` int(11) NOT NULL COMMENT '上传者ID',
  `uploader_type` varchar(20) NOT NULL COMMENT '上传者类型(TEACHER/MENTOR)',
  `download_count` int(11) DEFAULT 0 COMMENT '下载次数',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(1) DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_course_id` (`course_id`),
  KEY `idx_uploader` (`uploader_id`, `uploader_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程资源表';

-- 创建课程评价表
CREATE TABLE IF NOT EXISTS `course_ratings` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `course_id` int(11) NOT NULL COMMENT '课程ID',
  `student_id` int(11) NOT NULL COMMENT '学生ID',
  `rating` int(11) NOT NULL COMMENT '评分(1-5)',
  `comment` text DEFAULT NULL COMMENT '评价内容',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_anonymous` tinyint(1) DEFAULT 0 COMMENT '是否匿名',
  `is_deleted` tinyint(1) DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_course_student` (`course_id`, `student_id`),
  KEY `idx_course_id` (`course_id`),
  KEY `idx_student_id` (`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程评价表'; 