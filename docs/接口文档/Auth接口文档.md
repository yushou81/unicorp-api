openapi: 3.0.3
info:
  title: 认证管理API
  description: 用户认证、注册及信息管理相关接口
  version: 1.0.0
  
servers:
  - url: /api
    description: API基础路径

tags:
  - name: Authentication
    description: 认证管理

components:
  schemas:
    LoginCredentialsDTO:
      type: object
      required:
        - principal
        - password
        - loginType
      properties:
        principal:
          type: string
          description: 登录标识(账号/邮箱/手机号)
        password:
          type: string
          description: 密码
        loginType:
          type: string
          enum: [account, email, phone]
          description: 登录类型
    
    TokenVO:
      type: object
      properties:
        token:
          type: string
          description: JWT认证令牌
        nickname:
          type: string
          description: 用户昵称
        role:
          type: string
          description: 用户角色
        avatar:
          type: string
          description: 用户头像URL
    
    StudentRegistrationDTO:
      type: object
      required:
        - realName
        - idCard
        - password
        - email
        - organizationId
      properties:
        realName:
          type: string
          description: 真实姓名
        idCard:
          type: string
          description: 身份证号
        password:
          type: string
          description: 密码
        email:
          type: string
          description: 电子邮箱
        phone:
          type: string
          description: 手机号
        nickname:
          type: string
          description: 昵称
        organizationId:
          type: integer
          description: 学校ID
          
    EnterpriseRegistrationDTO:
      type: object
      required:
        - organizationName
        - adminPassword
        - adminEmail
      properties:
        organizationName:
          type: string
          description: 企业名称
        description:
          type: string
          description: 企业描述
        address:
          type: string
          description: 企业地址
        website:
          type: string
          description: 企业网站
        industry:
          type: string
          description: 所属行业
        companySize:
          type: string
          description: 公司规模
        adminEmail:
          type: string
          description: 管理员邮箱
        adminPhone:
          type: string
          description: 管理员手机号
        adminPassword:
          type: string
          description: 管理员密码
        adminNickname:
          type: string
          description: 管理员昵称
          
    UserProfileUpdateDTO:
      type: object
      properties:
        email:
          type: string
          description: 电子邮箱
        phone:
          type: string
          description: 手机号
        nickname:
          type: string
          description: 昵称
          
    PasswordUpdateDTO:
      type: object
      required:
        - oldPassword
        - newPassword
      properties:
        oldPassword:
          type: string
          description: 原密码
        newPassword:
          type: string
          description: 新密码
          
    UserVO:
      type: object
      properties:
        id:
          type: integer
          description: 用户ID
        account:
          type: string
          description: 用户账号
        nickname:
          type: string
          description: 用户昵称
        email:
          type: string
          description: 用户邮箱
        phone:
          type: string
          description: 用户手机号
        avatar:
          type: string
          description: 用户头像URL
        status:
          type: string
          description: 用户状态
        organizationId:
          type: integer
          description: 所属组织ID
        organizationName:
          type: string
          description: 所属组织名称
        role:
          type: string
          description: 用户角色
        createdAt:
          type: string
          format: date-time
          description: 创建时间
          
    ResultVO:
      type: object
      properties:
        code:
          type: integer
          description: 状态码
        message:
          type: string
          description: 消息
        data:
          type: object
          description: 返回数据

  securitySchemes:
    bearerAuth:
      type: http
      scheme: bearer
      bearerFormat: JWT

paths:
  /v1/auth/login:
    post:
      tags:
        - Authentication
      summary: 通用登录接口
      description: 用户使用账号和密码登录，成功后返回JWT、用户昵称和角色信息
      operationId: login
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/LoginCredentialsDTO'
      responses:
        '200':
          description: 登录成功
          content:
            application/json:
              schema:
                type: object
                properties:
                  code:
                    type: integer
                    example: 200
                  message:
                    type: string
                    example: 登录成功
                  data:
                    $ref: '#/components/schemas/TokenVO'
        '401':
          description: 认证失败
          
  /v1/auth/register/student:
    post:
      tags:
        - Authentication
      summary: 学生注册接口
      description: 学生选择已存在的学校进行注册，并提供实名信息
      operationId: registerStudent
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/StudentRegistrationDTO'
      responses:
        '200':
          description: 学生注册成功
          content:
            application/json:
              schema:
                type: object
                properties:
                  code:
                    type: integer
                    example: 200
                  message:
                    type: string
                    example: 注册成功
                  data:
                    $ref: '#/components/schemas/UserVO'
        '400':
          description: 无效的输入，或账号/邮箱已存在
          
  /v1/auth/register/enterprise:
    post:
      tags:
        - Authentication
      summary: 企业注册接口
      description: 企业代表进行公开注册。注册后，企业和其管理员账号状态均为'pending'，需要系统管理员审核
      operationId: registerEnterprise
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/EnterpriseRegistrationDTO'
      responses:
        '200':
          description: 注册申请已提交，等待审核
          content:
            application/json:
              schema:
                type: object
                properties:
                  code:
                    type: integer
                    example: 200
                  message:
                    type: string
                    example: 企业注册申请已提交，等待审核
                  data:
                    $ref: '#/components/schemas/UserVO'
        '400':
          description: 无效的输入，或邮箱/手机号/企业名称已存在
  
  /v1/auth/me:
    get:
      tags:
        - Authentication
      summary: 获取当前登录用户信息
      description: 根据提供的JWT获取当前登录用户的详细信息
      operationId: getCurrentUser
      security:
        - bearerAuth: []
      responses:
        '200':
          description: 成功获取用户信息
          content:
            application/json:
              schema:
                type: object
                properties:
                  code:
                    type: integer
                    example: 200
                  message:
                    type: string
                    example: 获取用户信息成功
                  data:
                    $ref: '#/components/schemas/UserVO'
        '401':
          description: 未授权
          
  /v1/auth/profile:
    put:
      tags:
        - Authentication
      summary: 更新用户个人信息
      description: 允许用户修改自己的邮箱、手机号和昵称
      operationId: updateUserProfile
      security:
        - bearerAuth: []
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/UserProfileUpdateDTO'
      responses:
        '200':
          description: 信息更新成功
          content:
            application/json:
              schema:
                type: object
                properties:
                  code:
                    type: integer
                    example: 200
                  message:
                    type: string
                    example: 个人信息更新成功
                  data:
                    $ref: '#/components/schemas/UserVO'
        '400':
          description: 无效的输入，或邮箱/手机号已被其他用户使用
        '401':
          description: 未授权
          
  /v1/auth/password:
    put:
      tags:
        - Authentication
      summary: 修改用户密码
      description: 允许用户修改自己的登录密码，需要提供原密码进行验证
      operationId: updatePassword
      security:
        - bearerAuth: []
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/PasswordUpdateDTO'
      responses:
        '200':
          description: 密码修改成功
          content:
            application/json:
              schema:
                type: object
                properties:
                  code:
                    type: integer
                    example: 200
                  message:
                    type: string
                    example: 密码修改成功
        '400':
          description: 原密码不正确
        '401':
          description: 未授权
          
  /v1/auth/avatar:
    post:
      tags:
        - Authentication
      summary: 上传用户头像
      description: 允许用户上传自己的头像
      operationId: updateAvatar
      security:
        - bearerAuth: []
      requestBody:
        required: true
        content:
          multipart/form-data:
            schema:
              type: object
              properties:
                file:
                  type: string
                  format: binary
                  description: 头像图片文件
      responses:
        '200':
          description: 头像上传成功
          content:
            application/json:
              schema:
                type: object
                properties:
                  code:
                    type: integer
                    example: 200
                  message:
                    type: string
                    example: 头像上传成功
                  data:
                    $ref: '#/components/schemas/UserVO'
        '400':
          description: 无效的文件格式或大小
        '401':
          description: 未授权