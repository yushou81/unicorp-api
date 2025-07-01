package com.csu.unicorp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

/**
 * Web配置类
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Value("${file.upload-dir:upload}")
    private String uploadDir;
    
    /**
     * 配置静态资源访问
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置上传文件的访问路径
        String uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize().toString();
        // registry.addResourceHandler("/files/**")
        //         .addResourceLocations("file:" + uploadPath + "/");
        registry.addResourceHandler("/api/v1/files/**")
                .addResourceLocations("file:" + uploadPath + "/");
    }
} 