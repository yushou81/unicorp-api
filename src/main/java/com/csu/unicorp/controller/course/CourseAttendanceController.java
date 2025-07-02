package com.csu.unicorp.controller.course;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.csu.unicorp.dto.CourseAttendanceDTO;
import com.csu.unicorp.service.CourseAttendanceService;
import com.csu.unicorp.vo.CourseAttendanceVO;
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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 课程出勤控制器
 */
@RestController
@RequestMapping("/v1/course-attendance")
@RequiredArgsConstructor
@Tag(name = "课程出勤", description = "提供课程出勤记录、统计等功能")
public class CourseAttendanceController {

    private final CourseAttendanceService attendanceService;

    @PostMapping
    @Operation(summary = "记录课程出勤", description = "记录课程的学生出勤情况，需要教师或管理员权限")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "记录成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "400", description = "参数错误",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "403", description = "无权限",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class)))
    })
    @PreAuthorize("hasAnyRole('TEACHER', 'SYSADMIN')")
    public ResultVO<Boolean> recordAttendance(
            @RequestBody @Valid CourseAttendanceDTO attendanceDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResultVO.success("记录出勤成功", attendanceService.recordAttendance(attendanceDTO, userDetails));
    }

    @GetMapping("/course/{courseId}/date/{date}")
    @Operation(summary = "获取课程某天的出勤记录", description = "获取课程在指定日期的所有学生出勤记录")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "404", description = "课程不存在",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class)))
    })
    @PreAuthorize("hasAnyRole('TEACHER', 'SYSADMIN', 'EN_TEACHER')")
    public ResultVO<List<CourseAttendanceVO>> getCourseAttendanceByDate(
            @PathVariable @Parameter(description = "课程ID") Integer courseId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @Parameter(description = "日期") LocalDate date,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResultVO.success("获取出勤记录成功", attendanceService.getCourseAttendanceByDate(courseId, date, userDetails));
    }

    @GetMapping("/course/{courseId}/student/{studentId}")
    @Operation(summary = "获取学生出勤记录", description = "获取学生在课程中的所有出勤记录")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "404", description = "课程或学生不存在",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<List<CourseAttendanceVO>> getStudentAttendance(
            @PathVariable @Parameter(description = "课程ID") Integer courseId,
            @PathVariable @Parameter(description = "学生ID") Integer studentId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResultVO.success("获取学生出勤记录成功", attendanceService.getStudentAttendance(courseId, studentId, userDetails));
    }

    @GetMapping("/course/{courseId}/dates")
    @Operation(summary = "获取课程出勤日期列表", description = "分页获取课程的所有出勤日期列表")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "404", description = "课程不存在",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class)))
    })
    @PreAuthorize("hasAnyRole('TEACHER', 'SYSADMIN', 'EN_TEACHER')")
    public ResultVO<IPage<LocalDate>> getCourseAttendanceDates(
            @PathVariable @Parameter(description = "课程ID") Integer courseId,
            @RequestParam(defaultValue = "1") @Parameter(description = "页码") Integer page,
            @RequestParam(defaultValue = "10") @Parameter(description = "每页数量") Integer size,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResultVO.success("获取出勤日期列表成功", attendanceService.getCourseAttendanceDates(courseId, page, size, userDetails));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新出勤记录", description = "更新指定ID的出勤记录，需要教师或管理员权限")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "更新成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "403", description = "无权限",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "404", description = "出勤记录不存在",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class)))
    })
    @PreAuthorize("hasAnyRole('TEACHER', 'SYSADMIN')")
    public ResultVO<CourseAttendanceVO> updateAttendance(
            @PathVariable @Parameter(description = "出勤记录ID") Integer id,
            @RequestParam @Parameter(description = "出勤状态") String status,
            @RequestParam(required = false) @Parameter(description = "备注") String remark,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResultVO.success("更新出勤记录成功", attendanceService.updateAttendance(id, status, remark, userDetails));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除出勤记录", description = "删除指定ID的出勤记录，需要教师或管理员权限")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "删除成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "403", description = "无权限",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "404", description = "出勤记录不存在",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class)))
    })
    @PreAuthorize("hasAnyRole('TEACHER', 'SYSADMIN')")
    public ResultVO<Boolean> deleteAttendance(
            @PathVariable @Parameter(description = "出勤记录ID") Integer id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResultVO.success("删除出勤记录成功", attendanceService.deleteAttendance(id, userDetails));
    }

    @GetMapping("/course/{courseId}/attendance-rate")
    @Operation(summary = "获取课程出勤率", description = "获取课程的整体出勤率")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "404", description = "课程不存在",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<Integer> getCourseAttendanceRate(
            @PathVariable @Parameter(description = "课程ID") Integer courseId) {
        return ResultVO.success("获取课程出勤率成功", attendanceService.calculateCourseAttendanceRate(courseId));
    }

    @GetMapping("/course/{courseId}/student/{studentId}/attendance-rate")
    @Operation(summary = "获取学生出勤率", description = "获取指定学生在课程中的出勤率")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "404", description = "课程或学生不存在",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<Integer> getStudentAttendanceRate(
            @PathVariable @Parameter(description = "课程ID") Integer courseId,
            @PathVariable @Parameter(description = "学生ID") Integer studentId) {
        return ResultVO.success("获取学生出勤率成功", attendanceService.calculateStudentAttendanceRate(courseId, studentId));
    }

    @GetMapping("/course/{courseId}/statistics")
    @Operation(summary = "获取课程出勤统计", description = "获取课程的出勤统计数据")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "404", description = "课程不存在",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class)))
    })
    @PreAuthorize("hasAnyRole('TEACHER', 'SYSADMIN', 'EN_TEACHER')")
    public ResultVO<Map<String, Object>> getAttendanceStatistics(
            @PathVariable @Parameter(description = "课程ID") Integer courseId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResultVO.success("获取出勤统计数据成功", attendanceService.getAttendanceStatistics(courseId, userDetails));
    }
} 