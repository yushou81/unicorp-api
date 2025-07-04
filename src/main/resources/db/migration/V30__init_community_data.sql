-- 初始化社区板块
INSERT INTO community_category (name, description, sort_order, permission_level) VALUES 
('校企合作', '讨论校企合作相关话题', 1, 0),
('技术交流', '分享技术经验和知识', 2, 0),
('就业指导', '提供就业和职业发展建议', 3, 0),
('学术研究', '探讨学术研究和科研项目', 4, 0),
('校园生活', '交流校园生活和学习经验', 5, 0),
('企业专区', '企业相关信息和交流', 6, 1);

-- 添加子板块
INSERT INTO community_category (name, description, parent_id, sort_order, permission_level) VALUES 
('项目对接', '校企项目合作对接', 1, 1, 0),
('实习机会', '企业实习机会分享', 1, 2, 0),
('编程语言', '各类编程语言学习和讨论', 2, 1, 0),
('框架工具', '开发框架和工具使用经验', 2, 2, 0),
('面试经验', '分享面试经验和技巧', 3, 1, 0),
('职业规划', '职业发展规划建议', 3, 2, 0);

-- 初始化标签
INSERT INTO community_tag (name, description) VALUES 
('Java', 'Java编程语言相关讨论'),
('Python', 'Python编程语言相关讨论'),
('前端', '前端开发技术相关讨论'),
('后端', '后端开发技术相关讨论'),
('数据库', '数据库技术相关讨论'),
('人工智能', 'AI与机器学习相关讨论'),
('校企合作', '校企合作项目相关讨论'),
('实习', '实习经验与机会分享'),
('求职', '求职经验与技巧分享'),
('考研', '考研经验与信息分享');

-- 初始化敏感词（示例）
INSERT INTO community_sensitive_word (word, level, replacement) VALUES 
('敏感词1', 3, '***'),
('敏感词2', 2, '***'),
('敏感词3', 1, '***'); 