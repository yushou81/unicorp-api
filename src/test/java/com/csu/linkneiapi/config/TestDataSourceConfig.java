package com.csu.linkneiapi.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

/**
 * 测试数据源和MyBatis-Plus配置
 * 确保H2数据库和MyBatis-Plus在测试环境中正确配置
 */
@TestConfiguration
public class TestDataSourceConfig {

    /**
     * 配置H2嵌入式数据源
     * 使用EmbeddedDatabaseBuilder直接创建并初始化数据库
     */
    @Bean
    @Primary
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .setName("test_db;MODE=MySQL;DATABASE_TO_LOWER=TRUE")
                .addScript("classpath:db/schema-h2.sql")
                .addScript("classpath:db/data-h2.sql")
                .build();
    }
    
    /**
     * 配置MyBatis-Plus插件
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        return new MybatisPlusInterceptor();
    }
} 