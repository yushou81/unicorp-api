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
  │   │   │               │   ├── constants/
  │   │   │               │   │   └── RoleConstants.java        # 角色常量定义
  │   │   │               │   ├── exception/
  │   │   │               │   │   └── BusinessException.java    # 业务异常类
  │   │   │               │   └── utils/
  │   │   │               │       ├── AccountGenerator.java     # 账号生成工具
  │   │   │               │       └── JwtUtil.java              # JWT工具类
  │   │   │               ├── config/
  │   │   │               │   ├── SecurityConfig.java           # Spring Security配置
  │   │   │               │   └── security/
  │   │   │               │       ├── CustomUserDetails.java    # 自定义UserDetails实现
  │   │   │               │       └── JwtAuthenticationFilter.java # JWT认证过滤器
  │   │   │               ├── controller/
  │   │   │               │   ├── AdminController.java          # 系统管理员接口
  │   │   │               │   ├── ApplicationController.java    # 项目申请接口
  │   │   │               │   ├── AuthController.java           # 认证接口
  │   │   │               │   ├── EnterpriseAdminController.java # 企业管理员接口
  │   │   │               │   ├── OrganizationController.java   # 组织接口
  │   │   │               │   └── SchoolAdminController.java    # 学校管理员接口
  │   │   │               ├── dto/
  │   │   │               │   ├── EnterpriseRegistrationDTO.java # 企业注册DTO
  │   │   │               │   ├── LoginCredentialsDTO.java      # 登录凭证DTO
  │   │   │               │   ├── OrgMemberCreationDTO.java     # 组织成员创建DTO
  │   │   │               │   ├── OrgMemberUpdateDTO.java       # 组织成员更新DTO
  │   │   │               │   ├── SchoolCreationDTO.java        # 学校创建DTO
  │   │   │               │   └── StudentRegistrationDTO.java   # 学生注册DTO
  │   │   │               ├── entity/
  │   │   │               │   ├── EnterpriseDetail.java         # 企业详情实体
  │   │   │               │   ├── Organization.java             # 组织实体
  │   │   │               │   ├── Role.java                     # 角色实体
  │   │   │               │   ├── User.java                     # 用户实体
  │   │   │               │   └── UserVerification.java         # 用户验证实体
  │   │   │               ├── mapper/
  │   │   │               │   ├── EnterpriseDetailMapper.java   # 企业详情Mapper
  │   │   │               │   ├── OrganizationMapper.java       # 组织Mapper
  │   │   │               │   ├── RoleMapper.java               # 角色Mapper
  │   │   │               │   ├── UserMapper.java               # 用户Mapper
  │   │   │               │   └── UserVerificationMapper.java   # 用户验证Mapper
  │   │   │               ├── service/
  │   │   │               │   ├── EnterpriseService.java        # 企业服务接口
  │   │   │               │   ├── OrganizationService.java      # 组织服务接口
  │   │   │               │   ├── RoleService.java              # 角色服务接口
  │   │   │               │   ├── UserService.java              # 用户服务接口
  │   │   │               │   └── impl/
  │   │   │               │       ├── EnterpriseServiceImpl.java # 企业服务实现
  │   │   │               │       ├── OrganizationServiceImpl.java # 组织服务实现
  │   │   │               │       ├── RoleServiceImpl.java      # 角色服务实现
  │   │   │               │       └── UserServiceImpl.java      # 用户服务实现
  │   │   │               └── vo/
  │   │   │                   ├── OrganizationSimpleVO.java     # 组织简化VO
  │   │   │                   ├── OrganizationVO.java           # 组织VO
  │   │   │                   ├── ResultVO.java                 # 统一响应VO
  │   │   │                   ├── TokenVO.java                  # 令牌VO
  │   │   │                   └── UserVO.java                   # 用户VO
  │   │   └── resources/
  │   │       ├── application.yml                               # 应用配置文件
  │   │       └── db/
  │   │           └── migration/                                # Flyway数据库迁移脚本
  │   └── test/
  │       └── java/
  │           └── com/
  │               └── csu/
  │                   └── unicorp/
  │                       └── ...                               # 单元测试
  ├── docs/
  │   └── 第1次迭代/
  │       ├── 第一次迭代API5.0.md                               # 旧版API设计文档
  │       ├── 第一次迭代API6.0.md                               # 新版API设计文档
  │       └── 测试计划.md                                       # 测试计划文档
  ├── pom.xml                                                   # Maven配置文件
  └── README.md                                                 # 项目说明文档
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
   
   启动后访问: http://localhost:8081/api/swagger-ui.html
   
   **操作指南**:
   
   1. 首先需要登录系统获取JWT令牌:
      - 在Swagger UI界面找到 `/v1/auth/login` 接口（Authentication 标签组下）
      - 点击"Try it out"按钮，输入以下登录信息:
        ```json
        {
          "loginType": "account",
          "principal": "admin1",
          "password": "admin123"
        }
        ```
      - 点击"Execute"执行请求
      - 从响应中复制返回的token值
   
   2. 授权使用:
      - 点击Swagger UI页面右上角的"Authorize"按钮
      - 在弹出的窗口中，输入 `Bearer {你的token}` (将{你的token}替换为上一步复制的token值)
      - 点击"Authorize"按钮确认
   
   3. 现在您已完成授权，可以调用需要权限的API了:
      - 系统管理员可以创建学校、审核企业注册等
      - 学校管理员可以管理教师账号
      - 企业管理员可以管理企业导师账号
   
   4. 注意: JWT令牌有效期为24小时，过期后需要重新登录获取新令牌

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

## 功能模块

### 1. 用户认证与注册

- 支持账号、邮箱和手机号登录
- 学生注册功能
- 企业注册功能（需审核）
- JWT令牌生成与验证

### 2. 组织管理

- 系统管理员创建学校
- 获取学校列表
- 企业注册审核

### 3. 用户管理

- 学校管理员管理教师账号
  - 创建教师账号
  - 查询教师列表
  - 更新教师信息
  - 禁用教师账号
- 企业管理员管理企业导师账号
  - 创建企业导师账号
  - 查询导师列表
  - 更新导师信息
  - 禁用导师账号

## API文档

API文档使用Swagger UI生成，启动应用后访问：http://localhost:8080/api/swagger-ui.html

## 数据库设计

主要表结构：

1. `users` - 用户表
2. `roles` - 角色表
3. `user_roles` - 用户角色关联表
4. `organizations` - 组织表
5. `enterprise_details` - 企业详情表
6. `user_verifications` - 用户验证表

## 开发指南

### 环境要求

- JDK 17+
- Maven 3.8+
- MySQL 8.0+

### 本地运行

1. 克隆项目
```bash
git clone <repository-url>
cd unicorp-api
```

2. 配置数据库
编辑 `src/main/resources/application.yml` 文件，修改数据库连接信息

3. 构建并运行
```bash
mvn clean package
java -jar target/unicorp-api-0.0.1-SNAPSHOT.jar
```

或者使用Maven插件运行：
```bash
mvn spring-boot:run
```

### 测试

执行单元测试：
```bash
mvn test
```

API测试请参考 `docs/第1次迭代/测试计划.md` 文件，使用Postman进行测试。

## 版本历史

- v0.1.0 - 初始版本，实现基础认证和组织管理功能
- v0.2.0 - 添加教师和企业导师管理功能 