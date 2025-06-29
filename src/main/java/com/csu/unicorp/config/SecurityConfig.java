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

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;

/**
 * Spring Security配置类
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;
    
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
                    .requestMatchers("/v1/auth/login", "/v1/auth/register/**").permitAll()
                    .requestMatchers("/v1/organizations/schools").permitAll()
                    .requestMatchers("/v1/jobs", "/v1/jobs/**").permitAll()
                    .requestMatchers("/v1/projects", "/v1/projects/{id}").permitAll()
                    // WebSocket端点 - 允许所有访问，认证在WebSocketAuthInterceptor中处理
                    .requestMatchers("/ws/**").permitAll()
                    // 资源共享中心公开接口
                    .requestMatchers("/v1/resources", "/v1/resources/{id}").permitAll()
                    // 聊天接口需要认证
                    .requestMatchers("/v1/chat/**").authenticated()
                    // 静态资源访问
                    .requestMatchers("/files/**").permitAll()
                    .requestMatchers("/v1/chat/**").authenticated()
                    // Swagger UI and API docs
                    .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                    // 管理接口需要SYSADMIN权限
                    .requestMatchers("/v1/admin/**").hasRole(RoleConstants.ROLE_SYSTEM_ADMIN)
                    // 学校管理员接口需要SCH_ADMIN权限
                    .requestMatchers("/v1/school-admin/**").hasRole(RoleConstants.ROLE_SCHOOL_ADMIN)
                    // 企业管理员接口需要EN_ADMIN权限
                    .requestMatchers("/v1/enterprise-admin/**").hasRole(RoleConstants.ROLE_ENTERPRISE_ADMIN)
                    // 其他所有请求需要认证
                    .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
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
                        .title("校企合作平台 API")
                        .description("校企合作平台 RESTful API 文档")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("CSU Team")
                                .email("support@unicorp.com")));
    }
}
