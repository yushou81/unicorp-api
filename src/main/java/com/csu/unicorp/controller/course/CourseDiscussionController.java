package com.csu.unicorp.controller.course;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.csu.unicorp.dto.CourseDiscussionDTO;
import com.csu.unicorp.service.CourseDiscussionService;
import com.csu.unicorp.vo.CourseDiscussionVO;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 课程讨论控制器
 */
@RestController
@RequestMapping("/v1/course-discussions")
@RequiredArgsConstructor
@Tag(name = "课程讨论", description = "提供课程讨论的创建、回复、查询等功能")
public class CourseDiscussionController {

    private final CourseDiscussionService discussionService;

    @PostMapping
    @Operation(summary = "创建讨论", description = "在课程中创建新的讨论主题")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "创建成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "400", description = "参数错误",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "404", description = "课程不存在",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<CourseDiscussionVO> createDiscussion(
            @RequestBody @Valid CourseDiscussionDTO discussionDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResultVO.success("创建讨论成功", discussionService.createDiscussion(discussionDTO, userDetails));
    }

    @PostMapping("/reply")
    @Operation(summary = "回复讨论", description = "回复已有的讨论")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "回复成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "400", description = "参数错误",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "404", description = "讨论不存在",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<CourseDiscussionVO> replyToDiscussion(
            @RequestBody @Valid CourseDiscussionDTO replyDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResultVO.success("回复讨论成功", discussionService.replyToDiscussion(replyDTO, userDetails));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取讨论详情", description = "获取讨论详情（包含回复）")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "404", description = "讨论不存在",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<CourseDiscussionVO> getDiscussionDetail(
            @PathVariable @Parameter(description = "讨论ID") Integer id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResultVO.success("获取讨论详情成功", discussionService.getDiscussionDetail(id, userDetails));
    }

    @GetMapping("/course/{courseId}")
    @Operation(summary = "获取课程讨论列表", description = "分页获取课程的所有讨论列表")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "404", description = "课程不存在",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<IPage<CourseDiscussionVO>> getCourseDiscussions(
            @PathVariable @Parameter(description = "课程ID") Integer courseId,
            @RequestParam(defaultValue = "1") @Parameter(description = "页码") Integer page,
            @RequestParam(defaultValue = "10") @Parameter(description = "每页数量") Integer size,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResultVO.success("获取课程讨论列表成功", discussionService.getCourseDiscussions(courseId, page, size, userDetails));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新讨论内容", description = "更新讨论内容（仅限自己发布的讨论）")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "更新成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "403", description = "无权限",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "404", description = "讨论不存在",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<CourseDiscussionVO> updateDiscussion(
            @PathVariable @Parameter(description = "讨论ID") Integer id,
            @RequestParam @Parameter(description = "更新的内容") String content,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResultVO.success("更新讨论内容成功", discussionService.updateDiscussion(id, content, userDetails));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除讨论", description = "删除讨论（仅限自己发布的讨论或教师/管理员）")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "删除成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "403", description = "无权限",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "404", description = "讨论不存在",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<Boolean> deleteDiscussion(
            @PathVariable @Parameter(description = "讨论ID") Integer id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResultVO.success("删除讨论成功", discussionService.deleteDiscussion(id, userDetails));
    }

    @GetMapping("/{id}/replies")
    @Operation(summary = "获取讨论回复", description = "获取讨论的所有回复")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "404", description = "讨论不存在",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<List<CourseDiscussionVO>> getDiscussionReplies(
            @PathVariable @Parameter(description = "讨论ID") Integer id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResultVO.success("获取讨论回复成功", discussionService.getDiscussionReplies(id, userDetails));
    }

    @GetMapping("/course/{courseId}/count")
    @Operation(summary = "统计课程讨论数量", description = "统计课程的讨论数量")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "统计成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "404", description = "课程不存在",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<Integer> countCourseDiscussions(
            @PathVariable @Parameter(description = "课程ID") Integer courseId) {
        return ResultVO.success("统计课程讨论数量成功", discussionService.countCourseDiscussions(courseId));
    }
} 