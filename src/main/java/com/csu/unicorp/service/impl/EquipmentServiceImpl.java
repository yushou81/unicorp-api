package com.csu.unicorp.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.unicorp.common.constants.RoleConstants;
import com.csu.unicorp.common.exception.BusinessException;
import com.csu.unicorp.common.exception.ResourceNotFoundException;
import com.csu.unicorp.dto.BookingCreationDTO;
import com.csu.unicorp.dto.BookingReviewDTO;
import com.csu.unicorp.entity.EquipmentBooking;
import com.csu.unicorp.entity.organization.Organization;
import com.csu.unicorp.entity.Resource;
import com.csu.unicorp.entity.User;
import com.csu.unicorp.mapper.EquipmentBookingMapper;
import com.csu.unicorp.mapper.ResourceMapper;
import com.csu.unicorp.mapper.UserMapper;
import com.csu.unicorp.service.EquipmentService;
import com.csu.unicorp.vo.BookingVO;
import com.csu.unicorp.vo.ResourceTimeSlotVO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EquipmentServiceImpl implements EquipmentService {

    private final EquipmentBookingMapper bookingMapper;
    private final ResourceMapper resourceMapper;
    private final UserMapper userMapper;
    
    @Override
    @Transactional
    public BookingVO createBooking(BookingCreationDTO dto, UserDetails userDetails) {
        // 获取用户ID
        Integer userId = getUserIdFromUserDetails(userDetails);
        
        // 验证设备存在
        Resource resource = getResourceEntityById(dto.getResourceId());
        
        // 验证资源是设备类型
        if (!"实验设备".equals(resource.getResourceType())) {
            throw new BusinessException("预约的资源不是设备类型");
        }
        
        // 验证预约时间
        if (dto.getEndTime().isBefore(dto.getStartTime())) {
            throw new BusinessException("结束时间不能早于开始时间");
        }
        
        if (dto.getStartTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("开始时间不能早于当前时间");
        }
        
        // 检查时间冲突
        checkTimeConflict(dto.getResourceId(), dto.getStartTime(), dto.getEndTime(), null);
        
        // 创建预约
        EquipmentBooking booking = new EquipmentBooking();
        booking.setResourceId(dto.getResourceId());
        booking.setUserId(userId);
        booking.setStartTime(dto.getStartTime());
        booking.setEndTime(dto.getEndTime());
        booking.setPurpose(dto.getPurpose());
        booking.setStatus("PENDING");
        booking.setCreatedAt(LocalDateTime.now());
        booking.setUpdatedAt(LocalDateTime.now());
        
        // 保存预约
        bookingMapper.insert(booking);
        
        // 返回VO
        return convertBookingToVO(booking);
    }

    @Override
    public IPage<BookingVO> getBookings(int page, int size, Integer userId, Integer resourceId, 
            String status, Integer organizationId, UserDetails userDetails) {
        // 创建分页对象
        Page<EquipmentBooking> pageParam = new Page<>(page, size);
        
        // 校验权限（管理员或教师可以查看所有预约，学生只能查看自己的预约）
        Integer currentUserId = getUserIdFromUserDetails(userDetails);
        boolean isAdmin = hasEquipmentManagementPermission(userDetails);
        
        if (!isAdmin) {
            // 非管理员只能查看自己的预约
            userId = currentUserId;
        }
        
        // 执行查询
        IPage<EquipmentBooking> result = bookingMapper.findBookingsPage(
                pageParam, userId, resourceId, status, organizationId);
        
        // 转换为VO
        return result.convert(this::convertBookingToVO);
    }

    @Override
    public BookingVO getBookingById(Integer id, UserDetails userDetails) {
        EquipmentBooking booking = getBookingEntityById(id);
        
        // 检查权限（只有预约者、资源拥有者、管理员可以查看预约详情）
        Integer currentUserId = getUserIdFromUserDetails(userDetails);
        boolean isAdmin = hasEquipmentManagementPermission(userDetails);
        Resource resource = getResourceEntityById(booking.getResourceId());
        
        if (!isAdmin && !currentUserId.equals(booking.getUserId()) 
                && !currentUserId.equals(resource.getUploadedByUserId())) {
            throw new AccessDeniedException("没有权限查看此预约详情");
        }
        
        return convertBookingToVO(booking);
    }

    @Override
    @Transactional
    public BookingVO cancelBooking(Integer id, UserDetails userDetails) {
        EquipmentBooking booking = getBookingEntityById(id);
        Integer currentUserId = getUserIdFromUserDetails(userDetails);
        
        // 检查权限（只有预约者或管理员可以取消预约）
        boolean isAdmin = hasEquipmentManagementPermission(userDetails);
        
        if (!isAdmin && !currentUserId.equals(booking.getUserId())) {
            throw new AccessDeniedException("没有权限取消此预约");
        }
        
        // 检查状态
        if ("CANCELED".equals(booking.getStatus()) || "COMPLETED".equals(booking.getStatus())) {
            throw new BusinessException("已取消或已完成的预约不能再次取消");
        }
        
        // 更新状态
        booking.setStatus("CANCELED");
        booking.setUpdatedAt(LocalDateTime.now());
        bookingMapper.updateById(booking);
        
        return convertBookingToVO(booking);
    }

    @Override
    @Transactional
    public BookingVO reviewBooking(BookingReviewDTO dto, UserDetails userDetails) {
        EquipmentBooking booking = getBookingEntityById(dto.getBookingId());
        Integer currentUserId = getUserIdFromUserDetails(userDetails);
        
        // 检查权限（只有资源拥有者或管理员可以审核预约）
        boolean isAdmin = hasEquipmentManagementPermission(userDetails);
        Resource resource = getResourceEntityById(booking.getResourceId());
        
        if (!isAdmin && !currentUserId.equals(resource.getUploadedByUserId())) {
            throw new AccessDeniedException("没有权限审核此预约");
        }
        
        // 检查状态
        if (!"PENDING".equals(booking.getStatus())) {
            throw new BusinessException("只有待审核状态的预约可以被审核");
        }
        
        // 如果是批准，检查时间冲突
        if ("APPROVED".equals(dto.getApprove())) {
            checkTimeConflict(booking.getResourceId(), booking.getStartTime(), booking.getEndTime(), booking.getId());
            booking.setStatus("APPROVED");
        } else {
            booking.setStatus("REJECTED");
            booking.setRejectReason(dto.getRejectReason());
        }
        
        booking.setReviewerId(currentUserId);
        booking.setUpdatedAt(LocalDateTime.now());
        
        bookingMapper.updateById(booking);
        return convertBookingToVO(booking);
    }
    
    @Override
    public List<BookingVO> getCurrentUserBookings(UserDetails userDetails) {
        // 获取当前用户ID
        Integer userId = getUserIdFromUserDetails(userDetails);
        
        // 查询用户的所有预约
        LambdaQueryWrapper<EquipmentBooking> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EquipmentBooking::getUserId, userId)
                   .orderByDesc(EquipmentBooking::getCreatedAt);
        
        List<EquipmentBooking> bookings = bookingMapper.selectList(queryWrapper);
        
        // 转换为VO并返回
        return bookings.stream()
                .map(this::convertBookingToVO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<BookingVO> getResourceBookings(Integer resourceId, UserDetails userDetails) {
        // 检查资源是否存在
        Resource resource = getResourceEntityById(resourceId);
        
        // 检查资源类型是否为设备
        if (!"实验设备".equals(resource.getResourceType())) {
            throw new BusinessException("指定的资源不是设备类型");
        }
        
        // 获取当前用户ID
        Integer currentUserId = getUserIdFromUserDetails(userDetails);
        boolean isAdmin = hasEquipmentManagementPermission(userDetails);
        boolean isResourceOwner = currentUserId.equals(resource.getUploadedByUserId());
        
        // 查询资源的所有预约
        LambdaQueryWrapper<EquipmentBooking> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EquipmentBooking::getResourceId, resourceId);
        
        // 非管理员且非资源所有者只能查看已审批的预约
        if (!isAdmin && !isResourceOwner) {
            queryWrapper.and(wrapper -> wrapper
                    .eq(EquipmentBooking::getStatus, "APPROVED")
                    .or()
                    .eq(EquipmentBooking::getUserId, currentUserId));
        }
        
        queryWrapper.orderByAsc(EquipmentBooking::getStartTime);
        
        List<EquipmentBooking> bookings = bookingMapper.selectList(queryWrapper);
        
        // 转换为VO并返回
        return bookings.stream()
                .map(this::convertBookingToVO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<ResourceTimeSlotVO> getResourceTimeSlots(Integer resourceId, UserDetails userDetails) {
        // 检查资源是否存在
        Resource resource = getResourceEntityById(resourceId);
        
        // 检查资源类型是否为设备
        if (!"实验设备".equals(resource.getResourceType())) {
            throw new BusinessException("指定的资源不是设备类型");
        }
        
        // 获取当前用户ID
        Integer currentUserId = getUserIdFromUserDetails(userDetails);
        
        // 查询资源的所有预约
        LambdaQueryWrapper<EquipmentBooking> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EquipmentBooking::getResourceId, resourceId);
        
        // 只返回已批准的预约（真正占用时间的预约）和当前用户的所有预约
        queryWrapper.and(wrapper -> wrapper
                .eq(EquipmentBooking::getStatus, "APPROVED")
                );
        System.out.println("输出了这一行刚queryWrapper: " + queryWrapper);
        // 按开始时间升序排序
        queryWrapper.orderByAsc(EquipmentBooking::getStartTime);
        
        List<EquipmentBooking> bookings = bookingMapper.selectList(queryWrapper);
        
        // 转换为时间段VO
        return bookings.stream()
                .map(booking -> {
                    ResourceTimeSlotVO timeSlot = new ResourceTimeSlotVO();
                    timeSlot.setBookingId(booking.getId());
                    timeSlot.setStartTime(booking.getStartTime());
                    timeSlot.setEndTime(booking.getEndTime());
                    timeSlot.setStatus(booking.getStatus());
                    // 标记是否为被占用时间（只有APPROVED状态才是真正被占用）
                    timeSlot.setCurrentUser(booking.getUserId().equals(currentUserId));
                    timeSlot.setOccupied("APPROVED".equals(booking.getStatus()));
                    return timeSlot;
                })
                .collect(Collectors.toList());
    }
    
    // =========== 辅助方法 ===========
    
    /**
     * 检查用户是否有设备管理权限
     */
    private boolean hasEquipmentManagementPermission(UserDetails userDetails) {
        return userDetails.getAuthorities().stream()
                .anyMatch(auth -> {
                    String role = auth.getAuthority();
                    return role.equals("ROLE_" + RoleConstants.ROLE_SYSTEM_ADMIN) || 
                           role.equals("ROLE_" + RoleConstants.ROLE_SCHOOL_ADMIN) || 
                           role.equals("ROLE_" + RoleConstants.ROLE_TEACHER);
                });
    }
    
    /**
     * 从UserDetails获取用户ID
     */
    private Integer getUserIdFromUserDetails(UserDetails userDetails) {
        String username = userDetails.getUsername();
        User user = userMapper.selectByAccount(username);
        if (user == null) {
            throw new ResourceNotFoundException("用户不存在");
        }
        return user.getId();
    }
    
    private void checkTimeConflict(Integer resourceId, LocalDateTime startTime, LocalDateTime endTime, Integer excludeBookingId) {
        // 查询冲突的预约
        List<EquipmentBooking> conflictBookings = bookingMapper.findConflictBookings(
                resourceId, startTime, endTime, excludeBookingId);
        
        if (!conflictBookings.isEmpty()) {
            // 构造冲突信息
            StringBuilder conflictInfo = new StringBuilder("预约时间冲突：该时间段已有预约。冲突的预约：");
            for (int i = 0; i < conflictBookings.size(); i++) {
                EquipmentBooking conflict = conflictBookings.get(i);
                conflictInfo.append(String.format(
                        "预约ID=%d，时间=%s至%s", 
                        conflict.getId(), 
                        conflict.getStartTime(), 
                        conflict.getEndTime()));
                
                if (i < conflictBookings.size() - 1) {
                    conflictInfo.append("; ");
                }
            }
            
            throw new BusinessException(conflictInfo.toString());
        }
    }
    
    /**
     * 获取设备资源实体
     */
    private Resource getResourceEntityById(Integer id) {
        Resource resource = resourceMapper.selectById(id);
        if (resource == null) {
            throw new ResourceNotFoundException("设备资源不存在");
        }
        return resource;
    }
    
    /**
     * 获取预约实体
     */
    private EquipmentBooking getBookingEntityById(Integer id) {
        EquipmentBooking booking = bookingMapper.selectById(id);
        if (booking == null) {
            throw new ResourceNotFoundException("预约不存在");
        }
        return booking;
    }
    
    /**
     * 将预约实体转换为VO
     */
    private BookingVO convertBookingToVO(EquipmentBooking booking) {
        BookingVO vo = new BookingVO();
        BeanUtils.copyProperties(booking, vo);
        
        // 设置资源标题
        Resource resource = resourceMapper.selectById(booking.getResourceId());
        if (resource != null) {
            vo.setResourceTitle(resource.getTitle());
        }
        
        // 设置用户姓名
        User user = userMapper.selectById(booking.getUserId());
        if (user != null) {
            vo.setUserName(user.getNickname());
        }
        
        // 设置审核人姓名
        if (booking.getReviewerId() != null) {
            User reviewer = userMapper.selectById(booking.getReviewerId());
            if (reviewer != null) {
                vo.setReviewerName(reviewer.getNickname());
            }
        }
        
        return vo;
    }
} 