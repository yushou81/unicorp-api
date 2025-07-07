package com.csu.unicorp.controller.achievement;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.unicorp.common.utils.RequestUtils;
import com.csu.unicorp.dto.achievement.AchievementVerifyDTO;
import com.csu.unicorp.dto.achievement.CompetitionAwardCreationDTO;
import com.csu.unicorp.config.security.CustomUserDetails;
import com.csu.unicorp.service.CompetitionAwardService;
import com.csu.unicorp.vo.ResultVO;
import com.csu.unicorp.vo.achievement.CompetitionAwardVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 竞赛获奖Controller
 */
@RestController
@RequestMapping("/v1/awards")
@RequiredArgsConstructor
@Tag(name = "竞赛获奖管理", description = "竞赛获奖管理相关接口")
public class CompetitionAwardController {
    
    private final CompetitionAwardService competitionAwardService;
    
    @GetMapping
    @Operation(summary = "获取获奖列表", description = "获取当前用户的获奖列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @PreAuthorize("hasRole('STUDENT')")
    public ResultVO<List<CompetitionAwardVO>> getCompetitionAwards(@AuthenticationPrincipal UserDetails userDetails) {
        Integer userId = ((CustomUserDetails) userDetails).getUserId();
        List<CompetitionAwardVO> awards = competitionAwardService.getCompetitionAwards(userId);
        return ResultVO.success(awards);
    }
    
    @GetMapping("/page")
    @Operation(summary = "分页获取获奖列表", description = "分页获取当前用户的获奖列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @PreAuthorize("hasRole('STUDENT')")
    public ResultVO<Page<CompetitionAwardVO>> getCompetitionAwardPage(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Integer userId = ((CustomUserDetails) userDetails).getUserId();
        Page<CompetitionAwardVO> awardPage = competitionAwardService.getCompetitionAwardPage(userId, page - 1, size);
        return ResultVO.success(awardPage);
    }
    
    @GetMapping("/public")
    @Operation(summary = "获取公开获奖列表", description = "分页获取公开的获奖列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<Page<CompetitionAwardVO>> getPublicCompetitionAwardPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<CompetitionAwardVO> awardPage = competitionAwardService.getPublicCompetitionAwardPage(page - 1, size);
        return ResultVO.success(awardPage);
    }
    
    @GetMapping("/unverified")
    @Operation(summary = "获取待认证获奖列表", description = "分页获取待认证的获奖列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "403", description = "无权限访问该组织数据",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @PreAuthorize("hasRole('TEACHER') or hasRole('SCH_ADMIN')")
    public ResultVO<Page<CompetitionAwardVO>> getUnverifiedCompetitionAwardPage(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Integer organizationId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Integer currentUserId = ((CustomUserDetails) userDetails).getUserId();
        Page<CompetitionAwardVO> awardPage = competitionAwardService.getUnverifiedCompetitionAwardPage(organizationId, currentUserId, page - 1, size);
        return ResultVO.success(awardPage);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "获取获奖详情", description = "根据ID获取获奖详情")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "获奖不存在",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<CompetitionAwardVO> getCompetitionAwardDetail(
            @PathVariable Integer id,
            HttpServletRequest request) {
        String viewerIp = RequestUtils.getClientIp(request);
        CompetitionAwardVO award = competitionAwardService.getCompetitionAwardDetail(id, viewerIp);
        return ResultVO.success(award);
    }
    
    @PostMapping
    @Operation(summary = "创建获奖", description = "创建新的获奖")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "创建成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "400", description = "参数错误",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @PreAuthorize("hasRole('STUDENT')")
    public ResultVO<CompetitionAwardVO> createCompetitionAward(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CompetitionAwardCreationDTO competitionAwardCreationDTO) {
        Integer userId = ((CustomUserDetails) userDetails).getUserId();
        CompetitionAwardVO award = competitionAwardService.createCompetitionAward(userId, competitionAwardCreationDTO);
        return ResultVO.success(award);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "更新获奖", description = "更新指定ID的获奖")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "更新成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "400", description = "参数错误",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "403", description = "无权限",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "获奖不存在",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @PreAuthorize("hasRole('STUDENT')")
    public ResultVO<CompetitionAwardVO> updateCompetitionAward(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CompetitionAwardCreationDTO competitionAwardCreationDTO) {
        Integer userId = ((CustomUserDetails) userDetails).getUserId();
        CompetitionAwardVO award = competitionAwardService.updateCompetitionAward(id, userId, competitionAwardCreationDTO);
        return ResultVO.success(award);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "删除获奖", description = "删除指定ID的获奖")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "删除成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "403", description = "无权限",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "获奖不存在",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @PreAuthorize("hasRole('STUDENT')")
    public ResultVO<Boolean> deleteCompetitionAward(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails) {
        Integer userId = ((CustomUserDetails) userDetails).getUserId();
        boolean result = competitionAwardService.deleteCompetitionAward(id, userId);
        return ResultVO.success(result);
    }
    
    @PostMapping("/{id}/certificate")
    @Operation(summary = "上传获奖证书", description = "为指定获奖上传证书")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "上传成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "400", description = "参数错误",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "403", description = "无权限",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "获奖不存在",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @PreAuthorize("hasRole('STUDENT')")
    public ResultVO<CompetitionAwardVO> uploadCertificate(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("file") MultipartFile file) {
        Integer userId = ((CustomUserDetails) userDetails).getUserId();
        CompetitionAwardVO award = competitionAwardService.uploadCertificate(id, userId, file);
        return ResultVO.success(award);
    }
    
    @PostMapping("/{id}/verify")
    @Operation(summary = "认证获奖", description = "认证指定ID的获奖")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "认证成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "400", description = "参数错误",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "403", description = "无权限",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "获奖不存在",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @PreAuthorize("hasRole('TEACHER') or hasRole('SCH_ADMIN')")
    public ResultVO<CompetitionAwardVO> verifyCompetitionAward(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody AchievementVerifyDTO verifyDTO) {
        Integer verifierId = ((CustomUserDetails) userDetails).getUserId();
        CompetitionAwardVO award = competitionAwardService.verifyCompetitionAward(id, verifierId, verifyDTO);
        return ResultVO.success(award);
    }
    
    @GetMapping("/school")
    @Operation(summary = "获取学校学生竞赛获奖列表", description = "获取当前教师或管理员所属学校的学生竞赛获奖列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "403", description = "权限不足",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @PreAuthorize("hasRole('TEACHER') or hasRole('SCH_ADMIN')")
    public ResultVO<Page<CompetitionAwardVO>> getSchoolStudentAwards(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Integer userId = ((CustomUserDetails) userDetails).getUserId();
        Page<CompetitionAwardVO> awards = competitionAwardService.getSchoolStudentAwards(userId, page - 1, size);
        return ResultVO.success(awards);
    }
    
    @GetMapping("/school/statistics")
    @Operation(summary = "获取学校竞赛获奖统计数据", description = "获取当前教师或管理员所属学校的竞赛获奖统计数据")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "403", description = "权限不足",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @PreAuthorize("hasRole('TEACHER') or hasRole('SCH_ADMIN')")
    public ResultVO<Map<String, Object>> getSchoolAwardStatistics(
            @AuthenticationPrincipal UserDetails userDetails) {
        Integer userId = ((CustomUserDetails) userDetails).getUserId();
        Map<String, Object> statistics = competitionAwardService.getSchoolAwardStatistics(userId);
        return ResultVO.success(statistics);
    }
    
    @GetMapping("/school/by-student")
    @Operation(summary = "获取学校指定学生的竞赛获奖列表", description = "获取学校指定学生的竞赛获奖列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "403", description = "权限不足",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @PreAuthorize("hasRole('TEACHER') or hasRole('SCH_ADMIN')")
    public ResultVO<List<CompetitionAwardVO>> getSchoolStudentAwardsByStudent(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Integer studentId) {
        Integer userId = ((CustomUserDetails) userDetails).getUserId();
        List<CompetitionAwardVO> awards = competitionAwardService.getSchoolStudentAwardsByStudent(userId, studentId);
        return ResultVO.success(awards);
    }
} 