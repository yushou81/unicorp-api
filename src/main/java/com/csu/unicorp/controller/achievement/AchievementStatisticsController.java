package com.csu.unicorp.controller.achievement;

import com.csu.unicorp.config.security.CustomUserDetails;
import com.csu.unicorp.service.AchievementStatisticsService;
import com.csu.unicorp.vo.ResultVO;
import com.csu.unicorp.vo.achievement.StudentAchievementOverviewVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 学生成果统计Controller
 */
@RestController
@RequestMapping("/v1/achievement/statistics")
@RequiredArgsConstructor
@Tag(name = "学生成果统计", description = "学生成果统计相关接口")
public class AchievementStatisticsController {
    
    private final AchievementStatisticsService achievementStatisticsService;
    
    @GetMapping("/overview")
    @Operation(summary = "获取学生成果概览", description = "获取当前学生的成果概览")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @PreAuthorize("hasRole('STUDENT')")
    public ResultVO<StudentAchievementOverviewVO> getStudentAchievementOverview(@AuthenticationPrincipal UserDetails userDetails) {
        Integer userId = ((CustomUserDetails) userDetails).getUserId();
        StudentAchievementOverviewVO overview = achievementStatisticsService.getStudentAchievementOverview(userId);
        return ResultVO.success(overview);
    }
    
    @GetMapping("/overview/{userId}")
    @Operation(summary = "获取指定学生成果概览", description = "获取指定学生的成果概览")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "学生不存在",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<StudentAchievementOverviewVO> getStudentAchievementOverviewById(@PathVariable Integer userId) {
        StudentAchievementOverviewVO overview = achievementStatisticsService.getStudentAchievementOverview(userId);
        return ResultVO.success(overview);
    }
    
    @GetMapping("/views")
    @Operation(summary = "获取学生成果访问统计", description = "获取当前学生的成果访问统计")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @PreAuthorize("hasRole('STUDENT')")
    public ResultVO<Map<String, Object>> getStudentAchievementViewStatistics(@AuthenticationPrincipal UserDetails userDetails) {
        Integer userId = ((CustomUserDetails) userDetails).getUserId();
        Map<String, Object> statistics = achievementStatisticsService.getStudentAchievementViewStatistics(userId);
        return ResultVO.success(statistics);
    }
    
    @GetMapping("/organization/{organizationId}")
    @Operation(summary = "获取组织成果统计", description = "获取指定组织的成果统计")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "组织不存在",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @PreAuthorize("hasRole('TEACHER') or hasRole('SCH_ADMIN')")
    public ResultVO<Map<String, Object>> getOrganizationAchievementStatistics(@PathVariable Integer organizationId) {
        Map<String, Object> statistics = achievementStatisticsService.getOrganizationAchievementStatistics(organizationId);
        return ResultVO.success(statistics);
    }
} 