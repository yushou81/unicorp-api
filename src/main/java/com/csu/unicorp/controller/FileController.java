package com.csu.unicorp.controller;

import com.csu.unicorp.service.FileService;
import com.csu.unicorp.vo.ResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
     * 通用文件上传接口
     */
    @Operation(summary = "通用文件上传接口", description = "用于上传文件（如资源文件、营业执照等），成功后返回文件的访问URL")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "文件上传成功", 
                content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "400", description = "文件为空或格式不正确")
    })
    @PostMapping("/upload")
    public ResponseEntity<ResultVO<Map<String, String>>> uploadFile(@RequestParam("file") MultipartFile file) {
        String fileUrl = fileService.uploadFile(file);
        
        Map<String, String> result = new HashMap<>();
        result.put("file_url", fileUrl);
        
        return ResponseEntity.ok(ResultVO.success("文件上传成功", result));
    }
} 