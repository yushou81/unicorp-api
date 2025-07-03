package com.csu.unicorp.controller;

import com.csu.unicorp.config.security.CustomUserDetails;
import com.csu.unicorp.dto.ResumeCreationDTO;
import com.csu.unicorp.dto.ResumeUpdateDTO;
import com.csu.unicorp.service.FileService;
import com.csu.unicorp.service.ResumeService;
import com.csu.unicorp.vo.ResumeVO;
import com.csu.unicorp.vo.ResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 简历管理控制器
 */
@Tag(name = "Resumes", description = "简历管理")
@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
@Slf4j
public class ResumeController {
    
    private final ResumeService resumeService;
    private final FileService fileService;
    
    /**
     * 获取指定用户的所有简历列表
     */
    @Operation(summary = "获取指定用户的所有简历列表", description = "获取一个用户的所有公开简历信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取用户简历列表",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "用户未找到",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @GetMapping("/resumes/user/{userId}")
    public ResultVO<List<ResumeVO>> getUserResumes(@PathVariable Integer userId) {
        List<ResumeVO> resumes = resumeService.getUserResumes(userId);
        // 处理简历URL，将相对路径转换为完整URL
        resumes.forEach(resume -> {
            if (resume.getResumeUrl() != null && !resume.getResumeUrl().isEmpty()) {
                resume.setResumeUrl(fileService.getFullFileUrl(resume.getResumeUrl()));
            }
        });
        return ResultVO.success("获取用户简历列表成功", resumes);
    }
    
    /**
     * 获取指定ID的简历
     */
    @Operation(summary = "获取指定ID的简历", description = "根据简历ID获取简历详情")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取简历信息",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "简历未找到",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @GetMapping("/resumes/{resumeId}")
    public ResultVO<ResumeVO> getResumeById(@PathVariable Integer resumeId) {
        ResumeVO resume = resumeService.getResumeById(resumeId);
        // 处理简历URL，将相对路径转换为完整URL
        if (resume.getResumeUrl() != null && !resume.getResumeUrl().isEmpty()) {
            resume.setResumeUrl(fileService.getFullFileUrl(resume.getResumeUrl()));
        }
        return ResultVO.success("获取简历信息成功", resume);
    }
    
    /**
     * 获取我的所有简历列表
     */
    @Operation(summary = "获取我的所有简历列表", description = "获取当前登录用户的所有简历列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取简历列表",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "401", description = "未授权",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/me/resumes")
    public ResultVO<List<ResumeVO>> getMyResumes(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<ResumeVO> resumes = resumeService.getUserResumes(userDetails.getUser().getId());
        // 处理简历URL，将相对路径转换为完整URL
        resumes.forEach(resume -> {
            if (resume.getResumeUrl() != null && !resume.getResumeUrl().isEmpty()) {
                resume.setResumeUrl(fileService.getFullFileUrl(resume.getResumeUrl()));
            }
        });
        return ResultVO.success("获取简历列表成功", resumes);
    }
    
    /**
     * 创建新简历
     */
    @Operation(summary = "创建我的简历", description = "为当前登录用户创建新的简历，同时上传简历文件")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "简历创建成功", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "400", description = "请求参数有误",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "401", description = "未授权",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/me/resume")
    public ResultVO<ResumeVO> createMyResume(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam String major,
            @RequestParam String educationLevel, 
            @RequestParam String achievements,
            @RequestParam MultipartFile file) {
        
        log.info("创建简历请求 - 用户ID: {}", userDetails.getUser().getId());
        
        // 上传文件，获取相对路径
        String resumeRelativePath = null;
        if (file != null && !file.isEmpty()) {
            log.info("上传简历文件 - 文件名: {}, 大小: {}", file.getOriginalFilename(), file.getSize());
            resumeRelativePath = fileService.uploadFile(file, "resume");
            log.info("简历文件上传成功 - 相对路径: {}", resumeRelativePath);
        }
        
        // 构建简历DTO并创建简历
        ResumeCreationDTO dto = new ResumeCreationDTO();
        dto.setMajor(major);
        dto.setEducationLevel(educationLevel);
        dto.setResumeUrl(resumeRelativePath);
        dto.setAchievements(achievements);
        
        ResumeVO createdResume = resumeService.createResume(userDetails.getUser().getId(), dto);
        
        // 返回前将相对路径转换为完整URL
        if (createdResume.getResumeUrl() != null && !createdResume.getResumeUrl().isEmpty()) {
            createdResume.setResumeUrl(fileService.getFullFileUrl(createdResume.getResumeUrl()));
        }
        
        log.info("简历创建成功 - 简历ID: {}", createdResume.getId());
        
        return ResultVO.success("简历创建成功", createdResume);
    }
    
    /**
     * 更新我的简历（带文件上传）
     */
    @Operation(summary = "更新我的简历", description = "更新当前登录用户的简历信息，同时上传新的简历文件")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "简历更新成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "400", description = "请求参数有误",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "401", description = "未授权",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "简历不存在",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping(value = "/me/resume/{resumeId}")
    public ResultVO<ResumeVO> updateMyResume(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Integer resumeId,
            @RequestParam(required = false) String major,
            @RequestParam(required = false) String educationLevel,
            @RequestParam(required = false) String achievements,
            @RequestParam(required = false) MultipartFile file) {
        
        log.info("更新简历请求 - 用户ID: {}, 简历ID: {}", userDetails.getUser().getId(), resumeId);
        
        // 上传文件，获取相对路径
        String resumeRelativePath = null;
        if (file != null && !file.isEmpty()) {
            log.info("更新简历文件 - 文件名: {}, 大小: {}", file.getOriginalFilename(), file.getSize());
            resumeRelativePath = fileService.uploadFile(file, "resume");
            log.info("简历文件更新成功 - 相对路径: {}", resumeRelativePath);
        }
        
        // 构建简历DTO并更新简历
        ResumeUpdateDTO dto = new ResumeUpdateDTO();
        dto.setMajor(major);
        dto.setEducationLevel(educationLevel);
        dto.setResumeUrl(resumeRelativePath);
        dto.setAchievements(achievements);
        
        log.info("调用服务更新简历 - 专业: {}, 教育水平: {}", major, educationLevel);
        ResumeVO updatedResume = resumeService.updateResume(userDetails.getUser().getId(), resumeId, dto);
        
        // 返回前将相对路径转换为完整URL
        if (updatedResume.getResumeUrl() != null && !updatedResume.getResumeUrl().isEmpty()) {
            updatedResume.setResumeUrl(fileService.getFullFileUrl(updatedResume.getResumeUrl()));
        }
        
        log.info("简历更新成功 - 简历ID: {}", updatedResume.getId());
        
        return ResultVO.success("简历更新成功", updatedResume);
    }
    
    /**
     * 删除我的简历
     */
    @Operation(summary = "删除我的简历", description = "删除当前登录用户的指定简历")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "简历删除成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "401", description = "未授权",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "简历不存在",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/me/resume/{resumeId}")
    public ResultVO<Void> deleteMyResume(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Integer resumeId) {
        resumeService.deleteResume(userDetails.getUser().getId(), resumeId);
        return ResultVO.success("简历删除成功");
    }
} 