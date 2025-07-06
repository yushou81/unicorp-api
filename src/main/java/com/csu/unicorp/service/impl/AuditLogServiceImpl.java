package com.csu.unicorp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.unicorp.common.constants.LogActionType;
import com.csu.unicorp.entity.AuditLog;
import com.csu.unicorp.mapper.AuditLogMapper;
import com.csu.unicorp.service.AuditLogService;
import com.csu.unicorp.vo.AuditLogVO;
import com.csu.unicorp.vo.LogStatisticsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 审计日志服务实现类
 */
@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {
    
    private final AuditLogMapper auditLogMapper;
    
    @Override
    public void log(Integer userId, LogActionType action, String details) {
        AuditLog log = AuditLog.builder()
                .userId(userId)
                .action(action.name())
                .details(details)
                .timestamp(LocalDateTime.now())
                .build();
        
        auditLogMapper.insert(log);
    }
    
    @Override
    public void log(Integer userId, String userAccount, String userName, 
                   LogActionType action, String module, String details, 
                   String result, String ip) {
        AuditLog log = AuditLog.builder()
                .userId(userId)
                .userAccount(userAccount)
                .userName(userName)
                .action(action.name())
                .module(module)
                .details(details)
                .result(result)
                .ip(ip)
                .timestamp(LocalDateTime.now())
                .build();
        
        auditLogMapper.insert(log);
    }
    
    @Override
    public IPage<AuditLogVO> getLogsByPage(int page, int size, 
                                         Integer userId, String action, 
                                         LocalDateTime startTime, LocalDateTime endTime) {
        Page<AuditLog> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<AuditLog> wrapper = new LambdaQueryWrapper<>();
        
        // 构建查询条件
        if (userId != null) {
            wrapper.eq(AuditLog::getUserId, userId);
        }
        
        if (action != null && !action.isEmpty()) {
            wrapper.eq(AuditLog::getAction, action);
        }
        
        if (startTime != null && endTime != null) {
            wrapper.between(AuditLog::getTimestamp, startTime, endTime);
        }
        
        // 按时间倒序排序
        wrapper.orderByDesc(AuditLog::getTimestamp);
        
        // 执行查询
        IPage<AuditLog> logPage = auditLogMapper.selectPage(pageParam, wrapper);
        
        // 转换为VO
        return logPage.convert(this::convertToVO);
    }
    
    @Override
    public List<Map<String, Object>> getActionStatistics() {
        return auditLogMapper.countByAction();
    }
    
    @Override
    public List<Map<String, Object>> getDateStatistics() {
        return auditLogMapper.countByDate();
    }
    
    @Override
    public LogStatisticsVO getLogStatistics() {
        // 获取总日志数
        Long totalLogs = auditLogMapper.selectCount(null);
        
        // 获取今日日志数
        LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LambdaQueryWrapper<AuditLog> todayWrapper = new LambdaQueryWrapper<>();
        todayWrapper.ge(AuditLog::getTimestamp, today);
        Long todayLogs = auditLogMapper.selectCount(todayWrapper);
        
        // 获取操作类型统计
        List<Map<String, Object>> actionStats = getActionStatistics();
        
        // 获取日期统计
        List<Map<String, Object>> dateStats = getDateStatistics();
        
        // 构建统计VO
        return LogStatisticsVO.builder()
                .totalLogs(totalLogs)
                .todayLogs(todayLogs)
                .actionStatistics(actionStats)
                .dateStatistics(dateStats)
                .build();
    }
    
    /**
     * 将实体转换为VO
     */
    private AuditLogVO convertToVO(AuditLog log) {
        if (log == null) {
            return null;
        }
        
        return AuditLogVO.builder()
                .id(log.getId())
                .userId(log.getUserId())
                .userAccount(log.getUserAccount())
                .userName(log.getUserName())
                .action(log.getAction())
                .actionDesc(getActionDescription(log.getAction()))
                .module(log.getModule())
                .details(log.getDetails())
                .result(log.getResult())
                .ip(log.getIp())
                .timestamp(log.getTimestamp())
                .build();
    }
    
    /**
     * 获取操作类型描述
     */
    private String getActionDescription(String action) {
        try {
            return LogActionType.valueOf(action).getDescription();
        } catch (Exception e) {
            return action;
        }
    }
} 