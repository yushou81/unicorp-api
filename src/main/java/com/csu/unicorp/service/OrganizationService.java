package com.csu.unicorp.service;

import com.csu.unicorp.dto.SchoolCreationDTO;
import com.csu.unicorp.entity.Organization;
import com.csu.unicorp.vo.OrganizationSimpleVO;
import com.csu.unicorp.vo.OrganizationVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 组织服务接口
 */
public interface OrganizationService {
    
    /**
     * 获取所有学校列表
     * 
     * @param view 视图类型，决定返回数据的详细程度（simple或detailed）
     * @return 学校列表，根据view参数返回不同的VO类型
     */
    List<?> getAllSchools(String view);
    
    /**
     * 获取所有企业列表
     * 
     * @param view 视图类型，决定返回数据的详细程度（simple或detailed）
     * @return 企业列表，根据view参数返回不同的VO类型
     */
    List<?> getAllEnterprises(String view);
    
    /**
     * 根据ID获取学校详情
     * 
     * @param id 学校ID
     * @return 学校详细信息
     */
    OrganizationVO getSchoolById(Integer id);
    
    /**
     * 根据ID获取企业详情
     * 
     * @param id 企业ID
     * @return 企业详细信息
     */
    OrganizationVO getEnterpriseById(Integer id);
    
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
     * 根据组织名称查询组织
     * 
     * @param name 组织名称
     * @return 组织实体
     */
    Organization getByName(String name);
    
    /**
     * 将组织实体转换为VO对象
     * 
     * @param organization 组织实体
     * @return 组织VO
     */
    OrganizationVO convertToVO(Organization organization);
    
    /**
     * 获取所有待审核的组织列表
     * 
     * @return 待审核的组织列表
     */
    List<OrganizationVO> getPendingOrganizations();
    
    /**
     * 更新组织Logo
     *
     * @param id 组织ID
     * @param logoFile Logo文件
     * @return 上传后的Logo URL
     */
    String updateOrganizationLogo(Integer id, MultipartFile logoFile);
} 