package com.csu.unicorp.controller.course;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.csu.unicorp.dto.CourseQuestionDTO;
import com.csu.unicorp.service.CourseQuestionService;
import com.csu.unicorp.vo.CourseQuestionVO;
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

import java.util.Map;

/**
 * 课程问答控制器
 */
@RestController
@RequestMapping("/v1/course-questions")
@RequiredArgsConstructor
@Tag(name = "课程问答", description = "提供课程问答的提问、回答、查询等功能")
public class CourseQuestionController {

    private final CourseQuestionService questionService;

    @PostMapping
    @Operation(summary = "学生提问", description = "学生在课程中提出问题")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "提问成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "400", description = "参数错误",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "404", description = "课程不存在",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<CourseQuestionVO> askQuestion(
            @RequestBody @Valid CourseQuestionDTO questionDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResultVO.success("提问成功", questionService.askQuestion(questionDTO, userDetails));
    }

    @PutMapping("/{id}/answer")
    @Operation(summary = "教师回答问题", description = "教师或导师回答学生提出的问题")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "回答成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "403", description = "无权限",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "404", description = "问题不存在",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class)))
    })
    @PreAuthorize("hasAnyRole('TEACHER', 'SCH_ADMIN', 'EN_TEACHER')")
    public ResultVO<CourseQuestionVO> answerQuestion(
            @PathVariable @Parameter(description = "问题ID") Integer id,
            @RequestParam @Parameter(description = "回答内容") String answer,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResultVO.success("回答问题成功", questionService.answerQuestion(id, answer, userDetails));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取问题详情", description = "获取问题详情")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "404", description = "问题不存在",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<CourseQuestionVO> getQuestionDetail(
            @PathVariable @Parameter(description = "问题ID") Integer id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResultVO.success("获取问题详情成功", questionService.getQuestionDetail(id, userDetails));
    }

    @GetMapping("/course/{courseId}")
    @Operation(summary = "获取课程问题列表", description = "分页获取课程的所有问题列表")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "404", description = "课程不存在",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<IPage<CourseQuestionVO>> getCourseQuestions(
            @PathVariable @Parameter(description = "课程ID") Integer courseId,
            @RequestParam(defaultValue = "1") @Parameter(description = "页码") Integer page,
            @RequestParam(defaultValue = "10") @Parameter(description = "每页数量") Integer size,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResultVO.success("获取课程问题列表成功", questionService.getCourseQuestions(courseId, page, size, userDetails));
    }

    @GetMapping("/chapter/{chapterId}")
    @Operation(summary = "获取章节问题列表", description = "分页获取章节的所有问题列表")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "404", description = "章节不存在",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<IPage<CourseQuestionVO>> getChapterQuestions(
            @PathVariable @Parameter(description = "章节ID") Integer chapterId,
            @RequestParam(defaultValue = "1") @Parameter(description = "页码") Integer page,
            @RequestParam(defaultValue = "10") @Parameter(description = "每页数量") Integer size,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResultVO.success("获取章节问题列表成功", questionService.getChapterQuestions(chapterId, page, size, userDetails));
    }

    @GetMapping("/course/{courseId}/student/{studentId}")
    @Operation(summary = "获取学生问题列表", description = "分页获取学生在课程中提出的问题列表")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "404", description = "课程或学生不存在",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<IPage<CourseQuestionVO>> getStudentQuestions(
            @PathVariable @Parameter(description = "课程ID") Integer courseId,
            @PathVariable @Parameter(description = "学生ID") Integer studentId,
            @RequestParam(defaultValue = "1") @Parameter(description = "页码") Integer page,
            @RequestParam(defaultValue = "10") @Parameter(description = "每页数量") Integer size,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResultVO.success("获取学生问题列表成功", questionService.getStudentQuestions(courseId, studentId, page, size, userDetails));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新问题", description = "更新问题标题和内容（仅限提问者本人）")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "更新成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "403", description = "无权限",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "404", description = "问题不存在",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<CourseQuestionVO> updateQuestion(
            @PathVariable @Parameter(description = "问题ID") Integer id,
            @RequestParam @Parameter(description = "问题标题") String title,
            @RequestParam @Parameter(description = "问题内容") String content,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResultVO.success("更新问题成功", questionService.updateQuestion(id, title, content, userDetails));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除问题", description = "删除问题（仅限提问者本人或教师/管理员）")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "删除成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "403", description = "无权限",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "404", description = "问题不存在",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<Boolean> deleteQuestion(
            @PathVariable @Parameter(description = "问题ID") Integer id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResultVO.success("删除问题成功", questionService.deleteQuestion(id, userDetails));
    }

    @GetMapping("/course/{courseId}/statistics")
    @Operation(summary = "获取问答统计数据", description = "获取课程的问答统计数据")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "404", description = "课程不存在",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<Map<String, Object>> getQuestionStatistics(
            @PathVariable @Parameter(description = "课程ID") Integer courseId) {
        return ResultVO.success("获取问答统计数据成功", questionService.getQuestionStatistics(courseId));
    }

    @GetMapping("/course/{courseId}/teacher/{teacherId}/pending-count")
    @Operation(summary = "获取待回答问题数量", description = "获取指定教师在课程中需要回答的问题数量")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "404", description = "课程或教师不存在",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<Integer> getPendingQuestionsCount(
            @PathVariable @Parameter(description = "课程ID") Integer courseId,
            @PathVariable @Parameter(description = "教师ID") Integer teacherId) {
        return ResultVO.success("获取待回答问题数量成功", questionService.countPendingQuestionsForTeacher(courseId, teacherId));
    }
} 