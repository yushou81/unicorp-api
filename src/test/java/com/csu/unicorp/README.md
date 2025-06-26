# API测试指南

本文档提供了对项目API进行测试的多种方法和最佳实践。

## 测试类型

### 1. 单元测试

单元测试主要针对特定的功能单元（通常是一个方法或类），隔离外部依赖进行测试。

**示例：** `UserServiceTest.java`

```java
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserMapper userMapper;
    
    @InjectMocks
    private UserServiceImpl userService;
    
    @Test
    public void testRegisterSuccess() {
        // 模拟依赖行为
        when(userMapper.selectByUsername(anyString())).thenReturn(null);
        
        // 执行测试并验证结果
        assertDoesNotThrow(() -> userService.register(userDTO));
    }
}
```

**适用场景：**
- 测试业务逻辑
- 验证边界条件和异常处理
- 快速反馈代码质量

### 2. 控制器测试

控制器测试使用`MockMvc`模拟HTTP请求，测试API端点的行为，但不启动完整的服务器。

**示例：** `UserControllerTest.java`

```java
@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private UserService userService;
    
    @Test
    public void testRegisterSuccess() throws Exception {
        // 模拟Service层行为
        doNothing().when(userService).register(any(UserDTO.class));
        
        // 执行请求并验证结果
        mockMvc.perform(post("/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}
```

**适用场景：**
- 测试API请求/响应处理
- 验证URL映射和请求参数绑定
- 测试控制器层的异常处理

### 3. 集成测试

集成测试启动完整的应用程序上下文，测试多个组件的协同工作。

**示例：** `UserApiIntegrationTest.java`

```java
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserApiIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    public void testRegisterAndLoginFlow() throws Exception {
        // 执行注册请求
        mockMvc.perform(post("/user/register")...)
                .andExpect(status().isOk());
        
        // 执行登录请求
        mockMvc.perform(post("/user/login")...)
                .andExpect(status().isOk());
    }
}
```

**适用场景：**
- 测试完整业务流程
- 验证组件间的交互
- 确保数据库操作正确执行

### 4. API端到端测试

使用真实的HTTP客户端测试API端点，启动完整的服务器。

#### 使用TestRestTemplate

**示例：** `TestRestTemplateApiTest.java`

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestRestTemplateApiTest {
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    public void testUserRegisterAndLogin() {
        // 发送真实HTTP请求
        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/user/register", userDTO, Map.class);
        
        assertEquals(200, response.getStatusCodeValue());
    }
}
```

#### 使用RestAssured

**示例：** `RestAssuredApiTest.java`

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RestAssuredApiTest {
    @Test
    public void testUserRegisterAndLogin() {
        // 使用RestAssured发送请求
        given()
            .contentType(ContentType.JSON)
            .body(userDTO)
        .when()
            .post("/user/register")
        .then()
            .statusCode(200)
            .body("code", equalTo(200));
    }
}
```

**适用场景：**
- 验证API的真实行为
- 测试认证和授权
- 模拟真实用户场景

## 测试环境配置

项目使用`application-test.yml`配置测试环境：

- 使用H2内存数据库进行测试
- 禁用Flyway迁移，使用JPA自动创建表
- 配置测试专用的JWT密钥
- 启用详细日志记录

## 最佳实践

1. **测试数据隔离**：使用`@Transactional`注解自动回滚测试数据
2. **测试用例独立**：每个测试用例应该独立运行，不依赖其他测试的状态
3. **使用随机值**：使用`System.currentTimeMillis()`等生成唯一标识符，避免测试冲突
4. **合理使用断言**：使用恰当的断言验证测试结果
5. **测试异常情况**：不仅测试正常流程，也要测试异常情况

## 外部工具测试

除了代码测试外，还可以使用以下工具进行API测试：

1. **Swagger UI**：访问`/swagger-ui.html`使用交互式文档测试API
2. **Postman**：创建请求集合，进行手动或自动化测试
3. **curl命令**：使用命令行工具快速测试API

```bash
# 注册用户
curl -X POST http://localhost:8080/user/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password123"}'

# 登录获取token
curl -X POST http://localhost:8080/user/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password123"}'
``` 