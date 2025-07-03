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

## 项目结构

```
unicorp-api/
├── docs/                        # 项目文档
│   ├── 第1次迭代/
│   ├── 第2次迭代/
│   ├── 第3次迭代/
│   ├── 第4次迭代/
│   ├── 第5次迭代/
│   ├── 第6次迭代/
│   ├── 第7次迭代/
│   └── 第8次迭代/
│       ├── 用户管理接口测试计划.md  # 用户管理接口测试计划
│       └── 企业管理员接口测试计划.md # 企业管理员接口测试计划
├── src/                         # 源代码
│   ├── main/
│   │   ├── java/com/csu/unicorp/
│   │   │   ├── common/          # 通用工具和常量
│   │   │   ├── config/          # 配置类
│   │   │   ├── controller/      # 控制器层
│   │   │   ├── dto/             # 数据传输对象
│   │   │   │   ├── UserStatusUpdateDTO.java  # 用户状态更新DTO
│   │   │   │   └── UserUpdateDTO.java        # 用户基本信息更新DTO
│   │   │   ├── entity/          # 实体类
│   │   │   ├── mapper/          # MyBatis Mapper接口
│   │   │   ├── service/         # 服务接口
│   │   │   │   └── impl/        # 服务实现类
│   │   │   └── vo/              # 视图对象
│   │   └── resources/
│   │       ├── db/migration/    # Flyway数据库迁移脚本
│   │       ├── mapper/          # MyBatis XML映射文件
│   │       ├── application.yml  # 应用配置文件
│   │       └── ...
│   └── test/                    # 测试代码
├── upload/                      # 文件上传目录
├── pom.xml                      # Maven项目配置文件
└── README.md                    # 项目说明文档
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

已实现API:
1. 用户登录 - POST `/v1/auth/login`
2. 学生注册 - POST `/v1/auth/register/student`
3. 文件上传 - POST `/v1/files/upload`
4. 获取资源列表 - GET `/v1/resources`
5. 获取资源详情 - GET `/v1/resources/{id}`
6. 创建资源 - POST `/v1/resources`
7. 更新资源 - PUT `/v1/resources/{id}`
8. 删除资源 - DELETE `/v1/resources/{id}`
9. 获取用户简历 - GET `/v1/resumes/{userId}`
10. 获取我的简历 - GET `/v1/me/resume`
11. 获取我的所有简历列表 - GET `/v1/me/resumes`
12. 创建简历 - POST `/v1/me/resume`
13. 更新简历 - PUT `/v1/me/resume/{resumeId}`
14. 删除简历 - DELETE `/v1/me/resume/{resumeId}`
15. 获取作品集列表 - GET `/v1/me/portfolio`
16. 添加作品集项目 - POST `/v1/me/portfolio`
17. 更新作品集项目 - PUT `/v1/me/portfolio/{itemId}`
18. 删除作品集项目 - DELETE `/v1/me/portfolio/{itemId}`
19. 获取收藏岗位列表 - GET `/v1/me/favorites/jobs`
20. 收藏岗位 - POST `/v1/jobs/{id}/favorite`
21. 取消收藏岗位 - DELETE `/v1/jobs/{id}/favorite`
22. 更新用户个人信息 - PUT `/v1/auth/profile`
23. 修改用户密码 - PUT `/v1/auth/password`
24. 获取所有顶级岗位分类 - GET `/v1/admin/job-categories/root`
25. 获取指定分类的子分类 - GET `/v1/admin/job-categories/{id}/children`
26. 获取所有岗位分类 - GET `/v1/admin/job-categories`
27. 获取岗位分类详情 - GET `/v1/admin/job-categories/{id}`
28. 创建岗位分类 - POST `/v1/admin/job-categories`
29. 更新岗位分类 - PUT `/v1/admin/job-categories/{id}`
30. 删除岗位分类 - DELETE `/v1/admin/job-categories/{id}`
31. 创建双师课堂课程 - POST `/v1/dual-courses`
32. 更新课程信息 - PUT `/v1/dual-courses/{id}`
33. 获取课程详情 - GET `/v1/dual-courses/{id}`
34. 删除课程 - DELETE `/v1/dual-courses/{id}`
35. 获取教师创建的课程列表 - GET `/v1/dual-courses/teacher`
36. 获取企业导师参与的课程列表 - GET `/v1/dual-courses/mentor`
37. 获取可报名课程列表 - GET `/v1/dual-courses/enrollable`
38. 学生报名课程 - POST `/v1/dual-courses/enroll`
39. 学生取消报名 - DELETE `/v1/dual-courses/enroll/{courseId}`
40. 获取学生已报名的课程列表 - GET `/v1/dual-courses/enrolled`
41. 更新课程状态 - PATCH `/v1/dual-courses/{id}/status`
42. 上传课程资源 - POST `/v1/course-resources`
43. 删除课程资源 - DELETE `/v1/course-resources/{resourceId}`
44. 获取课程资源详情 - GET `/v1/course-resources/{resourceId}`
45. 获取课程资源列表 - GET `/v1/course-resources/course/{courseId}`
46. 下载课程资源 - GET `/v1/course-resources/download/{resourceId}`
47. 提交课程评价 - POST `/v1/course-ratings`
48. 更新课程评价 - PUT `/v1/course-ratings/{ratingId}`
49. 删除课程评价 - DELETE `/v1/course-ratings/{ratingId}`
50. 获取课程评价详情 - GET `/v1/course-ratings/{ratingId}`
51. 获取课程评价列表 - GET `/v1/course-ratings/course/{courseId}`
52. 获取课程平均评分 - GET `/v1/course-ratings/average/{courseId}`
53. 检查学生是否已评价课程 - GET `/v1/course-ratings/check/{courseId}`
54. 搜索用户 - GET `/v1/auth/search`