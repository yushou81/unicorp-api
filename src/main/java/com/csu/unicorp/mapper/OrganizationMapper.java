package com.csu.unicorp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csu.unicorp.entity.Organization;
import com.csu.unicorp.vo.OrganizationSimpleVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 组织Mapper接口
 */
@Mapper
public interface OrganizationMapper extends BaseMapper<Organization> {
    
    /**
     * 查询所有已批准的学校列表
     * 
     * @return 学校简化列表
     */
    @Select("SELECT id, organization_name FROM organizations " +
            "WHERE type = 'School' AND status = 'approved' AND is_deleted = 0 " +
            "ORDER BY organization_name")
    List<OrganizationSimpleVO> selectAllApprovedSchools();
} 