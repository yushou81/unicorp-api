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
│   └── 第7次迭代/
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
3. **学生档案 (StudentProfile)**：存储学生特有的专业、教育水平等信息

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

### 第七次迭代 - 岗位收藏功能
- 学生用户收藏招聘岗位
- 学生用户取消收藏岗位
- 学生用户查看已收藏岗位列表
- 岗位收藏权限控制

已实现API:
1. 用户登录 - POST `/v1/auth/login`
2. 学生注册 - POST `/v1/auth/register/student`
3. 文件上传 - POST `/v1/files/upload`
4. 获取资源列表 - GET `/v1/resources`
5. 获取资源详情 - GET `/v1/resources/{id}`
6. 创建资源 - POST `/v1/resources`
7. 更新资源 - PUT `/v1/resources/{id}`
8. 删除资源 - DELETE `/v1/resources/{id}`
9. 获取用户主页 - GET `/v1/profiles/{userId}`
10. 获取个人档案 - GET `/v1/me/profile`
11. 更新个人档案 - PUT `/v1/me/profile`
12. 获取作品集列表 - GET `/v1/me/portfolio`
13. 添加作品集项目 - POST `/v1/me/portfolio`
14. 更新作品集项目 - PUT `/v1/me/portfolio/{itemId}`
15. 删除作品集项目 - DELETE `/v1/me/portfolio/{itemId}`
16. 获取收藏岗位列表 - GET `/v1/me/favorites/jobs`
17. 收藏岗位 - POST `/v1/jobs/{id}/favorite`
18. 取消收藏岗位 - DELETE `/v1/jobs/{id}/favorite`
19. 更新用户个人信息 - PUT `/v1/auth/profile`
20. 修改用户密码 - PUT `/v1/auth/password`
21. 获取所有顶级岗位分类 - GET `/v1/admin/job-categories/root`
22. 获取指定分类的子分类 - GET `/v1/admin/job-categories/{id}/children`
23. 获取所有岗位分类 - GET `/v1/admin/job-categories`
24. 获取岗位分类详情 - GET `/v1/admin/job-categories/{id}`
25. 创建岗位分类 - POST `/v1/admin/job-categories`
26. 更新岗位分类 - PUT `/v1/admin/job-categories/{id}`
27. 删除岗位分类 - DELETE `/v1/admin/job-categories/{id}`