package com.csu.unicorp.controller.log;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.csu.unicorp.common.annotation.Log;
import com.csu.unicorp.common.constants.LogActionType;
import com.csu.unicorp.service.AuditLogService;
import com.csu.unicorp.vo.AuditLogVO;
import com.csu.unicorp.vo.LogStatisticsVO;
import com.csu.unicorp.vo.ResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * 审计日志控制器
 * 提供日志查询和统计功能
 */
@Tag(name = "Audit", description = "审计日志管理")
@RestController
@RequestMapping("/v1/admin/audit")
@RequiredArgsConstructor
public class AuditLogController {
    
    private final AuditLogService auditLogService;
    
    /**
     * 分页查询审计日志
     * 
     * @param page 页码
     * @param size 每页大小
     * @param userId 用户ID
     * @param action 操作类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 日志分页列表
     */
    @Operation(summary = "分页查询审计日志", description = "根据条件查询审计日志记录")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "查询成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @GetMapping("/logs")
    @PreAuthorize("hasRole('SYSADMIN')")
    @Log(value = LogActionType.SYSTEM_INFO, module = "审计日志", description = "查询审计日志")
    public ResultVO<IPage<AuditLogVO>> getLogs(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) @Parameter(description = "用户ID") Integer userId,
            @RequestParam(required = false) @Parameter(description = "操作类型") String action,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) 
                @Parameter(description = "开始时间") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) 
                @Parameter(description = "结束时间") LocalDateTime endTime) {
        
        IPage<AuditLogVO> logs = auditLogService.getLogsByPage(page, size, userId, action, startTime, endTime);
        return ResultVO.success("查询成功", logs);
    }
    
    /**
     * 获取日志统计信息
     * 
     * @return 日志统计信息
     */
    @Operation(summary = "获取日志统计信息", description = "获取系统日志的统计数据")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "查询成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('SYSADMIN')")
    @Log(value = LogActionType.SYSTEM_INFO, module = "审计日志", description = "获取日志统计")
    public ResultVO<LogStatisticsVO> getStatistics() {
        LogStatisticsVO statistics = auditLogService.getLogStatistics();
        return ResultVO.success("查询成功", statistics);
    }
} 