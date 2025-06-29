package com.csu.unicorp.controller;

import com.csu.unicorp.service.FileService;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 文件上传控制器
 */
@Tag(name = "File Upload", description = "文件上传服务")
@RestController
@RequestMapping("/v1/files")
@RequiredArgsConstructor
public class FileController {
    
    private final FileService fileService;
    
    /**
     * 文件上传接口
     */
    @Operation(summary = "通用文件上传接口", description = "用于上传文件（如资源文件、营业执照、头像、简历等），成功后返回文件的访问URL。")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "文件上传成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "400", description = "文件为空或超过大小限制",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/upload")
    public ResultVO<Map<String, String>> uploadFile(
            @Parameter(description = "要上传的文件") @RequestParam("file") MultipartFile file,
            @Parameter(description = "文件类型标识 (e.g., avatar, resume, resource)") @RequestParam(value = "type", required = false, defaultValue = "resource") String type) {
        
        String fileUrl = fileService.uploadFile(file, type);
        
        Map<String, String> result = new HashMap<>();
        result.put("file_url", fileUrl);
        
        return ResultVO.success("文件上传成功", result);
    }
} 