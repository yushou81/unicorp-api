package com.csu.unicorp.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.csu.unicorp.dto.CourseChapterDTO;
import com.csu.unicorp.service.CourseChapterService;
import com.csu.unicorp.vo.CourseChapterVO;
import com.csu.unicorp.vo.ResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 课程章节控制器
 */
@RestController
@RequestMapping("/v1/course-chapters")
@RequiredArgsConstructor
@Tag(name = "课程章节", description = "提供课程章节的创建、更新、删除、查询等功能")
public class CourseChapterController {

    private final CourseChapterService chapterService;

    @PostMapping
    @Operation(summary = "创建课程章节", description = "为指定课程创建新的章节，需要教师或管理员权限")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "创建成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "400", description = "参数错误",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "403", description = "无权限",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class)))
    })
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResultVO<CourseChapterVO> createChapter(
            @RequestBody @Valid CourseChapterDTO chapterDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResultVO.success("章节创建成功", chapterService.createChapter(chapterDTO, userDetails));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新课程章节", description = "更新指定ID的章节信息，需要教师或管理员权限")
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
            @ApiResponse(responseCode = "404", description = "章节不存在",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class)))
    })
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResultVO<CourseChapterVO> updateChapter(
            @PathVariable @Parameter(description = "章节ID") Integer id,
            @RequestBody @Valid CourseChapterDTO chapterDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResultVO.success("章节更新成功", chapterService.updateChapter(id, chapterDTO, userDetails));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除课程章节", description = "删除指定ID的章节，需要教师或管理员权限")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "删除成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "403", description = "无权限",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "404", description = "章节不存在",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class)))
    })
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResultVO<Boolean> deleteChapter(
            @PathVariable @Parameter(description = "章节ID") Integer id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResultVO.success("章节删除成功", chapterService.deleteChapter(id, userDetails));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取章节详情", description = "获取指定ID章节的详细信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "404", description = "章节不存在",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<CourseChapterVO> getChapterDetail(
            @PathVariable @Parameter(description = "章节ID") Integer id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResultVO.success("获取章节详情成功", chapterService.getChapterDetail(id, userDetails));
    }

    @GetMapping("/course/{courseId}")
    @Operation(summary = "获取课程章节列表", description = "获取指定课程的所有章节列表")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "404", description = "课程不存在",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<List<CourseChapterVO>> getChaptersByCourse(
            @PathVariable @Parameter(description = "课程ID") Integer courseId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResultVO.success("获取课程章节列表成功", chapterService.getChaptersByCourse(courseId, userDetails));
    }

    @PutMapping("/{id}/publish")
    @Operation(summary = "更新章节发布状态", description = "更新指定章节的发布状态，需要教师或管理员权限")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "更新成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "403", description = "无权限",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "404", description = "章节不存在",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class)))
    })
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResultVO<CourseChapterVO> updatePublishStatus(
            @PathVariable @Parameter(description = "章节ID") Integer id,
            @RequestParam @Parameter(description = "是否发布") Boolean isPublished,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResultVO.success("更新章节发布状态成功", chapterService.updateChapterPublishStatus(id, isPublished, userDetails));
    }

    @PutMapping("/{id}/sequence")
    @Operation(summary = "更新章节顺序", description = "更新指定章节的顺序，需要教师或管理员权限")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "更新成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "403", description = "无权限",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "404", description = "章节不存在",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class)))
    })
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResultVO<Boolean> updateSequence(
            @PathVariable @Parameter(description = "章节ID") Integer id,
            @RequestParam @Parameter(description = "顺序") Integer sequence,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResultVO.success("更新章节顺序成功", chapterService.updateChapterSequence(id, sequence, userDetails));
    }

    @PostMapping("/{id}/resources/{resourceId}")
    @Operation(summary = "关联资源到章节", description = "将指定资源关联到章节中，需要教师或管理员权限")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "关联成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "403", description = "无权限",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "404", description = "章节或资源不存在",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class)))
    })
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResultVO<Boolean> associateResource(
            @PathVariable @Parameter(description = "章节ID") Integer id,
            @PathVariable @Parameter(description = "资源ID") Integer resourceId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResultVO.success("关联资源到章节成功", chapterService.associateResourceToChapter(id, resourceId, userDetails));
    }

    @DeleteMapping("/{id}/resources/{resourceId}")
    @Operation(summary = "从章节中移除资源", description = "将指定资源从章节中移除，需要教师或管理员权限")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "移除成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "403", description = "无权限",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "404", description = "章节或资源不存在",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class)))
    })
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResultVO<Boolean> removeResource(
            @PathVariable @Parameter(description = "章节ID") Integer id,
            @PathVariable @Parameter(description = "资源ID") Integer resourceId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResultVO.success("从章节中移除资源成功", chapterService.removeResourceFromChapter(id, resourceId, userDetails));
    }

    @GetMapping("/{id}/resources")
    @Operation(summary = "获取章节关联的资源列表", description = "获取指定章节关联的所有资源ID列表")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "404", description = "章节不存在",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<List<Integer>> getChapterResources(
            @PathVariable @Parameter(description = "章节ID") Integer id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResultVO.success("获取章节关联资源列表成功", chapterService.getChapterResources(id, userDetails));
    }
} 