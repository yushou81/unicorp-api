package com.csu.linkneiapi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * 测试环境MyBatis-Plus配置类
 */
@Configuration
@Profile("test") // 仅在测试环境生效
public class TestMybatisPlusConfig {
    // 在MyBatis-Plus 3.5.12版本中不需要显式配置分页插件，它会自动处理分页
} 