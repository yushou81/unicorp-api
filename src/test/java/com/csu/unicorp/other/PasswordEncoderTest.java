package com.csu.unicorp.other;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.context.annotation.Import;
import com.csu.unicorp.config.SecurityConfig;

@SpringBootTest
@Import(SecurityConfig.class)
public class PasswordEncoderTest {
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Test
    public void encodePassword() {
        // 需要加密的密码列表
        String[] passwords = {
            "admin123", 
            "123456", 
            "password", 
            "test123", 
            "unicorp2025"
        };
        
        System.out.println("===== 密码加密结果 =====");
        for (String password : passwords) {
            // 使用注入的passwordEncoder进行加密
            String encoded = passwordEncoder.encode(password);
            System.out.println("原始密码: " + password);
            System.out.println("加密结果: " + encoded);
            System.out.println("验证结果: " + passwordEncoder.matches(password, encoded));
            System.out.println("--------------------");
        }
        
        // 如果您需要加密自定义密码，可以在这里添加
        // String customPassword = "您的自定义密码";
        // System.out.println("自定义密码: " + customPassword);
        // System.out.println("加密结果: " + passwordEncoder.encode(customPassword));
    }
    
    /**
     * 自定义密码加密测试
     * 运行时，右键点击此方法 -> Run或使用@ParameterizedTest注解传参
     * @param password 要加密的密码
     */
    @ParameterizedTest
    @ValueSource(strings = {"your_custom_password_here"}) // 替换为您想加密的密码
    public void encodeCustomPassword(String password) {
        System.out.println("\n===== 自定义密码加密 =====");
        System.out.println("原始密码: " + password);
        String encoded = passwordEncoder.encode(password);
        System.out.println("加密结果: " + encoded);
        System.out.println("验证结果: " + passwordEncoder.matches(password, encoded));
    }
}
