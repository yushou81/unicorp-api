package com.csu.unicorp.service.impl;

import java.time.LocalDateTime;
import java.util.Collection;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.unicorp.common.constants.RoleConstants;
import com.csu.unicorp.common.constants.VisibilityEnum;
import com.csu.unicorp.common.exception.ResourceNotFoundException;
import com.csu.unicorp.dto.ResourceCreationDTO;
import com.csu.unicorp.entity.Resource;
import com.csu.unicorp.entity.User;
import com.csu.unicorp.mapper.ResourceMapper;
import com.csu.unicorp.service.ResourceService;
import com.csu.unicorp.service.UserService;
import com.csu.unicorp.vo.ResourceVO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 资源服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {
    
    private final ResourceMapper resourceMapper;
    private final UserService userService;
    
    @Override
    public IPage<ResourceVO> getResources(int page, int size, String keyword) {
        Page<ResourceVO> pagination = new Page<>(page, size);
        return resourceMapper.selectResourcesWithUploader(pagination, keyword);
    }
    
    @Override
    public ResourceVO getResourceById(Integer id) {
        ResourceVO resource = resourceMapper.selectResourceWithUploaderById(id);
        if (resource == null) {
            throw new ResourceNotFoundException(id+"资源不存在");
        }
        
        // 对专利和著作权类型资源进行特殊处理
        if ("专利".equals(resource.getResourceType()) || "著作权".equals(resource.getResourceType())) {
            // 如果没有设置imageUrl，则使用fileUrl作为图片展示
            if (resource.getImageUrl() == null || resource.getImageUrl().isEmpty()) {
                resource.setImageUrl(resource.getFileUrl());
            }
        }
        
        return resource;
    }
    
    @Override
    @Transactional
    public ResourceVO createResource(ResourceCreationDTO resourceDTO, UserDetails userDetails) {
        // 检查用户是否有权限创建资源（教师或企业导师）
        if (!hasResourceManagementPermission(userDetails)) {
            throw new AccessDeniedException("只有教师,管理员或企业导师可以上传资源");
        }
        
        // 获取当前用户
        User user = userService.getByAccount(userDetails.getUsername());
        
        // 创建资源实体
        Resource resource = new Resource();
        resource.setTitle(resourceDTO.getTitle());
        resource.setDescription(resourceDTO.getDescription());
        resource.setResourceType(resourceDTO.getResourceType());
        resource.setFileUrl(resourceDTO.getFileUrl());
        resource.setUploadedByUserId(user.getId());
        
        // 设置可见性，默认为public
        String visibility = resourceDTO.getVisibility();
        if (visibility == null || visibility.isEmpty()) {
            visibility = VisibilityEnum.PUBLIC.name().toLowerCase();
        }
        resource.setVisibility(visibility);
        
        // 对专利和著作权类型资源进行特殊处理
        if ("专利".equals(resourceDTO.getResourceType()) || "著作权".equals(resourceDTO.getResourceType())) {
            // 设置imageUrl，如果没有提供则使用fileUrl
            if (resourceDTO.getImageUrl() != null && !resourceDTO.getImageUrl().isEmpty()) {
                resource.setImageUrl(resourceDTO.getImageUrl());
            } else {
                resource.setImageUrl(resourceDTO.getFileUrl());
            }
        } else {
            resource.setImageUrl(resourceDTO.getImageUrl());
        }
        
        // 设置时间
        LocalDateTime now = LocalDateTime.now();
        resource.setCreatedAt(now);
        resource.setUpdatedAt(now);
        
        // 保存资源
        resourceMapper.insert(resource);
        
        // 返回资源视图对象
        return getResourceById(resource.getId());
    }
    
    @Override
    @Transactional
    public ResourceVO updateResource(Integer id, ResourceCreationDTO resourceDTO, UserDetails userDetails) {
        // 检查资源是否存在
        Resource resource = resourceMapper.selectById(id);
        if (resource == null) {
            throw new ResourceNotFoundException("资源不存在");
        }
        
        // 检查用户是否有权限更新资源（资源所有者或管理员）
        User user = userService.getByAccount(userDetails.getUsername());
        boolean isOwner = resource.getUploadedByUserId().equals(user.getId());
        boolean isAdmin = isSystemAdmin(userDetails);
        
        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("只有资源所有者或管理员可以更新资源");
        }
        
        // 更新资源信息
        resource.setTitle(resourceDTO.getTitle());
        resource.setDescription(resourceDTO.getDescription());
        resource.setResourceType(resourceDTO.getResourceType());
        if (resourceDTO.getFileUrl() != null && !resourceDTO.getFileUrl().isEmpty()) {
            resource.setFileUrl(resourceDTO.getFileUrl());
        }
        
        // 设置可见性
        if (resourceDTO.getVisibility() != null && !resourceDTO.getVisibility().isEmpty()) {
            resource.setVisibility(resourceDTO.getVisibility());
        }
        
        // 对专利和著作权类型资源进行特殊处理
        if ("专利".equals(resourceDTO.getResourceType()) || "著作权".equals(resourceDTO.getResourceType())) {
            // 设置imageUrl，如果没有提供则使用fileUrl
            if (resourceDTO.getImageUrl() != null && !resourceDTO.getImageUrl().isEmpty()) {
                resource.setImageUrl(resourceDTO.getImageUrl());
            } else if (resource.getImageUrl() == null || resource.getImageUrl().isEmpty()) {
                resource.setImageUrl(resource.getFileUrl());
            }
        } else {
            if (resourceDTO.getImageUrl() != null) {
                resource.setImageUrl(resourceDTO.getImageUrl());
            }
        }
        
        // 更新时间
        resource.setUpdatedAt(LocalDateTime.now());
        
        // 保存更新
        resourceMapper.updateById(resource);
        
        // 返回更新后的资源
        return getResourceById(id);
    }
    
    @Override
    @Transactional
    public void deleteResource(Integer id, UserDetails userDetails) {
        // 检查资源是否存在
        Resource resource = resourceMapper.selectById(id);
        if (resource == null) {
            throw new ResourceNotFoundException("资源不存在");
        }
        
        // 检查用户是否有权限删除资源（资源所有者或管理员）
        User user = userService.getByAccount(userDetails.getUsername());
        boolean isOwner = resource.getUploadedByUserId().equals(user.getId());
        boolean isAdmin = isSystemAdmin(userDetails);
        
        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("只有资源所有者或管理员可以删除资源");
        }
        
        // 逻辑删除资源
        resourceMapper.deleteById(id);
    }
    
    @Override
    public boolean checkImageAccessPermission(String filename) {
        log.debug("检查资源图片访问权限: {}", filename);
        
        try {
            // 查询使用该图片的资源
            LambdaQueryWrapper<Resource> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Resource::getImageUrl, "resource_images/" + filename)
                   .or()
                   .eq(Resource::getFileUrl, "resource_images/" + filename);
            
            Resource resource = resourceMapper.selectOne(wrapper);
            
            // 如果找不到对应的资源，拒绝访问
            if (resource == null) {
                log.warn("找不到与图片关联的资源: {}", filename);
                return false;
            }
            
            // 如果资源是公开的，允许访问
            if (VisibilityEnum.PUBLIC.name().toLowerCase().equals(resource.getVisibility())) {
                log.debug("资源是公开的，允许访问: {}", resource.getId());
                return true;
            }
            
            // 如果资源不是公开的，检查用户是否登录
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal == null || "anonymousUser".equals(principal)) {
                log.debug("资源不是公开的，用户未登录，拒绝访问");
                return false;
            }
            
            // 检查是否是系统管理员
            if (principal instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) principal;
                
                // 如果是系统管理员，允许访问
                if (isSystemAdmin(userDetails)) {
                    log.debug("用户是系统管理员，允许访问");
                    return true;
                }
                
                // 检查是否是资源所有者
                User user = userService.getByAccount(userDetails.getUsername());
                if (user != null && resource.getUploadedByUserId().equals(user.getId())) {
                    log.debug("用户是资源所有者，允许访问");
                    return true;
                }
            }
            
            // 默认拒绝访问
            log.debug("用户无权访问此资源图片");
            return false;
        } catch (Exception e) {
            log.error("检查资源图片访问权限时发生错误", e);
            return false;
        }
    }
    
    /**
     * 检查用户是否有资源管理权限（教师或企业导师）
     */
    private boolean hasResourceManagementPermission(UserDetails userDetails) {
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        return authorities.stream()
                .anyMatch(auth -> {
                    String role = auth.getAuthority();
                    return role.equals("ROLE_" + RoleConstants.ROLE_TEACHER) || 
                           role.equals("ROLE_" + RoleConstants.ROLE_ENTERPRISE_MENTOR)||
                           role.equals("ROLE_" + RoleConstants.ROLE_SCHOOL_ADMIN);
                });
    }
    
    /**
     * 检查用户是否是系统管理员
     */
    private boolean isSystemAdmin(UserDetails userDetails) {
        return userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_" + RoleConstants.ROLE_SYSTEM_ADMIN));
    }
} 