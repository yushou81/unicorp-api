# 控制器测试目录

本目录包含校企联盟平台API控制器层的测试代码。

## 测试类

- `AuthControllerTest.java` - 认证控制器测试，包含用户注册和登录功能的测试用例

## 测试模式

我们使用 Spring Boot 的 `@WebMvcTest` 和 `@SpringBootTest` 两种测试模式：

1. `@WebMvcTest` - 仅加载控制器层，测试专注于HTTP请求处理
2. `@SpringBootTest` + `@AutoConfigureMockMvc` - 加载完整应用上下文进行端到端测试

## 测试工具

- MockMvc - 模拟HTTP请求和验证响应
- ObjectMapper - 处理JSON序列化和反序列化
- Mockito - 模拟依赖服务

## 运行测试

```bash
# 运行所有控制器测试
mvn test -Dtest=*ControllerTest

# 运行特定控制器测试
mvn test -Dtest=AuthControllerTest
``` 