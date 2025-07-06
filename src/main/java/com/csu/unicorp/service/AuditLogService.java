package com.csu.unicorp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.csu.unicorp.common.constants.LogActionType;
import com.csu.unicorp.vo.AuditLogVO;
import com.csu.unicorp.vo.LogStatisticsVO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 审计日志服务接口
 */
public interface AuditLogService {
    
    /**
     * 记录审计日志
     * 
     * @param userId 用户ID
     * @param action 操作类型
     * @param details 操作详情
     */
    void log(Integer userId, LogActionType action, String details);
    
    /**
     * 记录审计日志（包含更多信息）
     * 
     * @param userId 用户ID
     * @param userAccount 用户账号
     * @param userName 用户名称
     * @param action 操作类型
     * @param module 所属模块
     * @param details 操作详情
     * @param result 操作结果
     * @param ip IP地址
     */
    void log(Integer userId, String userAccount, String userName, 
             LogActionType action, String module, String details, String result, String ip);
    
    /**
     * 分页查询日志
     * 
     * @param page 页码
     * @param size 每页大小
     * @param userId 用户ID
     * @param action 操作类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 日志分页列表
     */
    IPage<AuditLogVO> getLogsByPage(int page, int size, 
                                   Integer userId, String action, 
                                   LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 获取操作类型统计
     * 
     * @return 操作类型统计结果
     */
    List<Map<String, Object>> getActionStatistics();
    
    /**
     * 获取日期统计
     * 
     * @return 日期统计结果
     */
    List<Map<String, Object>> getDateStatistics();
    
    /**
     * 获取日志统计信息
     * 
     * @return 日志统计信息
     */
    LogStatisticsVO getLogStatistics();
} 