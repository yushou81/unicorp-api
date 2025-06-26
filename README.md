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
  src/
    main/
      java/
        com/
          csu/
            unicorp/
              common/             # 公共组件
                exception/        # 全局异常处理
                utils/            # 工具类
              config/             # 配置类
              controller/         # 控制器
              dto/                # 数据传输对象
              entity/             # 数据库实体
                Organization.java # 组织实体 (organizations)
                SchoolDetail.java # 学校详情 (school_details)
                EnterpriseDetail.java # 企业详情 (enterprise_details)
                User.java         # 用户实体 (users)
                Role.java         # 角色实体 (roles)
                UserRole.java     # 用户角色关联 (user_roles)
                Permission.java   # 权限实体 (permissions)
                RolePermission.java # 角色权限关联 (role_permissions)
                StudentProfile.java # 学生档案 (student_profiles)
                Job.java          # 招聘岗位 (jobs)
                Application.java  # 岗位申请 (applications)
                Project.java      # 合作项目 (projects)
                Resource.java     # 共享资源 (resources)
                DualTeacherCourse.java # 双师课堂 (dual_teacher_courses)
                AuditLog.java     # 系统日志 (audit_logs)
              mapper/             # Mybatis 映射器
              service/            # 服务层
                impl/             # 服务实现
              vo/                 # 视图对象
                ResultVO.java     # 通用响应视图
      resources/
        db/migration/            # 数据库迁移脚本
        mapper/                  # MyBatis XML映射文件
        application.yml          # 应用配置
```

## 数据库命名规范

项目采用以下数据库命名规范：

1. **表名**：使用蛇形命名法（snake_case），如 `organizations`, `user_roles`
2. **字段名**：使用蛇形命名法，如 `organization_id`, `created_at`
3. **主键**：所有表主键统一命名为 `id`

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