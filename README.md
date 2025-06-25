# 校企联盟平台 API 项目

## 项目概述
校企联盟平台是一个致力于优化人力资源配置、提升企业人才队伍建设的系统，基于Spring Boot框架开发，提供RESTful API接口支持前端应用。本平台大力开展人才招募、技能培训、赛事组织、内培分享等公益性、行业性活动，助推企业创新驱动发展和产业提档升级。

## 技术栈
- **Java 17**: 编程语言
- **Spring Boot 3.4.7**: 核心框架
- **Spring Security**: 安全认证
- **JWT**: JSON Web Token用于身份验证
- **MyBatis-Plus**: 数据库ORM框架
- **MySQL**: 数据库
- **SpringDoc OpenAPI**: API文档自动生成
- **Spring Validation**: 数据验证框架
- **Maven**: 项目管理与构建工具

## 项目结构
```
src/main/java/com/csu/linkneiapi/
  ├── LinkneiApiApplication.java   # 应用入口类
  ├── ServletInitializer.java      # Servlet初始化类
  ├── common/                      # 通用组件
  │   ├── exception/               # 异常处理
  │   │   ├── BusinessException.java          # 业务异常类
  │   │   ├── GlobalExceptionHandler.java     # 全局异常处理器
  │   │   └── JwtAuthenticationException.java # JWT认证异常
  │   └── utils/                   # 工具类
  │       └── JwtUtils.java        # JWT工具类
  ├── config/                      # 配置类
  │   ├── JwtAuthenticationFilter.java # JWT认证过滤器
  │   ├── MybatisPlusConfig.java       # MyBatis-Plus配置
  │   ├── MyMetaObjectHandler.java     # MyBatis-Plus字段自动填充处理器
  │   ├── OpenApiConfig.java           # OpenAPI (Swagger) 配置
  │   ├── SecurityConfig.java          # Spring Security配置
  │   └── migration/                   # 数据库迁移配置
  ├── controller/                  # 控制器层
  │   ├── EnterpriseController.java  # 企业相关API
  │   ├── JobPostController.java     # 招聘岗位API
  │   ├── UserController.java        # 用户相关API
  │   └── UserProfileController.java # 用户个人资料管理API
  ├── dto/                         # 数据传输对象
  │   ├── LoginDTO.java            # 用户登录DTO
  │   ├── ProfileUpdateDTO.java    # 用户简历更新DTO
  │   └── UserDTO.java             # 用户注册DTO
  ├── entity/                      # 实体类
  │   ├── Enterprise.java          # 企业实体
  │   ├── EnterpriseMember.java    # 企业成员实体
  │   ├── JobApplication.java      # 职位申请实体
  │   ├── JobPost.java             # 招聘岗位实体
  │   ├── User.java                # 用户实体
  │   └── UserProfile.java         # 用户简历/档案实体
  ├── mapper/                      # Mapper接口
  │   ├── EnterpriseMapper.java    # 企业数据访问接口
  │   ├── EnterpriseMemberMapper.java # 企业成员数据访问接口
  │   ├── JobApplicationMapper.java # 职位申请数据访问接口
  │   ├── JobPostMapper.java       # 招聘岗位数据访问接口
  │   ├── UserMapper.java          # 用户数据访问接口
  │   └── UserProfileMapper.java   # 用户简历数据访问接口
  ├── service/                     # 服务接口
  │   ├── EnterpriseService.java   # 企业服务接口
  │   ├── JobPostService.java      # 招聘岗位服务接口
  │   ├── UserService.java         # 用户服务接口
  │   ├── UserProfileService.java  # 用户简历服务接口
  │   └── impl/                    # 服务实现
  │       ├── EnterpriseServiceImpl.java   # 企业服务实现
  │       ├── JobPostServiceImpl.java      # 招聘岗位服务实现
  │       ├── UserDetailsServiceImpl.java  # Spring Security用户服务实现
  │       ├── UserProfileServiceImpl.java  # 用户简历服务实现
  │       └── UserServiceImpl.java         # 用户服务实现
  └── vo/                          # 视图对象
      ├── EnterpriseDetailVO.java  # 企业详情视图对象
      ├── EnterpriseSummaryVO.java # 企业摘要视图对象
      ├── JobPostDetailVO.java     # 岗位详情视图对象
      ├── JobPostSummaryVO.java    # 岗位摘要视图对象
      ├── JwtResponseVO.java       # JWT响应对象
      ├── PageResultVO.java        # 分页结果视图对象
      ├── ResultVO.java            # 统一响应结果对象
      └── UserProfileVO.java       # 用户简历视图对象
```

resources/
  ├── application.yml      # 应用配置文件
  ├── mapper/              # MyBatis映射文件
  ├── db/migration/        # 数据库迁移脚本
  ├── static/              # 静态资源
  └── templates/           # 模板文件
```

## 功能特性
- 用户注册与认证：支持用户名和密码注册，密码采用BCrypt加密存储，基于JWT的身份验证
- 简历档案管理：用户可以创建和维护个人简历，包括教育背景、工作经历等
- 企业认证管理：支持企业认证流程，多成员角色管理
- 招聘岗位发布：企业可以发布招聘信息，设置职位要求和薪资范围
- 求职申请流程：求职者可以在线投递简历，查看申请状态
- 数据权限控制：基于角色的权限管理，保障企业和个人数据安全
- 全局异常处理：统一处理系统异常，返回友好提示
- API文档：基于OpenAPI 3.0的自动生成API文档

## 配置说明
主要配置项位于`application.yml`文件：
- 服务端口: 8081
- 接口路径前缀: /api
- 数据库连接: MySQL
- JWT配置:
  - 密钥: jwt.secret
  - 过期时间: 24小时
- SpringDoc配置:
  - API文档路径: /api/v3/api-docs
  - Swagger UI路径: /api/swagger-ui.html
- MyBatis-Plus配置:
  - 支持驼峰命名转换
  - 逻辑删除功能
  - SQL日志打印

## 开发指南
### 环境准备
- JDK 17+
- Maven 3.6+
- MySQL 8.0+

### 本地开发
1. 克隆项目到本地
2. 创建数据库`linknei_db`
3. 修改`application.yml`中的数据库连接信息
4. 执行Maven命令启动项目：
   ```bash
   mvn spring-boot:run
   ```

### API文档
项目集成了SpringDoc，提供了基于OpenAPI 3.0的API文档：
- 访问Swagger UI: http://localhost:8081/api/swagger-ui.html
- 获取OpenAPI JSON: http://localhost:8081/api/v3/api-docs

通过Swagger UI，您可以：
- 查看所有API的详细信息
- 尝试API调用，包括接口测试
- 了解请求和响应的数据结构

### 接口文档
- 用户注册: POST /api/user/register
  - 请求体: 
    ```json
    {
      "username": "用户名",
      "password": "密码"
    }
    ```
  - 响应:
    ```json
    {
      "code": 200,
      "message": "注册成功！",
      "data": null
    }
    ```

- 用户登录: POST /api/user/login
  - 请求体: 
    ```json
    {
      "username": "用户名",
      "password": "密码"
    }
    ```
  - 响应:
    ```json
    {
      "code": 200,
      "message": "操作成功",
      "data": {
        "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
        "type": "Bearer",
        "username": "用户名"
      }
    }
    ```

- 获取用户信息: GET /api/user/info
  - 请求头: 
    ```
    Authorization: Bearer {token}
    ```
  - 响应:
    ```json
    {
      "code": 200,
      "message": "操作成功",
      "data": "当前登录用户: 用户名"
    }
    ```

- 获取用户个人资料: GET /api/user/profile
  - 请求头: 
    ```
    Authorization: Bearer {token}
    ```
  - 响应:
    ```json
    {
      "code": 200,
      "message": "操作成功",
      "data": {
        "username": "用户名",
        "nickname": "用户昵称",
        "avatarUrl": "头像URL",
        "phone": "138****8888",
        "role": "USER"
      }
    }
    ```

- 更新用户个人资料: PUT /api/user/profile
  - 请求头: 
    ```
    Authorization: Bearer {token}
    ```
  - 请求体:
    ```json
    {
      "nickname": "新昵称",
      "avatarUrl": "新头像URL",
      "phone": "13900000000"
    }
    ```
  - 响应:
    ```json
    {
      "code": 200,
      "message": "操作成功",
      "data": null
    }
    ```

## JWT认证说明
系统使用JWT(JSON Web Token)实现无状态的用户身份验证：

1. 用户登录成功后，服务器生成JWT令牌并返回给客户端
2. 客户端需要在后续请求中，在请求头中添加`Authorization: Bearer {token}`
3. 服务器验证令牌的有效性并识别用户身份
4. 令牌默认有效期为24小时
5. 令牌过期后，客户端需要重新登录获取新令牌

## 测试说明
项目包含完整的测试套件，用于确保功能正确性和代码质量：

### 测试结构
测试代码位于`src/test/java`目录下，主要包含：

- 单元测试：测试各个组件的独立功能
  - 服务层测试（`service/`）
  - 控制器测试（`controller/`）
- 集成测试：测试组件之间的协作

### 测试技术
- JUnit 5：测试框架
- Mockito：模拟依赖对象
- MockMvc：模拟HTTP请求
- H2 Database：内存数据库用于测试

### 运行测试
执行所有测试：
```bash
mvn test
```

执行特定测试类：
```bash
mvn test -Dtest=AuthServiceTest
```

### 测试文档
详细的测试文档位于`docs/`目录下：
- 测试文档-登录注册功能.md：登录注册功能的测试用例和结果

## 部署说明
项目打包为JAR文件，可以通过以下命令构建：
```bash
mvn clean package
```

启动应用：
```bash
java -jar target/linknei-api-0.0.1-SNAPSHOT.jar
```

## 后续开发计划
- 权限管理
- 更多业务功能接口开发

## 维护者
- 中南大学开发团队 