# 校企联盟平台 (UniCorp API)

校企联盟平台是一款专为高校与企业搭建的数字化协作桥梁，旨在打破传统校企合作中的信息壁垒，促进人才培养、科研合作、实习就业等多维度深度融合。

## 项目简介

本平台通过整合双方资源，提供高效的项目对接、人才交流、成果转化等服务，实现教育链、人才链与产业链、创新链的有机衔接。系统采用模块化设计，结合严格的权限管理和完善的日志审计体系，确保不同角色用户能够安全、便捷地使用各项功能，推动校企合作向规范化、智能化方向发展。

## 技术栈

- **后端框架**：Spring Boot 3.4.7
- **安全框架**：Spring Security
- **数据访问**：MyBatis-Plus 3.5.12
- **数据库**：MySQL
- **数据库版本控制**：Flyway
- **接口文档**：SpringDoc OpenAPI
- **JWT认证**：JJWT 0.11.5
- **构建工具**：Maven

## 主要功能模块

1. **权限管理系统**
   - 多角色权限模型
   - 权限分级控制
   - 动态权限调整
   - 单点登录

2. **校企信息管理**
   - 学校信息展示
   - 企业信息展示
   - 信息审核机制

3. **项目合作管理**
   - 项目发布与对接
   - 项目全周期管理
   - 经费管理

4. **人才培养与交流**
   - 实习就业管理
   - 双师课堂
   - 学生成果展示

5. **资源共享中心**
   - 科研资源共享
   - 课程资源库
   - 知识产权交易

6. **日志审计系统**
   - 操作日志记录
   - 系统日志监控
   - 审计报表生成

7. **在线交流社区**
   - 社区板块管理
   - 话题与帖子
   - 问答系统
   - 资源共享
   - 活动发布与报名
   - 私信与通知
   - 积分与激励
   - 内容审核与举报

## 项目结构

```
unicorp-api/
├── docs/                     # 文档目录
├── src/                      # 源代码目录
│   ├── main/                 # 主代码
│   │   ├── java/             # Java代码
│   │   │   └── com/csu/unicorp/
│   │   │       ├── common/           # 通用工具和配置
│   │   │       │   ├── annotation/   # 自定义注解
│   │   │       │   ├── aspect/       # AOP切面
│   │   │       │   ├── constants/    # 常量定义
│   │   │       │   ├── exception/    # 异常处理
│   │   │       │   └── utils/        # 工具类，包含验证码工具
│   │   │       ├── config/           # 配置类
│   │   │       │   └── security/     # 安全相关配置
│   │   │       ├── controller/       # 控制器层
│   │   │       │   ├── achievement/  # 成果相关控制器
│   │   │       │   ├── community/    # 社区相关控制器
│   │   │       │   ├── course/       # 课程相关控制器
│   │   │       │   ├── job/          # 工作相关控制器
│   │   │       │   └── log/          # 日志相关控制器
│   │   │       ├── dto/              # 数据传输对象
│   │   │       ├── entity/           # 实体类
│   │   │       ├── interceptor/      # 拦截器
│   │   │       ├── mapper/           # MyBatis Mapper接口
│   │   │       ├── service/          # 服务层接口
│   │   │       │   └── impl/         # 服务层实现类，包含邮件服务和验证码服务
│   │   │       └── vo/               # 视图对象
│   │   ├── resources/        # 资源文件
│   │   │   ├── db/           # 数据库迁移脚本
│   │   │   ├── mapper/       # MyBatis XML映射文件
│   │   │   ├── static/       # 静态资源
│   │   │   └── templates/    # 模板文件
│   └── test/                 # 测试代码
├── upload/                   # 上传文件存储目录
├── docker-compose.yml        # Docker容器编排配置
├── mvnw                      # Maven包装器（Unix/Linux）
├── mvnw.cmd                  # Maven包装器（Windows）
├── pom.xml                   # Maven项目配置
└── README.md                 # 项目说明文档
```

## 数据库命名规范

项目采用以下数据库命名规范：

1. **表名**：使用蛇形命名法（snake_case），如 `organizations`, `user_roles`
2. **字段名**：使用蛇形命名法，如 `organization_id`, `created_at`
3. **主键**：所有表主键统一命名为 `id`（除关联表外）

## 用户身份体系

平台实现了账户与实名信息分离的设计：

1. **用户账户 (User)**：存储登录账号、密码、昵称等基本信息
2. **实名认证 (UserVerification)**：隔离存储用户实名信息，提高数据安全性
3. **简历 (Resume)**：存储学生特有的专业、教育水平、简历URL等信息

## 已实现功能

### 第一次迭代 - 基础用户体系与组织管理
- 用户登录注册体系（基于Spring Security + JWT）
- 组织管理（学校/企业）
- 角色权限管理
- 用户实名认证模型

### 第二次迭代 - 招聘与申请管理
- 企业发布招聘信息
- 学生浏览招聘信息
- 学生申请职位
- 企业查看申请并处理

### 第三次迭代 - 项目合作管理
- 校企双方发布合作项目
- 校企双方管理合作项目
- 公开浏览合作项目
- 学生申请参与项目

### 第四次迭代 - 资源共享中心
- 文件上传与管理
- 教师/企业导师上传共享资源
- 公开浏览资源列表
- 资源详情查看与下载
- 资源所有者更新和删除资源

### 第五次迭代 - 个人主页与作品集管理
- 用户个人主页的展示与编辑
- 学生作品集管理（创建、更新、删除）
- 个人档案信息维护
- 用户头像和个人简介管理

### 第六次迭代 - 双师课堂功能
- 教师和企业导师联合开设双师课程
- 学生浏览和报名课程
- 课程全生命周期管理（规划、开放报名、进行中、完成）
- 课程名额限制和报名管理
- 支持在线/线下/混合模式的课程设置
- 课程资源管理（上传、下载、查询）
- 课程评价系统（学生评分、评价）

### 第七次迭代 - 岗位收藏功能
- 学生用户收藏招聘岗位
- 学生用户取消收藏岗位
- 学生用户查看已收藏岗位列表
- 岗位收藏权限控制

### 第八次迭代 - 岗位详情增强
- 添加了岗位具体要求(`job_requirements`)和工作福利描述(`job_benefits`)字段
- 更新了JobVO和JobCreationDTO，添加了相应字段
- 修复了application.yml中的重复paths-to-match配置错误

### 2. 岗位与分类关系调整
- 调整了岗位与分类的关系，一个岗位只对应一个三级分类
- 修改了JobCreationDTO，添加categoryId字段表示三级分类ID
- 将JobVO中的categories列表改为单一的category字段
- 修改了JobServiceImpl中的相关方法，确保岗位只关联一个三级分类
- 更新了API文档，明确说明岗位需要指定一个三级分类ID

### 3. 岗位申请与简历关联
- 在岗位申请表中添加简历ID字段，关联到学生档案
- 更新了ApplicationCreationDTO，要求提交岗位申请时必须指定简历ID
- 修改了岗位申请相关VO，添加简历ID字段
- 企业导师可以通过岗位申请直接查看学生简历

### 4. 学生档案表重命名为简历表
- 将student_profiles表重命名为resumes，使命名更加直观
- 更新了相关实体类、Mapper和VO，使用Resume替代StudentProfile
- 调整了ApplicationMapper中的SQL查询，使用简历ID直接关联简历表
- 简化了申请与简历的关系，提高了系统灵活性

### 5. 简历管理功能
- 重构了原有的个人主页控制器，创建专门的简历管理控制器
- 支持用户创建多份简历，满足不同岗位申请需求
- 提供简历的增删改查完整功能
- 优化了简历与岗位申请的关联关系
- 移除了resumes表中user_id的唯一约束，使一个用户可以拥有多份简历

### 第九次迭代 - 智能推荐系统
- 实现了基于用户特征和岗位特征的智能推荐系统
- 添加了岗位自动特征提取功能，企业发布岗位时自动分析提取关键词和技能要求
- 支持用户设置个人特征（技能、兴趣、专业等）
- 基于特征匹配推荐合适的岗位给用户
- 支持用户行为分析，根据浏览、收藏、申请等行为优化推荐结果

### 9. 用户搜索功能
- 添加了通过电话号码或邮箱搜索用户的功能
- 实现了基于安全认证的用户搜索接口
- 只有已登录用户才能使用搜索功能
- 搜索结果包含用户基本信息（不包含敏感数据）

### 第九次迭代 - 学生成果展示平台
- 学生作品集管理（创建、更新、删除、展示）
- 竞赛获奖管理（创建、更新、删除、展示、认证）
- 科研成果管理（创建、更新、删除、展示、认证）
- 成果访问统计与分析
- 教师认证学生成果
- 企业查看学生成果

### 第十次迭代 - 在线交流社区
- 社区板块管理（分类、权限设置、置顶与精华）
- 话题与帖子（发布、编辑、互动、标签系统）
- 问答系统（提问、回答、最佳答案、悬赏机制）
- 资源共享（上传、评价、分类管理）
- 活动发布与报名（线上/线下活动、报名管理）
- 私信与通知（私信系统、消息通知、订阅功能）
- 积分与激励（积分规则、等级体系、荣誉徽章）
- 内容审核与举报（自动审核、人工审核、违规处理）

### 第十一次迭代 - 智能推荐系统
- 岗位推荐（基于学生特征和行为数据推荐合适岗位）
- 人才推荐（基于企业需求推荐匹配的学生人才）
- 用户行为分析（记录和分析用户浏览、搜索、申请等行为）
- 用户特征提取（从简历、作品集、成就等数据中提取用户特征）
- 岗位特征提取（从岗位描述、要求等信息中提取岗位特征）
- 推荐算法实现（基于内容的推荐和协同过滤推荐）

### 第十二次迭代 - GitHub OAuth2登录集成
- 支持使用GitHub账号登录系统
- 自动创建关联用户账号
- 实现无缝的OAuth2认证流程
- 优化数据库结构支持第三方登录（密码字段允许为NULL）

已实现API:
1. 用户登录 - POST `/v1/auth/login`
2. 学生注册 - POST `/v1/auth/register/student`
3. 获取岗位推荐列表 - GET `/v1/recommendations/jobs`
4. 获取人才推荐列表 - GET `/v1/recommendations/talents`
5. 更新岗位推荐状态 - PATCH `/v1/recommendations/jobs/{id}`
6. 更新人才推荐状态 - PATCH `/v1/recommendations/talents/{id}`
7. 记录用户行为 - POST `/v1/recommendations/behaviors`
8. 获取个人特征 - GET `/v1/recommendations/features/me`
9. 更新个人特征 - PUT `/v1/recommendations/features/me`
10. 获取行为统计 - GET `/v1/recommendations/statistics/behaviors`
11. 生成岗位推荐 - POST `/v1/recommendations/jobs/generate`
12. 生成人才推荐 - POST `/v1/recommendations/talents/generate`
13. 文件上传 - POST `/v1/files/upload`
14. 获取资源列表 - GET `/v1/resources`
15. 获取资源详情 - GET `/v1/resources/{id}`
16. 创建资源 - POST `/v1/resources`
17. 更新资源 - PUT `/v1/resources/{id}`
18. 删除资源 - DELETE `/v1/resources/{id}`
19. 获取用户简历 - GET `/v1/resumes/{userId}`
20. 获取我的简历 - GET `/v1/me/resume`
21. 获取我的所有简历列表 - GET `/v1/me/resumes`
22. 创建简历 - POST `/v1/me/resume`
23. 更新简历 - PUT `/v1/me/resume/{resumeId}`
24. 删除简历 - DELETE `/v1/me/resume/{resumeId}`
25. 获取作品集列表 - GET `/v1/me/portfolio`
26. 添加作品集项目 - POST `/v1/me/portfolio`
27. 更新作品集项目 - PUT `/v1/me/portfolio/{itemId}`
28. 删除作品集项目 - DELETE `/v1/me/portfolio/{itemId}`
29. 获取收藏岗位列表 - GET `/v1/me/favorites/jobs`
30. 收藏岗位 - POST `/v1/jobs/{id}/favorite`
31. 取消收藏岗位 - DELETE `/v1/jobs/{id}/favorite`
32. 更新用户个人信息 - PUT `/v1/auth/profile`
33. 修改用户密码 - PUT `/v1/auth/password`
34. 获取所有顶级岗位分类 - GET `/v1/admin/job-categories/root`
35. 获取指定分类的子分类 - GET `/v1/admin/job-categories/{id}/children`
36. 获取所有岗位分类 - GET `/v1/admin/job-categories`
37. 获取岗位分类详情 - GET `/v1/admin/job-categories/{id}`
38. 创建岗位分类 - POST `/v1/admin/job-categories`
39. 更新岗位分类 - PUT `/v1/admin/job-categories/{id}`
40. 删除岗位分类 - DELETE `/v1/admin/job-categories/{id}`
41. 创建双师课堂课程 - POST `/v1/dual-courses`
42. 更新课程信息 - PUT `/v1/dual-courses/{id}`
43. 获取课程详情 - GET `/v1/dual-courses/{id}`
44. 删除课程 - DELETE `/v1/dual-courses/{id}`
45. 获取教师创建的课程列表 - GET `/v1/dual-courses/teacher`
46. 获取企业导师参与的课程列表 - GET `/v1/dual-courses/mentor`
47. 获取可报名课程列表 - GET `/v1/dual-courses/enrollable`
48. 学生报名课程 - POST `/v1/dual-courses/enroll`
49. 学生取消报名 - DELETE `/v1/dual-courses/enroll/{courseId}`
50. 获取学生已报名的课程列表 - GET `/v1/dual-courses/enrolled`
51. 更新课程状态 - PATCH `/v1/dual-courses/{id}/status`
52. 上传课程资源 - POST `/v1/course-resources`
53. 删除课程资源 - DELETE `/v1/course-resources/{resourceId}`
54. 获取课程资源详情 - GET `/v1/course-resources/{resourceId}`
55. 获取课程资源列表 - GET `/v1/course-resources/course/{courseId}`
56. 下载课程资源 - GET `/v1/course-resources/download/{resourceId}`
57. 提交课程评价 - POST `/v1/course-ratings`
58. 更新课程评价 - PUT `/v1/course-ratings/{ratingId}`
59. 删除课程评价 - DELETE `/v1/course-ratings/{ratingId}`
60. 获取课程评价详情 - GET `/v1/course-ratings/{ratingId}`
61. 获取课程评价列表 - GET `/v1/course-ratings/course/{courseId}`
62. 获取课程平均评分 - GET `/v1/course-ratings/average/{courseId}`
63. 检查学生是否已评价课程 - GET `/v1/course-ratings/check/{courseId}`
64. 搜索用户 - GET `/v1/auth/search`