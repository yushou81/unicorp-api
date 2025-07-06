package com.csu.unicorp.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 日志统计视图对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogStatisticsVO {
    /**
     * 总日志数
     */
    private Long totalLogs;
    
    /**
     * 今日日志数
     */
    private Long todayLogs;
    
    /**
     * 操作类型统计
     */
    private List<Map<String, Object>> actionStatistics;
    
    /**
     * 日期统计
     */
    private List<Map<String, Object>> dateStatistics;
} 