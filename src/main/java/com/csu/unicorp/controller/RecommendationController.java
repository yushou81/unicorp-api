package com.csu.unicorp.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.csu.unicorp.config.security.CustomUserDetails;
import com.csu.unicorp.dto.recommendation.RecommendationStatusUpdateDTO;
import com.csu.unicorp.dto.recommendation.UserBehaviorRecordDTO;
import com.csu.unicorp.dto.recommendation.UserFeatureUpdateDTO;
import com.csu.unicorp.service.RecommendationService;
import com.csu.unicorp.vo.JobRecommendationVO;
import com.csu.unicorp.vo.ResultVO;
import com.csu.unicorp.vo.StudentTalentVO;
import com.csu.unicorp.vo.UserFeatureVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 推荐系统控制器
 */
@RestController
@RequestMapping("/v1/recommendations")
@RequiredArgsConstructor
@Tag(name = "智能推荐", description = "智能推荐系统相关接口")
public class RecommendationController {
    
    private final RecommendationService recommendationService;
    
    @GetMapping("/jobs")
    @Operation(summary = "[学生] 获取岗位推荐列表", description = "获取为当前登录学生推荐的岗位列表",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "403", description = "权限不足 (非学生用户)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class)))
    })
    @PreAuthorize("hasRole('STUDENT')")
    public ResultVO<IPage<JobRecommendationVO>> getJobRecommendations(
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Integer userId = ((CustomUserDetails) userDetails).getUserId();
        IPage<JobRecommendationVO> recommendations = recommendationService.getJobRecommendations(userId, page, size);
        return ResultVO.success("获取岗位推荐列表成功", recommendations);
    }
    
    @GetMapping("/talents")
    @Operation(summary = "[企业] 获取人才推荐列表", description = "获取为当前登录企业推荐的人才列表",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "403", description = "权限不足 (非企业用户)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class)))
    })
    @PreAuthorize("hasAnyRole('EN_ADMIN', 'EN_TEACHER')")
    public ResultVO<IPage<StudentTalentVO>> getTalentRecommendations(
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Integer orgId = ((CustomUserDetails) userDetails).getOrganizationId();
        IPage<StudentTalentVO> recommendations = recommendationService.getTalentRecommendations(orgId, page, size);
        return ResultVO.success("获取人才推荐列表成功", recommendations);
    }
    
    @PatchMapping("/jobs/{id}")
    @Operation(summary = "[学生] 更新岗位推荐状态", description = "更新指定岗位推荐的状态",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "更新成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "400", description = "无效的状态值",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "403", description = "权限不足",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "404", description = "推荐不存在",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class)))
    })
    @PreAuthorize("hasRole('STUDENT')")
    public ResultVO<Boolean> updateJobRecommendationStatus(
            @Parameter(description = "推荐ID") @PathVariable Integer id,
            @Valid @RequestBody RecommendationStatusUpdateDTO statusUpdateDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Integer userId = ((CustomUserDetails) userDetails).getUserId();
        boolean success = recommendationService.updateJobRecommendationStatus(id, userId, statusUpdateDTO);
        return ResultVO.success("更新推荐状态成功", success);
    }
    
    @PatchMapping("/talents/{id}")
    @Operation(summary = "[企业] 更新人才推荐状态", description = "更新指定人才推荐的状态",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "更新成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "400", description = "无效的状态值",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "403", description = "权限不足",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "404", description = "推荐不存在",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class)))
    })
    @PreAuthorize("hasAnyRole('EN_ADMIN', 'EN_TEACHER')")
    public ResultVO<Boolean> updateTalentRecommendationStatus(
            @Parameter(description = "推荐ID") @PathVariable Integer id,
            @Valid @RequestBody RecommendationStatusUpdateDTO statusUpdateDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Integer orgId = ((CustomUserDetails) userDetails).getOrganizationId();
        boolean success = recommendationService.updateTalentRecommendationStatus(id, orgId, statusUpdateDTO);
        return ResultVO.success("更新推荐状态成功", success);
    }
    
    @PostMapping("/behaviors")
    @Operation(summary = "记录用户行为", description = "记录用户的行为数据，用于个性化推荐",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "记录成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "400", description = "无效的行为类型或目标类型",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<Boolean> recordUserBehavior(
            @Valid @RequestBody UserBehaviorRecordDTO behaviorDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Integer userId = ((CustomUserDetails) userDetails).getUserId();
        boolean success = recommendationService.recordUserBehavior(userId, behaviorDTO);
        return ResultVO.success("记录行为成功", success);
    }
    
    @GetMapping("/features/me")
    @Operation(summary = "[学生] 获取个人特征", description = "获取当前登录学生的个人特征信息",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "403", description = "权限不足 (非学生用户)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class)))
    })
    @PreAuthorize("hasRole('STUDENT')")
    public ResultVO<UserFeatureVO> getUserFeature(@AuthenticationPrincipal UserDetails userDetails) {
        Integer userId = ((CustomUserDetails) userDetails).getUserId();
        UserFeatureVO feature = recommendationService.getUserFeature(userId);
        return ResultVO.success("获取个人特征成功", feature);
    }
    
    @PutMapping("/features/me")
    @Operation(summary = "[学生] 更新个人特征", description = "更新当前登录学生的个人特征信息",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "更新成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "400", description = "参数错误",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "403", description = "权限不足 (非学生用户)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class)))
    })
    @PreAuthorize("hasRole('STUDENT')")
    public ResultVO<UserFeatureVO> updateUserFeature(
            @Valid @RequestBody UserFeatureUpdateDTO featureDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Integer userId = ((CustomUserDetails) userDetails).getUserId();
        UserFeatureVO feature = recommendationService.updateUserFeature(userId, featureDTO);
        return ResultVO.success("更新个人特征成功", feature);
    }
    
    @GetMapping("/statistics/behaviors")
    @Operation(summary = "[学生] 获取行为统计", description = "获取当前登录学生的行为统计数据",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "403", description = "权限不足 (非学生用户)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class)))
    })
    @PreAuthorize("hasRole('STUDENT')")
    public ResultVO<Map<String, Object>> getUserBehaviorStatistics(@AuthenticationPrincipal UserDetails userDetails) {
        Integer userId = ((CustomUserDetails) userDetails).getUserId();
        Map<String, Object> statistics = recommendationService.getUserBehaviorStatistics(userId);
        return ResultVO.success("获取行为统计成功", statistics);
    }
    
    @PostMapping("/jobs/generate")
    @Operation(summary = "[学生] 生成岗位推荐", description = "为当前登录学生生成岗位推荐",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "生成成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "403", description = "权限不足 (非学生用户)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class)))
    })
    @PreAuthorize("hasRole('STUDENT')")
    public ResultVO<Integer> generateJobRecommendations(@AuthenticationPrincipal UserDetails userDetails) {
        Integer userId = ((CustomUserDetails) userDetails).getUserId();
        int count = recommendationService.generateJobRecommendationsForUser(userId);
        return ResultVO.success("生成岗位推荐成功", count);
    }
    
    @PostMapping("/talents/generate")
    @Operation(summary = "[企业] 生成人才推荐", description = "为当前登录企业生成人才推荐",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "生成成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "403", description = "权限不足 (非企业用户)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class)))
    })
    @PreAuthorize("hasAnyRole('EN_ADMIN', 'EN_TEACHER')")
    public ResultVO<Integer> generateTalentRecommendations(@AuthenticationPrincipal UserDetails userDetails) {
        Integer orgId = ((CustomUserDetails) userDetails).getOrganizationId();
        int count = recommendationService.generateTalentRecommendationsForOrganization(orgId);
        return ResultVO.success("生成人才推荐成功", count);
    }
} 