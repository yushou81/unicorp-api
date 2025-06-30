package com.csu.unicorp.service.impl;

import java.time.LocalDateTime;
import java.util.Collection;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.unicorp.common.constants.RoleConstants;
import com.csu.unicorp.common.constants.VisibilityEnum;
import com.csu.unicorp.common.exception.BusinessException;
import com.csu.unicorp.common.exception.ResourceNotFoundException;
import com.csu.unicorp.dto.ResourceCreationDTO;
import com.csu.unicorp.entity.EquipmentApplication;
import com.csu.unicorp.entity.Resource;
import com.csu.unicorp.entity.User;
import com.csu.unicorp.mapper.EquipmentApplicationMapper;
import com.csu.unicorp.mapper.ResourceMapper;
import com.csu.unicorp.service.ResourceService;
import com.csu.unicorp.service.UserService;
import com.csu.unicorp.vo.EquipmentApplicationVO;
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
    private final EquipmentApplicationMapper equipmentApplicationMapper;
    
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
        return resource;
    }
    
    @Override
    @Transactional
    public ResourceVO createResource(ResourceCreationDTO resourceDTO, UserDetails userDetails) {
        // 检查用户是否有权限创建资源（教师或企业导师）
        if (!hasResourceManagementPermission(userDetails)) {
            throw new AccessDeniedException("只有教师或企业导师可以上传资源");
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
        
        // 处理可见性设置，如果用户未指定则默认为公开
        String visibility = resourceDTO.getVisibility();
        if (visibility == null || visibility.isEmpty()) {
            resource.setVisibility(VisibilityEnum.PUBLIC.getValue());
        } else {
            // 验证可见性值是否有效
            try {
                VisibilityEnum visibilityEnum = VisibilityEnum.fromValue(visibility);
                resource.setVisibility(visibilityEnum.getValue());
            } catch (Exception e) {
                // 无效的可见性值，使用默认值
                resource.setVisibility(VisibilityEnum.PUBLIC.getValue());
                log.warn("无效的可见性值: {}, 使用默认值: {}", visibility, VisibilityEnum.PUBLIC.getValue());
            }
        }
        
        LocalDateTime now = LocalDateTime.now();
        resource.setCreatedAt(now);
        resource.setUpdatedAt(now);
        
        // 保存资源
        resourceMapper.insert(resource);
        
        // 返回资源详情
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
        
        // 更新资源
        resource.setTitle(resourceDTO.getTitle());
        resource.setDescription(resourceDTO.getDescription());
        resource.setResourceType(resourceDTO.getResourceType());
        resource.setFileUrl(resourceDTO.getFileUrl());
        
        // 处理可见性设置
        String visibility = resourceDTO.getVisibility();
        if (visibility != null && !visibility.isEmpty()) {
            try {
                VisibilityEnum visibilityEnum = VisibilityEnum.fromValue(visibility);
                resource.setVisibility(visibilityEnum.getValue());
            } catch (Exception e) {
                log.warn("更新时无效的可见性值: {}, 保持原值", visibility);
            }
        }
        
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
    
    /**
     * 检查用户是否有资源管理权限（教师或企业导师）
     */
    private boolean hasResourceManagementPermission(UserDetails userDetails) {
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        return authorities.stream()
                .anyMatch(auth -> {
                    String role = auth.getAuthority();
                    return role.equals("ROLE_" + RoleConstants.ROLE_TEACHER) || 
                           role.equals("ROLE_" + RoleConstants.ROLE_ENTERPRISE_MENTOR);
                });
    }
    
    /**
     * 检查用户是否是系统管理员
     */
    private boolean isSystemAdmin(UserDetails userDetails) {
        return userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_" + RoleConstants.ROLE_SYSTEM_ADMIN));
    }
    
    /**
     * 检查用户是否为管理员
     */
    @Override
    public boolean isAdmin(UserDetails userDetails) {
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        return authorities.stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + RoleConstants.ADMIN));
    }
    
    /**
     * 检查设备在指定时间段是否已被占用
     */
    @Override
    public boolean isEquipmentTimeOccupied(Integer resourceId, LocalDateTime startTime, LocalDateTime endTime) {
        int count = equipmentApplicationMapper.countOverlappingApplications(resourceId, startTime, endTime);
        return count > 0;
    }
    
    /**
     * 申请使用实验设备
     */
    @Override
    @Transactional
    public Integer applyForEquipment(Integer resourceId, UserDetails userDetails, 
            LocalDateTime startTime, LocalDateTime endTime, String purpose) {
        
        // 检查资源是否存在
        Resource resource = resourceMapper.selectById(resourceId);
        if (resource == null) {
            throw new ResourceNotFoundException("设备资源不存在");
        }
        
        // 检查资源类型是否为实验设备
        if (!"实验设备".equals(resource.getResourceType())) {
            throw new BusinessException("该资源不是实验设备，无法申请使用");
        }
        
        // 检查时间段是否被占用
        if (isEquipmentTimeOccupied(resourceId, startTime, endTime)) {
            throw new BusinessException("该时间段已被占用，请选择其他时间");
        }
        
        // 获取当前用户
        User user = userService.getByAccount(userDetails.getUsername());
        
        // 创建申请记录
        EquipmentApplication application = new EquipmentApplication();
        application.setResourceId(resourceId);
        application.setUserId(user.getId());
        application.setStartTime(startTime);
        application.setEndTime(endTime);
        application.setPurpose(purpose);
        application.setStatus("pending");  // 初始状态为待审核
        
        LocalDateTime now = LocalDateTime.now();
        application.setCreatedAt(now);
        application.setUpdatedAt(now);
        
        // 保存申请记录
        equipmentApplicationMapper.insert(application);
        
        return application.getId();
    }
    
    /**
     * 审核实验设备申请
     */
    @Override
    @Transactional
    public void reviewEquipmentApplication(Integer applicationId, Boolean approved, 
            String comment, UserDetails userDetails) {
        
        // 检查申请是否存在
        EquipmentApplication application = equipmentApplicationMapper.selectById(applicationId);
        if (application == null) {
            throw new ResourceNotFoundException("申请记录不存在");
        }
        
        // 如果已经审核过，不能重复审核
        if (!"pending".equals(application.getStatus())) {
            throw new BusinessException("该申请已经审核过，不能重复审核");
        }
        
        // 获取当前用户
        User user = userService.getByAccount(userDetails.getUsername());
        
        // 更新申请状态
        application.setStatus(approved ? "approved" : "rejected");
        application.setReviewComment(comment);
        application.setReviewedByUserId(user.getId());
        application.setReviewedAt(LocalDateTime.now());
        application.setUpdatedAt(LocalDateTime.now());
        
        equipmentApplicationMapper.updateById(application);
    }
    
    /**
     * 获取用户的设备申请列表
     */
    @Override
    public IPage<?> getUserEquipmentApplications(UserDetails userDetails, int page, int size) {
        // 获取当前用户
        User user = userService.getByAccount(userDetails.getUsername());
        
        // 查询申请列表
        Page<EquipmentApplicationVO> pagination = new Page<>(page, size);
        return equipmentApplicationMapper.selectUserApplications(pagination, user.getId());
    }
    
    /**
     * 获取所有设备申请列表（管理员）
     */
    @Override
    public IPage<?> getAllEquipmentApplications(int page, int size, String status) {
        // 查询所有申请记录
        Page<EquipmentApplicationVO> pagination = new Page<>(page, size);
        return equipmentApplicationMapper.selectAllApplications(pagination, status);
    }
} 