package com.csu.unicorp.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import static org.springframework.security.config.Customizer.withDefaults;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.csu.unicorp.common.constants.RoleConstants;
import com.csu.unicorp.config.security.JwtAuthenticationFilter;
import com.csu.unicorp.config.security.OAuth2DebugFilter;
import com.csu.unicorp.config.security.OAuth2LoginSuccessHandler;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Spring Security配置类
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {
    
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final OAuth2DebugFilter oAuth2DebugFilter;
    
    /**
     * CORS配置 - 允许所有来源访问
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // 允许所有来源
        configuration.setAllowedOriginPatterns(List.of("*"));
        // 允许所有HTTP方法
        configuration.setAllowedMethods(List.of("*"));
        // 允许所有请求头
        configuration.setAllowedHeaders(List.of("*"));
        // 允许暴露的响应头
        configuration.setExposedHeaders(List.of("Authorization", "Content-Type"));
        // 允许凭证
        configuration.setAllowCredentials(true);
        // 缓存预检请求结果的时间（秒）
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    
    /**
     * 安全过滤链配置
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                    // 公共接口
                    .requestMatchers("/v1/auth/login", "/v1/auth/register/**", "/v1/auth/refresh","/v1/auth/github/login-url").permitAll()
                    .requestMatchers("/v1/files/**").permitAll()
                    .requestMatchers("/v1/files/resumes/**").permitAll()
                    .requestMatchers("/v1/organizations/schools").permitAll()
                    .requestMatchers("/v1/jobs", "/v1/jobs/**","/v1/job-categories").permitAll()
                    .requestMatchers("/v1/projects", "/v1/projects/{id}").permitAll()
                    // WebSocket端点 - 允许所有访问，认证在WebSocketAuthInterceptor中处理
                    .requestMatchers("/ws/**").permitAll()
                    // 资源共享中心公开接口
                    .requestMatchers("/v1/resources", "/v1/resources/{id}").permitAll()
                    // 双师课堂公开接口
                    .requestMatchers("/v1/dual-courses").permitAll()
                    .requestMatchers("/v1/dual-courses/{id}").permitAll()
                    .requestMatchers("/v1/dual-courses/enrollable").permitAll()
                    // 双师课堂需要认证的接口
                    .requestMatchers("/v1/dual-courses/{id}/students").authenticated()
                    // 课程评价公开接口
                    .requestMatchers("/v1/course-ratings/course/{courseId}").permitAll()
                    .requestMatchers("/v1/course-ratings/{ratingId}").permitAll()
                    .requestMatchers("/v1/course-ratings/average/{courseId}").permitAll()
                    // 课程资源公开接口
                    .requestMatchers("/v1/course-resources/{resourceId}").permitAll()
                    .requestMatchers("/v1/course-resources/course/{courseId}").permitAll()
                    .requestMatchers("/v1/course-resources/download/{resourceId}").permitAll()
                    .requestMatchers("/v1/equipments", "/v1/equipments/{id}").permitAll()
                    // 成果统计公开接口
                    .requestMatchers("/api/v1/achievement/statistics/overview", "/api/v1/achievement/statistics/overview/{userId}").permitAll()
                    .requestMatchers("/v1/achievement/statistics/overview", "/v1/achievement/statistics/overview/{userId}").permitAll()
                    
                    // 社区公开接口
                    .requestMatchers("/v1/community/categories/**").permitAll()
                    .requestMatchers("/v1/community/topics", "/v1/community/topics/{topicId}").permitAll()
                    .requestMatchers("/v1/community/topics/category/{categoryId}").permitAll()
                    .requestMatchers("/v1/community/topics/hot", "/v1/community/topics/latest", "/v1/community/topics/essence").permitAll()
                    .requestMatchers("/v1/community/topics/user/{userId}").permitAll()
                    .requestMatchers("/v1/community/questions", "/v1/community/questions/{questionId}").permitAll()
                    .requestMatchers("/v1/community/questions/hot", "/v1/community/questions/latest", "/v1/community/questions/unsolved").permitAll()
                    .requestMatchers("/v1/community/questions/user/{userId}").permitAll()
                    .requestMatchers("/v1/community/answers/question/{questionId}").permitAll()
                    .requestMatchers("/v1/community/answers/{answerId}").permitAll()
                    .requestMatchers("/v1/community/answers/user/{userId}").permitAll()
                    .requestMatchers("/v1/community/comments/topic/{topicId}", "/v1/community/comments/answer/{answerId}").permitAll()
                    .requestMatchers("/v1/community/comments/{commentId}/replies").permitAll()
                    .requestMatchers("/v1/community/comments/user/{userId}").permitAll()
                    .requestMatchers("/v1/community/tags", "/v1/community/tags/hot", "/v1/community/tags/search").permitAll()
                    .requestMatchers("/v1/community/tags/{tagId}", "/v1/community/tags/topic/{topicId}", "/v1/community/tags/question/{questionId}").permitAll()
                    // OAuth2登录相关路径
                    .requestMatchers("/login/oauth2/code/**", "/oauth2/**").permitAll()
                    // 推荐系统公开接口
                    .requestMatchers("/v1/recommendations/behaviors").permitAll()
                    
                    // 用户搜索接口需要认证
                    .requestMatchers("/v1/auth/search").authenticated()
                    // 聊天接口需要认证
                    .requestMatchers("/v1/chat/**").authenticated()
                    // 科研成果封面上传接口需要认证
                    .requestMatchers("/api/v1/research/*/cover").authenticated()
                    .requestMatchers("/v1/research/*/cover").authenticated()

                    // 静态资源访问 - 权限验证通过拦截器而不是这里处理
                    .requestMatchers("/v1/files/resources/**").permitAll()
                    .requestMatchers("/v1/files/resource_images/**").permitAll()
                    .requestMatchers("/v1/files/resumes/**").authenticated()
                    .requestMatchers("/v1/files/avatars/**").permitAll()
                    // Swagger UI and API docs
                    .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                    // 管理接口需要SYSADMIN权限
                    .requestMatchers("/v1/admin/**").hasRole(RoleConstants.ROLE_SYSTEM_ADMIN)
                    // 学校管理员接口需要SCH_ADMIN权限
                    .requestMatchers("/v1/school-admin/**").hasRole(RoleConstants.ROLE_SCHOOL_ADMIN)
                    // 企业管理员接口需要EN_ADMIN权限
                    .requestMatchers("/v1/enterprise-admin/**").hasRole(RoleConstants.ROLE_ENTERPRISE_ADMIN)
                    // 系统管理员接口
                    .requestMatchers("/v1/admin/users/**").hasRole("SYSADMIN")
                    .requestMatchers("/v1/admin/organizations/**").hasRole("SYSADMIN")
                    .requestMatchers("/v1/admin/audit/**").hasRole("SYSADMIN")
                    // 其他所有请求需要认证
                    .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(oAuth2DebugFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                // 配置OAuth2登录
                .oauth2Login(oauth2 -> {
                    log.info("配置OAuth2登录");
                    oauth2.loginPage("/login")
                        .successHandler(oAuth2LoginSuccessHandler) // 直接使用注入的处理器
                        .failureHandler((request, response, exception) -> {
                            log.error("OAuth2登录失败: {}", exception.getMessage(), exception);
                            response.sendRedirect("http://localhost:8082/login-error?error=" + URLEncoder.encode(exception.getMessage(), StandardCharsets.UTF_8));
                        });
                    log.info("OAuth2登录配置完成");
                })
                .build();
    }
    
    /**
     * 认证管理器
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    /**
     * 认证提供者
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
    
    /**
     * 密码编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    /**
     * OpenAPI配置（Swagger文档）
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .info(new Info()
                        .title("UniCorp API")
                        .description("校企合作平台API文档")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("UniCorp Team")
                                .email("unicorp@example.com")));
    }
}
