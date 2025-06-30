package com.csu.unicorp.service;

import java.time.LocalDateTime;

import org.springframework.security.core.userdetails.UserDetails;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.csu.unicorp.dto.ResourceCreationDTO;
import com.csu.unicorp.vo.ResourceVO;

/**
 * 资源服务接口
 */
public interface ResourceService {
    
    /**
     * 获取资源列表（分页）
     * 
     * @param page 页码
     * @param size 每页大小
     * @param keyword 搜索关键词
     * @return 资源列表（分页）
     */
    IPage<ResourceVO> getResources(int page, int size, String keyword);
    
    /**
     * 获取资源详情
     * 
     * @param id 资源ID
     * @return 资源详情
     */
    ResourceVO getResourceById(Integer id);
    
    /**
     * 创建资源
     * 
     * @param resourceDTO 资源创建信息
     * @param userDetails 当前登录用户
     * @return 创建的资源
     */
    ResourceVO createResource(ResourceCreationDTO resourceDTO, UserDetails userDetails);
    
    /**
     * 更新资源
     * 
     * @param id 资源ID
     * @param resourceDTO 资源更新信息
     * @param userDetails 当前登录用户
     * @return 更新后的资源
     */
    ResourceVO updateResource(Integer id, ResourceCreationDTO resourceDTO, UserDetails userDetails);
    
    /**
     * 删除资源
     * 
     * @param id 资源ID
     * @param userDetails 当前登录用户
     */
    void deleteResource(Integer id, UserDetails userDetails);
    
    /**
     * 检查用户是否为管理员
     * 
     * @param userDetails 用户详情
     * @return 是否为管理员
     */
    boolean isAdmin(UserDetails userDetails);
    
    /**
     * 检查实验设备在指定时间段是否已被占用
     * 
     * @param resourceId 资源ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 是否已被占用
     */
    boolean isEquipmentTimeOccupied(Integer resourceId, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 申请使用实验设备
     * 
     * @param resourceId 资源ID
     * @param userDetails 申请用户
     * @param startTime 开始使用时间
     * @param endTime 结束使用时间
     * @param purpose 使用目的
     * @return 申请ID
     */
    Integer applyForEquipment(Integer resourceId, UserDetails userDetails, 
            LocalDateTime startTime, LocalDateTime endTime, String purpose);
    
    /**
     * 审核实验设备申请
     * 
     * @param applicationId 申请ID
     * @param approved 是否批准
     * @param comment 审核意见
     * @param userDetails 审核人
     */
    void reviewEquipmentApplication(Integer applicationId, Boolean approved, 
            String comment, UserDetails userDetails);
    
    /**
     * 获取用户的设备申请列表
     * 
     * @param userDetails 用户
     * @param page 页码
     * @param size 每页大小
     * @return 申请列表（分页）
     */
    IPage<?> getUserEquipmentApplications(UserDetails userDetails, int page, int size);
    
    /**
     * 获取所有设备申请列表（管理员）
     * 
     * @param page 页码
     * @param size 每页大小
     * @param status 状态过滤（可选）
     * @return 申请列表（分页）
     */
    IPage<?> getAllEquipmentApplications(int page, int size, String status);
} 