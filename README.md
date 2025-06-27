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
  ├── src/
  │   ├── main/
  │   │   ├── java/
  │   │   │   └── com/
  │   │   │       └── csu/
  │   │   │           └── unicorp/
  │   │   │               ├── common/
  │   │   │               │   ├── exception/
  │   │   │               │   └── utils/
  │   │   │               ├── config/
  │   │   │               │   └── security/
  │   │   │               ├── controller/
  │   │   │               │   ├── AdminController.java
  │   │   │               │   ├── ApplicationController.java
  │   │   │               │   ├── AuthController.java
  │   │   │               │   ├── EnterpriseAdminController.java
  │   │   │               │   ├── JobController.java
  │   │   │               │   ├── MapController.java
  │   │   │               │   ├── OrganizationController.java
  │   │   │               │   ├── ProjectController.java
  │   │   │               │   └── SchoolAdminController.java
  │   │   │               ├── dto/
  │   │   │               │   ├── EnterpriseRegistrationDTO.java
  │   │   │               │   ├── JobCreationDTO.java
  │   │   │               │   ├── LoginCredentialsDTO.java
  │   │   │               │   ├── OrgMemberCreationDTO.java
  │   │   │               │   ├── ProjectCreationDTO.java
  │   │   │               │   ├── SchoolCreationDTO.java
  │   │   │               │   └── StudentRegistrationDTO.java
  │   │   │               ├── entity/
  │   │   │               │   ├── Application.java
  │   │   │               │   ├── EnterpriseDetail.java
  │   │   │               │   ├── Job.java
  │   │   │               │   ├── Organization.java
  │   │   │               │   ├── Project.java
  │   │   │               │   ├── Role.java
  │   │   │               │   ├── User.java
  │   │   │               │   └── UserVerification.java
  │   │   │               ├── mapper/
  │   │   │               │   ├── ApplicationMapper.java
  │   │   │               │   ├── EnterpriseDetailMapper.java
  │   │   │               │   ├── JobMapper.java
  │   │   │               │   ├── OrganizationMapper.java
  │   │   │               │   ├── ProjectMapper.java
  │   │   │               │   ├── RoleMapper.java
  │   │   │               │   ├── UserMapper.java
  │   │   │               │   └── UserVerificationMapper.java
  │   │   │               ├── service/
  │   │   │               │   ├── impl/
  │   │   │               │   │   ├── ApplicationServiceImpl.java
  │   │   │               │   │   ├── EnterpriseServiceImpl.java
  │   │   │               │   │   ├── JobServiceImpl.java
  │   │   │               │   │   ├── OrganizationServiceImpl.java
  │   │   │               │   │   ├── ProjectServiceImpl.java
  │   │   │               │   │   ├── RoleServiceImpl.java
  │   │   │               │   │   └── UserServiceImpl.java
  │   │   │               │   ├── ApplicationService.java
  │   │   │               │   ├── EnterpriseService.java
  │   │   │               │   ├── JobService.java
  │   │   │               │   ├── OrganizationService.java
  │   │   │               │   ├── ProjectService.java
  │   │   │               │   ├── RoleService.java
  │   │   │               │   └── UserService.java
  │   │   │               └── vo/
  │   │   │                   ├── ApplicationVO.java
  │   │   │                   ├── JobVO.java
  │   │   │                   ├── OrganizationVO.java
  │   │   │                   ├── ProjectVO.java
  │   │   │                   ├── ResultVO.java
  │   │   │                   ├── TokenVO.java
  │   │   │                   └── UserVO.java
  │   │   └── resources/
  │   │       ├── db/
  │   │       │   └── migration/
  │   │       │       ├── V1__init_schema.sql
  │   │       │       └── V2__insert_admin_users.sql
  │   │       ├── mapper/
  │   │       │   ├── ApplicationMapper.xml
  │   │       │   ├── EnterpriseDetailMapper.xml
  │   │       │   ├── JobMapper.xml
  │   │       │   ├── OrganizationMapper.xml
  │   │       │   ├── ProjectMapper.xml
  │   │       │   ├── RoleMapper.xml
  │   │       │   ├── UserMapper.xml
  │   │       │   └── UserVerificationMapper.xml
  │   │       ├── static/
  │   │       ├── templates/
  │   │       └── application.yml
  │   └── test/
  │       └── java/
  │           └── com/
  │               └── csu/
  │                   └── unicorp/
  │                       ├── controller/
  │                       ├── mapper/
  │                       └── service/
  └── pom.xml
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

已实现API:
1. 用户登录 - POST `/v1/auth/login`
2. 学生注册 - POST `/v1/auth/register/student`
3. 获取当前用户信息 - GET `/v1/auth/me`
4. 获取所有学校列表 - GET `/v1/organizations/schools`
5. 管理员创建学校 - POST `/v1/admin/organizations/schools`

系统默认账号:
- 系统管理员账号: admin1, admin2, admin3, admin4, admin5
- 默认密码: admin123

## 如何运行

### 环境要求
- JDK 17+
- MySQL 8.0+
- Maven 3.6+

### 步骤

1. **克隆项目**
   ```bash
   git clone https://github.com/your-organization/unicorp-api.git
   cd unicorp-api
   ```

2. **配置数据库**
   
   修改 `src/main/resources/application.yml` 文件中的数据库连接信息

3. **构建项目**
   ```bash
   mvn clean package
   ```

4. **运行项目**
   ```bash
   java -jar target/unicorp-api-0.0.1-SNAPSHOT.jar
   ```

5. **API文档**
   
   启动后访问: http://localhost:8080/api/swagger-ui.html

## 贡献指南

1. Fork 本仓库
2. 创建您的特性分支: `git checkout -b my-new-feature`
3. 提交您的更改: `git commit -am 'Add some feature'`
4. 推送到分支: `git push origin my-new-feature`
5. 提交拉取请求

## 许可证

[MIT License](LICENSE) 

## API实现进度

### 第一次迭代

- [x] 管理员录入学校
- [x] 学生注册
- [x] 企业注册与审核
- [x] 用户登录（账号/邮箱/手机号）
- [x] 获取当前用户信息
- [x] 学校管理员创建教师账号
- [x] 企业管理员创建企业导师账号

### 第二次迭代

- [x] 企业发布招聘信息
- [x] 学生浏览招聘信息
- [x] 学生申请职位
- [x] 企业查看申请并处理

### 第三次迭代

- [x] 校企双方发布合作项目
- [x] 校企双方管理合作项目
- [x] 公开浏览合作项目 