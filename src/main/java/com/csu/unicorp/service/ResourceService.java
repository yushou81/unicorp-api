package com.csu.unicorp.service;

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
} 