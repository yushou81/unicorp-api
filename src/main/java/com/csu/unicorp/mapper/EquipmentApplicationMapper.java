package com.csu.unicorp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.unicorp.entity.EquipmentApplication;
import com.csu.unicorp.vo.EquipmentApplicationVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 设备申请Mapper接口
 */
@Mapper
public interface EquipmentApplicationMapper extends BaseMapper<EquipmentApplication> {
    
    /**
     * 检查设备在指定时间段是否已被占用
     * 
     * @param resourceId 资源ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 占用的申请数量
     */
    @Select("SELECT COUNT(*) FROM equipment_applications " +
            "WHERE resource_id = #{resourceId} " +
            "AND status = 'approved' " +
            "AND ((start_time <= #{endTime} AND end_time >= #{startTime}) " +
            "OR (start_time >= #{startTime} AND start_time <= #{endTime}) " +
            "OR (end_time >= #{startTime} AND end_time <= #{endTime})) " +
            "AND is_deleted = 0")
    int countOverlappingApplications(@Param("resourceId") Integer resourceId,
                                      @Param("startTime") LocalDateTime startTime,
                                      @Param("endTime") LocalDateTime endTime);
    
    /**
     * 查询用户的设备申请列表（带分页）
     * 
     * @param page 分页参数
     * @param userId 用户ID
     * @return 设备申请列表
     */
    @Select("SELECT a.*, r.title as resource_title, " +
            "u1.nickname as user_name, o1.name as user_organization, " +
            "u2.nickname as reviewer_name " +
            "FROM equipment_applications a " +
            "LEFT JOIN resources r ON a.resource_id = r.id " +
            "LEFT JOIN users u1 ON a.user_id = u1.id " +
            "LEFT JOIN users u2 ON a.reviewed_by_user_id = u2.id " +
            "LEFT JOIN organizations o1 ON u1.organization_id = o1.id " +
            "WHERE a.user_id = #{userId} AND a.is_deleted = 0 " +
            "ORDER BY a.created_at DESC")
    IPage<EquipmentApplicationVO> selectUserApplications(Page<EquipmentApplicationVO> page,
                                                    @Param("userId") Integer userId);
    
    /**
     * 查询所有设备申请列表（带分页）
     * 
     * @param page 分页参数
     * @param status 状态过滤（可选）
     * @return 设备申请列表
     */
    @Select("<script>" +
            "SELECT a.*, r.title as resource_title, " +
            "u1.nickname as user_name, o1.name as user_organization, " +
            "u2.nickname as reviewer_name " +
            "FROM equipment_applications a " +
            "LEFT JOIN resources r ON a.resource_id = r.id " +
            "LEFT JOIN users u1 ON a.user_id = u1.id " +
            "LEFT JOIN users u2 ON a.reviewed_by_user_id = u2.id " +
            "LEFT JOIN organizations o1 ON u1.organization_id = o1.id " +
            "WHERE a.is_deleted = 0 " +
            "<if test=\"status != null and status != ''\"> " +
            "AND a.status = #{status} " +
            "</if> " +
            "ORDER BY a.created_at DESC" +
            "</script>")
    IPage<EquipmentApplicationVO> selectAllApplications(Page<EquipmentApplicationVO> page,
                                                    @Param("status") String status);
} 