# 学生成果展示平台API测试集合

这个Postman集合包含了学生成果展示平台的所有API接口，用于测试和开发。

## 使用说明

1. 下载并安装 [Postman](https://www.postman.com/downloads/)
2. 导入集合文件 `achievement-api-tests.json`
3. 导入环境配置文件 `achievement-api-environment.json`
4. 在Postman右上角选择"学生成果展示平台API环境"
5. 设置环境变量：
   - `base_url`: API的基础URL，默认为 `http://localhost:8080`
   - `student_token`: 学生用户的认证令牌
   - `teacher_token`: 教师用户的认证令牌
   - `enterprise_mentor_token`: 企业导师的认证令牌
   - `sysadmin_token`: 系统管理员的认证令牌
   - `enterprise_admin_token`: 企业管理员的认证令牌
   - `school_admin_email`: 学校管理员的邮箱地址

## 集合结构

集合分为五个主要部分：

1. **认证相关接口**：用于切换不同用户角色
2. **统计相关接口**：获取学生成果统计信息
3. **竞赛获奖相关接口**：管理学生的竞赛获奖信息
4. **作品集相关接口**：管理学生的作品项目
5. **科研成果相关接口**：管理学生的科研成果

## 认证说明

大部分接口需要认证才能访问。集合中已经配置了不同角色的认证令牌：

- 学生用户：使用 `student_token` 环境变量
- 教师用户：使用 `teacher_token` 环境变量
- 企业导师：使用 `enterprise_mentor_token` 环境变量
- 系统管理员：使用 `sysadmin_token` 环境变量
- 企业管理员：使用 `enterprise_admin_token` 环境变量

可以通过"认证相关接口"文件夹中的请求快速切换不同用户角色。

## 测试流程建议

1. 设置所有环境变量
2. 使用"认证相关接口"中的请求验证各个用户角色
3. 创建作品/获奖/科研成果
4. 获取列表和详情
5. 上传相关资源
6. 更新信息
7. 测试认证流程（使用教师或管理员角色）
8. 删除测试数据

## 注意事项

- 文件上传接口需要在Postman中选择实际文件
- 分页接口的page参数从1开始，但后端实际处理时会减1（因为MyBatis-Plus的Page对象从0开始计数）
- 认证相关接口需要教师或管理员权限，已在请求中配置相应的认证令牌 