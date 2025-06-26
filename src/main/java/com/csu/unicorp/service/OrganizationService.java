package com.csu.unicorp.service;

import com.csu.unicorp.dto.SchoolCreationDTO;
import com.csu.unicorp.entity.Organization;
import com.csu.unicorp.vo.OrganizationSimpleVO;
import com.csu.unicorp.vo.OrganizationVO;

import java.util.List;

/**
 * 组织服务接口
 */
public interface OrganizationService {
    
    /**
     * 获取所有学校列表（简化版，用于下拉菜单）
     * 
     * @return 学校列表
     */
    List<OrganizationSimpleVO> getAllSchools();
    
    /**
     * 创建学校及其管理员
     * 
     * @param schoolCreationDTO 学校创建信息
     * @return 创建成功的组织信息
     */
    OrganizationVO createSchool(SchoolCreationDTO schoolCreationDTO);
    
    /**
     * 根据ID查询组织
     * 
     * @param id 组织ID
     * @return 组织实体
     */
    Organization getById(Integer id);
    
    /**
     * 将组织实体转换为VO对象
     * 
     * @param organization 组织实体
     * @return 组织VO
     */
    OrganizationVO convertToVO(Organization organization);
} 