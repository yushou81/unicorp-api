package com.csu.unicorp.service.impl;

import com.csu.unicorp.common.constants.LogActionType;
import com.csu.unicorp.service.AuditLogService;
import com.csu.unicorp.service.SystemLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 系统日志服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SystemLogServiceImpl implements SystemLogService {
    
    private final AuditLogService auditLogService;
    
    @Override
    public void info(String module, String message) {
        log.info("[{}] {}", module, message);
        auditLogService.log(null, null, "System", 
                LogActionType.SYSTEM_INFO, module, message, "成功", "system");
    }
    
    @Override
    public void warning(String module, String message) {
        log.warn("[{}] {}", module, message);
        auditLogService.log(null, null, "System", 
                LogActionType.SYSTEM_WARNING, module, message, "警告", "system");
    }
    
    @Override
    public void error(String module, String message, Throwable e) {
        log.error("[{}] {}", module, message, e);
        auditLogService.log(null, null, "System", 
                LogActionType.SYSTEM_ERROR, module, message + "\n" + e.getMessage(), "错误", "system");
    }
} 