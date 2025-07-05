package com.csu.unicorp.controller.achievement;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.unicorp.common.utils.RequestUtils;
import com.csu.unicorp.dto.achievement.AchievementVerifyDTO;
import com.csu.unicorp.dto.achievement.ResearchAchievementCreationDTO;
import com.csu.unicorp.config.security.CustomUserDetails;
import com.csu.unicorp.service.ResearchAchievementService;
import com.csu.unicorp.vo.ResultVO;
import com.csu.unicorp.vo.achievement.ResearchAchievementVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

/**
 * 科研成果Controller
 */
@RestController
@RequestMapping("/v1/research")
@RequiredArgsConstructor
@Tag(name = "科研成果管理", description = "科研成果管理相关接口")
@Slf4j
public class ResearchAchievementController {
    
    private final ResearchAchievementService researchAchievementService;
    
    @GetMapping
    @Operation(summary = "获取科研成果列表", description = "获取当前用户的科研成果列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @PreAuthorize("hasRole('STUDENT')")
    public ResultVO<List<ResearchAchievementVO>> getResearchAchievements(@AuthenticationPrincipal UserDetails userDetails) {
        Integer userId = ((CustomUserDetails) userDetails).getUserId();
        List<ResearchAchievementVO> achievements = researchAchievementService.getResearchAchievements(userId);
        return ResultVO.success(achievements);
    }
    
    @GetMapping("/page")
    @Operation(summary = "分页获取科研成果列表", description = "分页获取当前用户的科研成果列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @PreAuthorize("hasRole('STUDENT')")
    public ResultVO<Page<ResearchAchievementVO>> getResearchAchievementPage(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Integer userId = ((CustomUserDetails) userDetails).getUserId();
        Page<ResearchAchievementVO> achievementPage = researchAchievementService.getResearchAchievementPage(userId, page - 1, size);
        return ResultVO.success(achievementPage);
    }
    
    @GetMapping("/public")
    @Operation(summary = "获取公开科研成果列表", description = "分页获取公开的科研成果列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<Page<ResearchAchievementVO>> getPublicResearchAchievementPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<ResearchAchievementVO> achievementPage = researchAchievementService.getPublicResearchAchievementPage(page - 1, size);
        return ResultVO.success(achievementPage);
    }
    
    @GetMapping("/public/type/{type}")
    @Operation(summary = "根据类型获取公开科研成果列表", description = "根据类型分页获取公开的科研成果列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<Page<ResearchAchievementVO>> getPublicResearchAchievementPageByType(
            @PathVariable String type,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<ResearchAchievementVO> achievementPage = researchAchievementService.getPublicResearchAchievementPageByType(type, page - 1, size);
        return ResultVO.success(achievementPage);
    }
    
    @GetMapping("/unverified")
    @Operation(summary = "获取待认证科研成果列表", description = "分页获取待认证的科研成果列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @PreAuthorize("hasRole('TEACHER') or hasRole('SCH_ADMIN')")
    public ResultVO<Page<ResearchAchievementVO>> getUnverifiedResearchAchievementPage(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Integer organizationId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<ResearchAchievementVO> achievementPage = researchAchievementService.getUnverifiedResearchAchievementPage(organizationId, page - 1, size);
        return ResultVO.success(achievementPage);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "获取科研成果详情", description = "根据ID获取科研成果详情")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "科研成果不存在",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<ResearchAchievementVO> getResearchAchievementDetail(
            @PathVariable Integer id,
            HttpServletRequest request) {
        String viewerIp = RequestUtils.getClientIp(request);
        ResearchAchievementVO achievement = researchAchievementService.getResearchAchievementDetail(id, viewerIp);
        return ResultVO.success(achievement);
    }
    
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "创建科研成果", description = "创建新的科研成果，可同时上传文件和封面图片")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "创建成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "400", description = "参数错误",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @PreAuthorize("hasRole('STUDENT')")
    public ResultVO<ResearchAchievementVO> createResearchAchievement(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("title") String title,
            @RequestParam("type") String type,
            @RequestParam("authors") String authors,
            @RequestParam(value = "publicationDate", required = false) String publicationDate,
            @RequestParam(value = "publisher", required = false) String publisher,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "isPublic", required = false, defaultValue = "true") Boolean isPublic,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestPart(value = "coverImage", required = false) MultipartFile coverImage) {
        
        Integer userId = ((CustomUserDetails) userDetails).getUserId();

        // 创建DTO对象
        ResearchAchievementCreationDTO dto = new ResearchAchievementCreationDTO();
        dto.setTitle(title);
        dto.setType(type);
        dto.setAuthors(authors);
        if (publicationDate != null && !publicationDate.isEmpty()) {
            dto.setPublicationDate(LocalDate.parse(publicationDate));
        }
        dto.setPublisher(publisher);
        dto.setDescription(description);
        dto.setIsPublic(isPublic);
        
        ResearchAchievementVO achievement = researchAchievementService.createResearchAchievement(userId, dto, file, coverImage);
        return ResultVO.success(achievement);
    }
    
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "更新科研成果", description = "更新指定ID的科研成果，可同时上传文件和封面图片")
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
        @ApiResponse(responseCode = "404", description = "科研成果不存在",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @PreAuthorize("hasRole('STUDENT')")
    public ResultVO<ResearchAchievementVO> updateResearchAchievement(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("title") String title,
            @RequestParam("type") String type,
            @RequestParam("authors") String authors,
            @RequestParam(value = "publicationDate", required = false) String publicationDate,
            @RequestParam(value = "publisher", required = false) String publisher,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "isPublic", required = false, defaultValue = "true") Boolean isPublic,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestPart(value = "coverImage", required = false) MultipartFile coverImage) {
        
        Integer userId = ((CustomUserDetails) userDetails).getUserId();
        // 创建DTO对象
        ResearchAchievementCreationDTO dto = new ResearchAchievementCreationDTO();
        dto.setTitle(title);
        dto.setType(type);
        dto.setAuthors(authors);
        if (publicationDate != null && !publicationDate.isEmpty()) {
            dto.setPublicationDate(LocalDate.parse(publicationDate));
        }
        dto.setPublisher(publisher);
        dto.setDescription(description);
        dto.setIsPublic(isPublic);
        
        ResearchAchievementVO achievement = researchAchievementService.updateResearchAchievement(id, userId, dto, file, coverImage);
        return ResultVO.success(achievement);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "删除科研成果", description = "删除指定ID的科研成果")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "删除成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "403", description = "无权限",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "科研成果不存在",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @PreAuthorize("hasRole('STUDENT')")
    public ResultVO<Boolean> deleteResearchAchievement(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails) {
        Integer userId = ((CustomUserDetails) userDetails).getUserId();
        boolean result = researchAchievementService.deleteResearchAchievement(id, userId);
        return ResultVO.success(result);
    }
    
    @PostMapping("/{id}/verify")
    @Operation(summary = "认证科研成果", description = "认证指定ID的科研成果")
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
        @ApiResponse(responseCode = "404", description = "科研成果不存在",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @PreAuthorize("hasRole('TEACHER') or hasRole('SCH_ADMIN')")
    public ResultVO<ResearchAchievementVO> verifyResearchAchievement(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody AchievementVerifyDTO verifyDTO) {
        Integer verifierId = ((CustomUserDetails) userDetails).getUserId();
        ResearchAchievementVO achievement = researchAchievementService.verifyResearchAchievement(id, verifierId, verifyDTO);
        return ResultVO.success(achievement);
    }
} 