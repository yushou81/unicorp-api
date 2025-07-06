package com.csu.unicorp.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.unicorp.entity.Resource;
import com.csu.unicorp.vo.ResourceVO;

/**
 * 资源数据访问接口
 */
@Mapper
public interface ResourceMapper extends BaseMapper<Resource> {
    
    /**
     * 分页查询资源列表，包含上传者信息
     *
     * @param page 分页参数
     * @param keyword 搜索关键词
     * @return 资源列表（含上传者信息）
     */
    @Select("<script>" +
            "SELECT r.*, u.nickname, o.organization_name " +
            "FROM resources r " +
            "LEFT JOIN users u ON r.uploaded_by_user_id = u.id " +
            "LEFT JOIN organizations o ON u.organization_id = o.id " +
            "WHERE r.is_deleted = 0 " +
            "<if test='keyword != null and keyword != \"\"'>" +
            "AND (r.title LIKE CONCAT('%', #{keyword}, '%') " +
            "OR r.description LIKE CONCAT('%', #{keyword}, '%') " +
            "OR r.resource_type LIKE CONCAT('%', #{keyword}, '%'))" +
            "</if>" +
            "ORDER BY r.created_at DESC" +
            "</script>")
    IPage<ResourceVO> selectResourcesWithUploader(Page<ResourceVO> page, @Param("keyword") String keyword);
    
    /**
     * 根据ID查询资源详情，包含上传者信息
     *
     * @param id 资源ID
     * @return 资源详情（含上传者信息）
     */
    @Select("SELECT r.*, u.nickname, o.organization_name " +
            "FROM resources r " +
            "LEFT JOIN users u ON r.uploaded_by_user_id = u.id " +
            "LEFT JOIN organizations o ON u.organization_id = o.id " +
            "WHERE r.id = #{id} AND r.is_deleted = 0")
    ResourceVO selectResourceWithUploaderById(@Param("id") Integer id);
    
    /**
     * 分页查询用户上传的资源列表
     *
     * @param page 分页参数
     * @param userId 用户ID
     * @param keyword 搜索关键词
     * @return 资源列表（含上传者信息）
     */
    @Select("<script>" +
            "SELECT r.*, u.nickname, o.organization_name " +
            "FROM resources r " +
            "LEFT JOIN users u ON r.uploaded_by_user_id = u.id " +
            "LEFT JOIN organizations o ON u.organization_id = o.id " +
            "WHERE r.is_deleted = 0 AND r.uploaded_by_user_id = #{userId} " +
            "<if test='keyword != null and keyword != \"\"'>" +
            "AND (r.title LIKE CONCAT('%', #{keyword}, '%') " +
            "OR r.description LIKE CONCAT('%', #{keyword}, '%') " +
            "OR r.resource_type LIKE CONCAT('%', #{keyword}, '%'))" +
            "</if>" +
            "ORDER BY r.created_at DESC" +
            "</script>")
    IPage<ResourceVO> selectUserUploadedResources(Page<ResourceVO> page, @Param("userId") Integer userId, @Param("keyword") String keyword);
} 