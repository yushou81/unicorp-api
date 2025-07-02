package com.csu.unicorp.config.security;

import com.csu.unicorp.entity.user.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * 自定义UserDetails实现，包装User实体类用于Spring Security认证
 */
public class CustomUserDetails implements UserDetails {
    
    @Getter
    private final User user;
    private final List<GrantedAuthority> authorities;
    
    public CustomUserDetails(User user, String role) {
        this.user = user;
        this.authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }
    
    /**
     * 获取用户所属组织ID
     * 
     * @return 组织ID
     */
    public Integer getOrganizationId() {
        return user.getOrganizationId();
    }
    
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
    
    @Override
    public String getPassword() {
        return user.getPassword();
    }
    
    @Override
    public String getUsername() {
        return user.getAccount();
    }
    
    public Integer getUserId() {
        return user.getId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    @Override
    public boolean isEnabled() {
        return "active".equals(user.getStatus());
    }
} 