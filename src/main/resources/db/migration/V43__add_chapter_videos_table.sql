-- 创建章节视频表
CREATE TABLE IF NOT EXISTS `course_chapter_videos` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `chapter_id` INT NOT NULL COMMENT '关联的章节ID',
    `title` VARCHAR(100) NOT NULL COMMENT '视频标题',
    `description` TEXT COMMENT '视频描述',
    `file_path` VARCHAR(255) NOT NULL COMMENT '视频文件路径',
    `file_size` BIGINT COMMENT '视频文件大小(字节)',
    `duration` INT COMMENT '视频时长(秒)',
    `cover_image` VARCHAR(255) COMMENT '视频封面图片路径',
    `uploader_id` INT NOT NULL COMMENT '上传者ID',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    FOREIGN KEY (`chapter_id`) REFERENCES `course_chapters`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='章节视频表';

-- 创建视频观看记录表
CREATE TABLE IF NOT EXISTS `video_watch_records` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `video_id` INT NOT NULL COMMENT '视频ID',
    `user_id` INT NOT NULL COMMENT '用户ID',
    `watch_progress` INT DEFAULT 0 COMMENT '观看进度(秒)',
    `is_completed` TINYINT(1) DEFAULT 0 COMMENT '是否看完',
    `last_position` INT DEFAULT 0 COMMENT '上次观看位置(秒)',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY `uk_video_user` (`video_id`, `user_id`),
    FOREIGN KEY (`video_id`) REFERENCES `course_chapter_videos`(`id`),
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='视频观看记录表'; 