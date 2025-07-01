package com.csu.unicorp.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.csu.unicorp.dto.BookingCreationDTO;
import com.csu.unicorp.dto.BookingReviewDTO;
import com.csu.unicorp.dto.EquipmentCreationDTO;
import com.csu.unicorp.service.EquipmentService;
import com.csu.unicorp.vo.BookingVO;
import com.csu.unicorp.vo.EquipmentVO;
import com.csu.unicorp.vo.ResultVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 实验设备控制器
 */
@Slf4j
@Tag(name = "Equipment", description = "实验设备管理")
@RestController
@RequestMapping("/v1/equipments")
@RequiredArgsConstructor
public class EquipmentController {
    
    private final EquipmentService equipmentService;
    
    /**
     * 获取设备列表
     */
    @Operation(summary = "获取设备列表", description = "获取实验设备列表，支持分页和搜索")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取设备列表", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @GetMapping
    public ResultVO<IPage<EquipmentVO>> getEquipments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer organizationId,
            @RequestParam(required = false) String status) {
        
        IPage<EquipmentVO> equipments = equipmentService.getEquipments(page, size, keyword, organizationId, status);
        return ResultVO.success("获取设备列表成功", equipments);
    }
    
    /**
     * 获取设备详情
     */
    @Operation(summary = "获取设备详情", description = "根据ID获取设备详情")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取设备详情", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = EquipmentVO.class))),
        @ApiResponse(responseCode = "404", description = "设备不存在")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ResultVO<EquipmentVO>> getEquipmentById(@PathVariable Integer id) {
        EquipmentVO equipment = equipmentService.getEquipmentById(id);
        return ResponseEntity.ok(ResultVO.success("获取设备详情成功", equipment));
    }
    
    /**
     * 创建设备
     */
    @Operation(summary = "[管理员/教师] 创建设备", description = "创建新的实验设备")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "设备创建成功", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = EquipmentVO.class))),
        @ApiResponse(responseCode = "403", description = "权限不足")
    })
    @PostMapping
    public ResponseEntity<ResultVO<EquipmentVO>> createEquipment(
            @Valid @RequestBody EquipmentCreationDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        EquipmentVO equipment = equipmentService.createEquipment(dto, userDetails);
        return new ResponseEntity<>(ResultVO.success("设备创建成功", equipment), HttpStatus.CREATED);
    }
    
    /**
     * 更新设备
     */
    @Operation(summary = "[设备管理员] 更新设备信息", description = "更新设备信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "设备更新成功", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = EquipmentVO.class))),
        @ApiResponse(responseCode = "403", description = "权限不足"),
        @ApiResponse(responseCode = "404", description = "设备不存在")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ResultVO<EquipmentVO>> updateEquipment(
            @PathVariable Integer id,
            @Valid @RequestBody EquipmentCreationDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        EquipmentVO equipment = equipmentService.updateEquipment(id, dto, userDetails);
        return ResponseEntity.ok(ResultVO.success("设备更新成功", equipment));
    }
    
    /**
     * 删除设备
     */
    @Operation(summary = "[设备管理员] 删除设备", description = "删除设备")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "设备删除成功"),
        @ApiResponse(responseCode = "403", description = "权限不足"),
        @ApiResponse(responseCode = "404", description = "设备不存在")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEquipment(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        equipmentService.deleteEquipment(id, userDetails);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * 申请使用设备
     */
    @Operation(summary = "[登录用户] 申请使用设备", description = "创建设备使用申请")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "申请创建成功", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = BookingVO.class))),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "403", description = "权限不足")
    })
    @PostMapping("/bookings")
    public ResponseEntity<ResultVO<BookingVO>> createBooking(
            @Valid @RequestBody BookingCreationDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        BookingVO booking = equipmentService.createBooking(dto, userDetails);
        return new ResponseEntity<>(ResultVO.success("设备预约申请成功", booking), HttpStatus.CREATED);
    }
    
    /**
     * 获取预约列表
     */
    @Operation(summary = "[管理员/教师] 获取预约列表", description = "获取设备预约列表，支持分页和搜索")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取预约列表", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @GetMapping("/bookings")
    public ResultVO<IPage<BookingVO>> getBookings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) Integer equipmentId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer organizationId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        IPage<BookingVO> bookings = equipmentService.getBookings(page, size, userId, equipmentId, status, organizationId, userDetails);
        return ResultVO.success("获取预约列表成功", bookings);
    }
    
    /**
     * 获取预约详情
     */
    @Operation(summary = "[预约者/设备管理员] 获取预约详情", description = "根据ID获取预约详情")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取预约详情", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = BookingVO.class))),
        @ApiResponse(responseCode = "403", description = "权限不足"),
        @ApiResponse(responseCode = "404", description = "预约不存在")
    })
    @GetMapping("/bookings/{id}")
    public ResponseEntity<ResultVO<BookingVO>> getBookingById(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        BookingVO booking = equipmentService.getBookingById(id, userDetails);
        return ResponseEntity.ok(ResultVO.success("获取预约详情成功", booking));
    }
    
    /**
     * 取消预约
     */
    @Operation(summary = "[预约者] 取消预约", description = "取消自己的预约")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "预约取消成功", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = BookingVO.class))),
        @ApiResponse(responseCode = "403", description = "权限不足"),
        @ApiResponse(responseCode = "404", description = "预约不存在")
    })
    @PostMapping("/bookings/{id}/cancel")
    public ResponseEntity<ResultVO<BookingVO>> cancelBooking(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        BookingVO booking = equipmentService.cancelBooking(id, userDetails);
        return ResponseEntity.ok(ResultVO.success("预约取消成功", booking));
    }
    
    /**
     * 审核预约
     */
    @Operation(summary = "[设备管理员] 审核预约", description = "批准或拒绝预约申请")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "预约审核完成", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = BookingVO.class))),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "403", description = "权限不足"),
        @ApiResponse(responseCode = "404", description = "预约不存在")
    })
    @PostMapping("/bookings/review")
    public ResponseEntity<ResultVO<BookingVO>> reviewBooking(
            @Valid @RequestBody BookingReviewDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        BookingVO booking = equipmentService.reviewBooking(dto, userDetails);
        return ResponseEntity.ok(ResultVO.success("预约审核完成", booking));
    }
} 