package com.csu.unicorp.config;

import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.csu.unicorp.interceptor.ResourceImageInterceptor;

import lombok.RequiredArgsConstructor;

/**
 * Web配置类
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    
    @Value("${app.upload.dir:upload}")
    private String uploadDir;
    
    private final ResourceImageInterceptor resourceImageInterceptor;
    
    /**
     * 配置MultipartResolver用于处理文件上传
     */
    @Bean
    public MultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }
    
    /**
     * 配置静态资源访问
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置上传文件的访问路径
        String uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize().toString();

        //原来的，就这样注释吧
        // registry.addResourceHandler("/files/**")
        //         .addResourceLocations("file:" + uploadPath + "/");


        //我之前写的，先别删，我再看看
        registry.addResourceHandler("/v1/files/**")
                .addResourceLocations("file:" + uploadPath + "/");
                
        // 配置静态资源路径，但明确排除API路径
        registry.addResourceHandler("/resources/**")
                .addResourceLocations("classpath:/static/resources/");
    }
    
    /**
     * 添加拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 添加资源图片拦截器，检查访问权限
        registry.addInterceptor(resourceImageInterceptor)
                .addPathPatterns("/v1/files/resource_images/**")
                .addPathPatterns("/api/v1/files/resource_images/**");
    }
    
    /**
     * 配置内容协商
     */
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer
            .favorParameter(false)
            .ignoreAcceptHeader(false)
            .defaultContentType(MediaType.APPLICATION_JSON)
            .mediaType("json", MediaType.APPLICATION_JSON)
            .mediaType("multipart", MediaType.MULTIPART_FORM_DATA);
    }
    
    /**
     * 配置消息转换器
     */
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 确保MappingJackson2HttpMessageConverter只处理application/json类型
        for (HttpMessageConverter<?> converter : converters) {
            if (converter instanceof MappingJackson2HttpMessageConverter) {
                MappingJackson2HttpMessageConverter jsonConverter = (MappingJackson2HttpMessageConverter) converter;
                // 设置只处理application/json
                jsonConverter.setSupportedMediaTypes(List.of(MediaType.APPLICATION_JSON));
            }
        }
    }
} 