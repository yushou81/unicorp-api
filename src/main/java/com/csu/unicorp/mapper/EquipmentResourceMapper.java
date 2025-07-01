package com.csu.unicorp.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.unicorp.entity.EquipmentResource;

@Mapper
public interface EquipmentResourceMapper extends BaseMapper<EquipmentResource> {
    
    /**
     * 分页查询设备列表
     */
    @Select("<script>" +
            "SELECT e.* FROM equipment_resources e " +
            "<where>" +
            "  <if test=\"keyword != null and keyword != ''\"> " +
            "    (e.name LIKE CONCAT('%', #{keyword}, '%') " +
            "    OR e.description LIKE CONCAT('%', #{keyword}, '%') " +
            "    OR e.location LIKE CONCAT('%', #{keyword}, '%'))" +
            "  </if>" +
            "  <if test=\"organizationId != null\"> " +
            "    AND e.organization_id = #{organizationId} " +
            "  </if>" +
            "  <if test=\"status != null and status != ''\"> " +
            "    AND e.status = #{status} " +
            "  </if>" +
            "</where>" +
            " ORDER BY e.created_at DESC" +
            "</script>")
    IPage<EquipmentResource> findEquipmentResourcesPage(
            Page<EquipmentResource> page, 
            @Param("keyword") String keyword,
            @Param("organizationId") Integer organizationId,
            @Param("status") String status);
} 