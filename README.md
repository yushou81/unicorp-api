# Linknei API 项目

## 项目概述
Linknei API是内江移动支撑项目平台的后端API服务，基于Spring Boot框架开发，提供RESTful API接口支持前端应用。

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
  │   ├── MerchantController.java  # 商户相关API
  │   └── UserController.java      # 用户相关API
  ├── dto/                         # 数据传输对象
  │   ├── LoginDTO.java            # 用户登录DTO
  │   └── UserDTO.java             # 用户注册DTO
  ├── entity/                      # 实体类
  │   ├── Merchant.java            # 商户实体
  │   ├── Product.java             # 产品实体
  │   └── User.java                # 用户实体
  ├── mapper/                      # Mapper接口
  │   ├── MerchantMapper.java      # 商户数据访问接口
  │   ├── ProductMapper.java       # 产品数据访问接口
  │   └── UserMapper.java          # 用户数据访问接口
  ├── service/                     # 服务接口
  │   ├── MerchantService.java     # 商户服务接口
  │   ├── UserService.java         # 用户服务接口
  │   └── impl/                    # 服务实现
  │       ├── MerchantServiceImpl.java   # 商户服务实现
  │       ├── UserDetailsServiceImpl.java # Spring Security用户服务实现
  │       └── UserServiceImpl.java       # 用户服务实现
  └── vo/                          # 视图对象
      ├── JwtResponseVO.java       # JWT响应对象
      ├── MerchantDetailVO.java    # 商户详情视图对象
      ├── MerchantSummaryVO.java   # 商户摘要视图对象
      ├── PageResultVO.java        # 分页结果视图对象
      ├── ProductSummaryVO.java    # 产品摘要视图对象
      └── ResultVO.java            # 统一响应结果对象
```

resources/
  ├── application.yml      # 应用配置文件
  ├── mapper/              # MyBatis映射文件
  ├── db/migration/        # 数据库迁移脚本
  ├── static/              # 静态资源
  └── templates/           # 模板文件
```

## 功能特性
- 用户注册：支持用户名和密码注册，密码采用BCrypt加密存储
- 用户登录：基于JWT的身份验证，生成令牌
- 数据验证：使用Spring Validation进行入参验证，确保数据合法性
- 接口保护：只有登录用户才能访问受保护的接口
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

## JWT认证说明
系统使用JWT(JSON Web Token)实现无状态的用户身份验证：

1. 用户登录成功后，服务器生成JWT令牌并返回给客户端
2. 客户端需要在后续请求中，在请求头中添加`Authorization: Bearer {token}`
3. 服务器验证令牌的有效性并识别用户身份
4. 令牌默认有效期为24小时
5. 令牌过期后，客户端需要重新登录获取新令牌

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