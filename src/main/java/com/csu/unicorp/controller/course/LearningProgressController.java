package com.csu.unicorp.controller.course;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.csu.unicorp.dto.LearningProgressDTO;
import com.csu.unicorp.service.LearningProgressService;
import com.csu.unicorp.vo.LearningProgressVO;
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
import java.util.Map;

/**
 * 学习进度控制器
 */
@RestController
@RequestMapping("/v1/learning-progress")
@RequiredArgsConstructor
@Tag(name = "学习进度", description = "提供学习进度的更新、查询等功能")
public class LearningProgressController {

    private final LearningProgressService progressService;

    @PutMapping
    @Operation(summary = "更新学习进度", description = "更新学生在章节中的学习进度")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "更新成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "400", description = "参数错误",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "404", description = "章节不存在",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<LearningProgressVO> updateProgress(
            @RequestBody @Valid LearningProgressDTO progressDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResultVO.success("更新学习进度成功", progressService.updateProgress(progressDTO, userDetails));
    }

    @GetMapping("/chapter/{chapterId}/student/{studentId}")
    @Operation(summary = "获取学生章节进度", description = "获取指定学生在特定章节中的学习进度")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "404", description = "章节或学生不存在",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<LearningProgressVO> getStudentProgressInChapter(
            @PathVariable @Parameter(description = "章节ID") Integer chapterId,
            @PathVariable @Parameter(description = "学生ID") Integer studentId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResultVO.success("获取学生章节进度成功", progressService.getStudentProgressInChapter(chapterId, studentId, userDetails));
    }

    @GetMapping("/course/{courseId}/student/{studentId}")
    @Operation(summary = "获取学生课程进度", description = "获取指定学生在整个课程中的学习进度")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "400", description = "学生未报名该课程或参数错误",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "404", description = "课程或学生不存在",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<List<LearningProgressVO>> getStudentProgressInCourse(
            @PathVariable @Parameter(description = "课程ID") Integer courseId,
            @PathVariable @Parameter(description = "学生ID") Integer studentId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResultVO.success("获取学生课程进度成功", progressService.getStudentProgressInCourse(courseId, studentId, userDetails));
    }

    @GetMapping("/chapter/{chapterId}/students")
    @Operation(summary = "获取章节所有学生进度", description = "获取特定章节中所有学生的学习进度")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "404", description = "章节不存在",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class)))
    })
    @PreAuthorize("hasAnyRole('TEACHER', 'SCH_ADMIN', 'EN_TEACHER')")
    public ResultVO<IPage<LearningProgressVO>> getChapterStudentProgress(
            @PathVariable @Parameter(description = "章节ID") Integer chapterId,
            @RequestParam(defaultValue = "1") @Parameter(description = "页码") Integer page,
            @RequestParam(defaultValue = "10") @Parameter(description = "每页数量") Integer size,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResultVO.success("获取章节所有学生进度成功", progressService.getChapterStudentProgress(chapterId, page, size, userDetails));
    }

    @GetMapping("/course/{courseId}/overview")
    @Operation(summary = "获取课程进度概览", description = "获取课程的整体学习进度概览")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "404", description = "课程不存在",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class)))
    })
    @PreAuthorize("hasAnyRole('TEACHER', 'SCH_ADMIN', 'EN_TEACHER')")
    public ResultVO<Map<String, Object>> getCourseProgressOverview(
            @PathVariable @Parameter(description = "课程ID") Integer courseId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResultVO.success("获取课程进度概览成功", progressService.getCourseProgressOverview(courseId, userDetails));
    }

    @GetMapping("/course/{courseId}/student/{studentId}/completion-rate")
    @Operation(summary = "获取学生课程完成率", description = "获取指定学生在课程中的完成率")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "404", description = "课程或学生不存在",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<Integer> getCourseCompletionRate(
            @PathVariable @Parameter(description = "课程ID") Integer courseId,
            @PathVariable @Parameter(description = "学生ID") Integer studentId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResultVO.success("获取学生课程完成率成功", progressService.calculateCourseCompletionRate(courseId, studentId, userDetails));
    }

    @PostMapping("/chapter/{chapterId}/initialize")
    @Operation(summary = "初始化章节学习进度", description = "为新章节初始化所有已报名学生的学习进度记录")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "初始化成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "403", description = "无权限",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "404", description = "章节不存在",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class)))
    })
    @PreAuthorize("hasAnyRole('TEACHER', 'SCH_ADMIN','STUDENT')")
    public ResultVO<Boolean> initializeChapterProgress(
            @PathVariable @Parameter(description = "章节ID") Integer chapterId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResultVO.success("初始化章节学习进度成功", progressService.initializeChapterProgress(chapterId, userDetails));
    }
} 