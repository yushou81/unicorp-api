-- 创建课程章节表
CREATE TABLE IF NOT EXISTS course_chapters (
    id INT AUTO_INCREMENT PRIMARY KEY,
    course_id INT NOT NULL COMMENT '关联的双师课堂ID',
    title VARCHAR(100) NOT NULL COMMENT '章节标题',
    description TEXT COMMENT '章节描述',
    sequence INT NOT NULL DEFAULT 0 COMMENT '章节顺序',
    is_published BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否已发布',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否删除',
    FOREIGN KEY (course_id) REFERENCES dual_teacher_courses(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程章节表';

-- 创建章节-资源关联表
CREATE TABLE IF NOT EXISTS chapter_resources (
    id INT AUTO_INCREMENT PRIMARY KEY,
    chapter_id INT NOT NULL COMMENT '章节ID',
    resource_id INT NOT NULL COMMENT '资源ID',
    sequence INT NOT NULL DEFAULT 0 COMMENT '资源在章节中的顺序',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否删除',
    FOREIGN KEY (chapter_id) REFERENCES course_chapters(id),
    FOREIGN KEY (resource_id) REFERENCES course_resources(id),
    UNIQUE KEY unique_chapter_resource (chapter_id, resource_id, is_deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='章节-资源关联表';

-- 创建学习进度表
CREATE TABLE IF NOT EXISTS learning_progress (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL COMMENT '学生ID',
    chapter_id INT NOT NULL COMMENT '章节ID',
    status ENUM('not_started', 'in_progress', 'completed') NOT NULL DEFAULT 'not_started' COMMENT '学习状态',
    progress_percent INT NOT NULL DEFAULT 0 COMMENT '完成百分比(0-100)',
    start_time TIMESTAMP NULL COMMENT '开始学习时间',
    complete_time TIMESTAMP NULL COMMENT '完成学习时间',
    duration_minutes INT NOT NULL DEFAULT 0 COMMENT '学习时长(分钟)',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (student_id) REFERENCES users(id),
    FOREIGN KEY (chapter_id) REFERENCES course_chapters(id),
    UNIQUE KEY unique_student_chapter (student_id, chapter_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学习进度表';

-- 创建课程讨论表
CREATE TABLE IF NOT EXISTS course_discussions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    course_id INT NOT NULL COMMENT '课程ID',
    user_id INT NOT NULL COMMENT '发布用户ID',
    content TEXT NOT NULL COMMENT '讨论内容',
    parent_id INT NULL COMMENT '父讨论ID，用于回复',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否删除',
    FOREIGN KEY (course_id) REFERENCES dual_teacher_courses(id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (parent_id) REFERENCES course_discussions(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程讨论表';

-- 创建课程问答表
CREATE TABLE IF NOT EXISTS course_questions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    course_id INT NOT NULL COMMENT '课程ID',
    chapter_id INT NULL COMMENT '相关章节ID',
    student_id INT NOT NULL COMMENT '提问学生ID',
    title VARCHAR(200) NOT NULL COMMENT '问题标题',
    content TEXT NOT NULL COMMENT '问题内容',
    status ENUM('pending', 'answered', 'closed') NOT NULL DEFAULT 'pending' COMMENT '问题状态',
    answer TEXT NULL COMMENT '回答内容',
    answered_by INT NULL COMMENT '回答者ID',
    answered_at TIMESTAMP NULL COMMENT '回答时间',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否删除',
    FOREIGN KEY (course_id) REFERENCES dual_teacher_courses(id),
    FOREIGN KEY (chapter_id) REFERENCES course_chapters(id),
    FOREIGN KEY (student_id) REFERENCES users(id),
    FOREIGN KEY (answered_by) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程问答表';

-- 创建课程出勤记录表
CREATE TABLE IF NOT EXISTS course_attendance (
    id INT AUTO_INCREMENT PRIMARY KEY,
    course_id INT NOT NULL COMMENT '课程ID',
    student_id INT NOT NULL COMMENT '学生ID',
    attendance_date DATE NOT NULL COMMENT '出勤日期',
    status ENUM('present', 'absent', 'late', 'leave') NOT NULL DEFAULT 'present' COMMENT '出勤状态',
    remark VARCHAR(255) NULL COMMENT '备注',
    recorded_by INT NOT NULL COMMENT '记录人ID',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (course_id) REFERENCES dual_teacher_courses(id),
    FOREIGN KEY (student_id) REFERENCES users(id),
    FOREIGN KEY (recorded_by) REFERENCES users(id),
    UNIQUE KEY unique_attendance (course_id, student_id, attendance_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程出勤记录表'; 