package com.csu.unicorp.mapper;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.unicorp.entity.EquipmentBooking;

@Mapper
public interface EquipmentBookingMapper extends BaseMapper<EquipmentBooking> {
    
    /**
     * 分页查询预约列表
     */
    @Select("<script>" +
            "SELECT b.* FROM equipment_bookings b " +
            "LEFT JOIN resources r ON b.resource_id = r.id " +
            "<where>" +
            "  <if test=\"userId != null\"> " +
            "    AND b.user_id = #{userId} " +
            "  </if>" +
            "  <if test=\"resourceId != null\"> " +
            "    AND b.resource_id = #{resourceId} " +
            "  </if>" +
            "  <if test=\"status != null and status != ''\"> " +
            "    AND b.status = #{status} " +
            "  </if>" +
            "  <if test=\"organizationId != null\"> " +
            "    AND r.organization_id = #{organizationId} " +
            "  </if>" +
            "</where>" +
            " ORDER BY b.created_at DESC" +
            "</script>")
    IPage<EquipmentBooking> findBookingsPage(
            Page<EquipmentBooking> page, 
            @Param("userId") Integer userId,
            @Param("resourceId") Integer resourceId,
            @Param("status") String status,
            @Param("organizationId") Integer organizationId);
    
    /**
     * 检查时间段冲突
     * 
     * @param resourceId 设备资源ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param excludeBookingId 排除的预约ID（用于更新时）
     * @return 冲突的预约列表
     */
    @Select("<script>" +
            "SELECT * FROM equipment_bookings " +
            "WHERE resource_id = #{resourceId} " +
            "AND status = 'APPROVED' " +
            "<if test=\"excludeBookingId != null\"> " +
            "  AND id != #{excludeBookingId} " +
            "</if>" +
            "AND (" +
            "  (start_time &lt; #{endTime} AND end_time &gt; #{startTime})" +
            ")" +
            "</script>")
    List<EquipmentBooking> findConflictBookings(
            @Param("resourceId") Integer resourceId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("excludeBookingId") Integer excludeBookingId);
} 