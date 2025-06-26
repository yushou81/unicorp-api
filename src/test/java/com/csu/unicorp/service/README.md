# 服务层测试目录

本目录包含校企联盟平台服务层的测试代码。

## 测试类

- `AuthServiceTest.java` - 用户认证服务测试，包含用户注册和登录功能的单元测试

## 测试方法

我们使用 JUnit 5 和 Mockito 进行服务层的单元测试：

1. 使用 `@Mock` 模拟依赖项（如数据库操作）
2. 使用 `@InjectMocks` 将模拟依赖自动注入到被测试服务
3. 通过 `when`/`thenReturn` 或 `doNothing`/`doThrow` 设定模拟行为
4. 验证服务方法的返回结果或异常抛出
5. 使用 `verify` 验证模拟对象的方法调用

## 测试范围

服务层测试专注于业务逻辑的正确性，包括：

- 成功场景测试
- 异常处理测试
- 边界条件测试

## 运行测试

```bash
# 运行所有服务测试
mvn test -Dtest=*ServiceTest

# 运行特定服务测试
mvn test -Dtest=AuthServiceTest
``` 