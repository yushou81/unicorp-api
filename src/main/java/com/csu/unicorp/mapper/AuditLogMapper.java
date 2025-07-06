package com.csu.unicorp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.unicorp.entity.AuditLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 审计日志Mapper接口
 */
@Mapper
public interface AuditLogMapper extends BaseMapper<AuditLog> {
    
    /**
     * 根据用户ID查询日志
     * 
     * @param userId 用户ID
     * @param page 分页参数
     * @return 日志分页列表
     */
    @Select("SELECT * FROM audit_logs WHERE user_id = #{userId} ORDER BY timestamp DESC")
    IPage<AuditLog> selectByUserId(@Param("userId") Integer userId, Page<AuditLog> page);
    
    /**
     * 根据操作类型查询日志
     * 
     * @param action 操作类型
     * @param page 分页参数
     * @return 日志分页列表
     */
    @Select("SELECT * FROM audit_logs WHERE action = #{action} ORDER BY timestamp DESC")
    IPage<AuditLog> selectByAction(@Param("action") String action, Page<AuditLog> page);
    
    /**
     * 根据时间范围查询日志
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param page 分页参数
     * @return 日志分页列表
     */
    @Select("SELECT * FROM audit_logs WHERE timestamp BETWEEN #{startTime} AND #{endTime} ORDER BY timestamp DESC")
    IPage<AuditLog> selectByTimeRange(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            Page<AuditLog> page);
    
    /**
     * 统计各操作类型的数量
     * 
     * @return 操作类型统计结果
     */
    @Select("SELECT action, COUNT(*) as count FROM audit_logs GROUP BY action")
    List<Map<String, Object>> countByAction();
    
    /**
     * 统计近30天每日日志数量
     * 
     * @return 日期统计结果
     */
    @Select("SELECT DATE(timestamp) as date, COUNT(*) as count FROM audit_logs GROUP BY DATE(timestamp) ORDER BY date DESC LIMIT 30")
    List<Map<String, Object>> countByDate();
} 