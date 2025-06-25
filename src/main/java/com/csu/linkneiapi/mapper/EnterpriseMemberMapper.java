package com.csu.linkneiapi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csu.linkneiapi.entity.EnterpriseMember;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 企业成员数据访问接口
 */
@Mapper
public interface EnterpriseMemberMapper extends BaseMapper<EnterpriseMember> {
    
    /**
     * 根据用户ID查询所属企业列表
     * @param userId 用户ID
     * @return 企业成员关系列表
     */
    @Select("SELECT * FROM enterprise_member WHERE user_id = #{userId}")
    List<EnterpriseMember> findByUserId(@Param("userId") Long userId);
    
    /**
     * 根据企业ID查询成员列表
     * @param enterpriseId 企业ID
     * @return 企业成员关系列表
     */
    @Select("SELECT * FROM enterprise_member WHERE enterprise_id = #{enterpriseId}")
    List<EnterpriseMember> findByEnterpriseId(@Param("enterpriseId") Long enterpriseId);
} 