package com.csu.unicorp.controller.course;

import com.csu.unicorp.dto.ChapterVideoDTO;
import com.csu.unicorp.service.ChapterVideoService;
import com.csu.unicorp.vo.ChapterVideoVO;
import com.csu.unicorp.vo.ResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 章节视频控制器
 */
@Slf4j
@RestController
@RequestMapping("/v1/chapter-videos")
@RequiredArgsConstructor
@Tag(name = "章节视频", description = "章节视频API，提供视频上传、查询、播放等功能")
public class ChapterVideoController {

    private final ChapterVideoService videoService;
    
    /**
     * 上传视频
     */
    @Operation(summary = "上传章节视频", description = "为指定章节上传视频，需要教师或管理员权限")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "上传成功",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "400", description = "参数错误或权限不足",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ResultVO.class)))
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('TEACHER', 'SCH_ADMIN')")
    public ResultVO<ChapterVideoVO> uploadVideo(
            @RequestPart("file") MultipartFile file,
            @RequestParam("chapterId") Integer chapterId,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        
        // 创建ChapterVideoDTO对象
        ChapterVideoDTO videoDTO = new ChapterVideoDTO();
        videoDTO.setChapterId(chapterId);
        videoDTO.setTitle(title);
        videoDTO.setDescription(description);
        
        ChapterVideoVO video = videoService.uploadVideo(file, videoDTO, userDetails);
        return ResultVO.success("视频上传成功", video);
    }
    
    /**
     * 获取视频详情
     */
    @Operation(summary = "获取视频详情", description = "获取指定ID的视频详细信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "视频不存在",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ResultVO.class)))
    })
    @GetMapping("/{videoId}")
    public ResultVO<ChapterVideoVO> getVideoById(
            @PathVariable @Parameter(description = "视频ID") Integer videoId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        ChapterVideoVO video = videoService.getVideoById(videoId, userDetails);
        return ResultVO.success("获取视频详情成功", video);
    }
    
    /**
     * 获取章节视频
     */
    @Operation(summary = "获取章节视频", description = "获取指定章节的视频")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "章节不存在",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ResultVO.class)))
    })
    @GetMapping("/chapter/{chapterId}")
    public ResultVO<ChapterVideoVO> getVideoByChapterId(
            @PathVariable @Parameter(description = "章节ID") Integer chapterId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        ChapterVideoVO video = videoService.getVideoByChapterId(chapterId, userDetails);
        if (video == null) {
            return ResultVO.success("章节暂无视频", null);
        }
        return ResultVO.success("获取章节视频成功", video);
    }
    
    /**
     * 删除视频
     */
    @Operation(summary = "删除视频", description = "删除指定ID的视频，需要教师或管理员权限")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "删除成功",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "403", description = "权限不足",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "视频不存在",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ResultVO.class)))
    })
    @DeleteMapping("/{videoId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'SCH_ADMIN')")
    public ResultVO<Boolean> deleteVideo(
            @PathVariable @Parameter(description = "视频ID") Integer videoId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        boolean result = videoService.deleteVideo(videoId, userDetails);
        return ResultVO.success("视频删除成功", result);
    }
    
    /**
     * 更新视频信息
     */
    @Operation(summary = "更新视频信息", description = "更新指定ID的视频信息，需要教师或管理员权限")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "更新成功",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "403", description = "权限不足",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "视频不存在",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ResultVO.class)))
    })
    @PutMapping("/{videoId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'SCH_ADMIN')")
    public ResultVO<ChapterVideoVO> updateVideo(
            @PathVariable @Parameter(description = "视频ID") Integer videoId,
            @RequestBody ChapterVideoDTO videoDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        ChapterVideoVO video = videoService.updateVideo(videoId, videoDTO, userDetails);
        return ResultVO.success("视频信息更新成功", video);
    }
    
    /**
     * 更新观看进度
     */
    @Operation(summary = "更新观看进度", description = "更新视频观看进度，仅限学生使用")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "更新成功",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "403", description = "权限不足",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "视频不存在",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ResultVO.class)))
    })
    @PostMapping("/{videoId}/progress")
    @PreAuthorize("hasRole('STUDENT')")
    public ResultVO<Boolean> updateWatchProgress(
            @PathVariable @Parameter(description = "视频ID") Integer videoId,
            @RequestParam @Parameter(description = "当前位置(秒)") Integer position,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        boolean result = videoService.updateWatchProgress(videoId, position, userDetails);
        return ResultVO.success("更新观看进度成功", result);
    }
    
    /**
     * 标记视频为已完成
     */
    @Operation(summary = "标记视频为已完成", description = "标记视频为已完成状态，仅限学生使用")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "标记成功",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "403", description = "权限不足",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "视频不存在",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ResultVO.class)))
    })
    @PostMapping("/{videoId}/complete")
    @PreAuthorize("hasRole('STUDENT')")
    public ResultVO<Boolean> markVideoCompleted(
            @PathVariable @Parameter(description = "视频ID") Integer videoId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        boolean result = videoService.markVideoCompleted(videoId, userDetails);
        return ResultVO.success("标记视频完成成功", result);
    }
} 