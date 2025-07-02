package com.csu.unicorp.service.impl;

import java.time.LocalDateTime;
import java.util.List;

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
import com.csu.unicorp.dto.EquipmentCreationDTO;
import com.csu.unicorp.entity.EquipmentBooking;
import com.csu.unicorp.entity.EquipmentResource;
import com.csu.unicorp.entity.organization.Organization;
import com.csu.unicorp.entity.user.User;
import com.csu.unicorp.mapper.EquipmentBookingMapper;
import com.csu.unicorp.mapper.EquipmentResourceMapper;
import com.csu.unicorp.mapper.OrganizationMapper;
import com.csu.unicorp.mapper.UserMapper;
import com.csu.unicorp.service.EquipmentService;
import com.csu.unicorp.vo.BookingVO;
import com.csu.unicorp.vo.EquipmentVO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EquipmentServiceImpl implements EquipmentService {

    private final EquipmentResourceMapper equipmentMapper;
    private final EquipmentBookingMapper bookingMapper;
    private final UserMapper userMapper;
    private final OrganizationMapper organizationMapper;
    
    // 角色常量
    private static final String ROLE_ADMIN = "ROLE_" + RoleConstants.ROLE_SYSTEM_ADMIN;
    private static final String ROLE_SCHOOL_ADMIN = "ROLE_" + RoleConstants.ROLE_SCHOOL_ADMIN;
    private static final String ROLE_ENTERPRISE_ADMIN = "ROLE_" + RoleConstants.ROLE_ENTERPRISE_ADMIN;
    private static final String ROLE_TEACHER = "ROLE_" + RoleConstants.ROLE_TEACHER;
    private static final String ROLE_ENTERPRISE_MENTOR = "ROLE_" + RoleConstants.ROLE_ENTERPRISE_MENTOR;

    @Override
    @Transactional
    public EquipmentVO createEquipment(EquipmentCreationDTO dto, UserDetails userDetails) {
        // 获取用户ID
        Integer userId = getUserIdFromUserDetails(userDetails);
        
        // 检查用户权限
        if (!hasEquipmentManagementPermission(userDetails)) {
            throw new AccessDeniedException("只有教师或管理员可以创建设备");
        }
        
        // 创建设备实体
        EquipmentResource equipment = new EquipmentResource();
        BeanUtils.copyProperties(dto, equipment);
        equipment.setStatus("AVAILABLE");
        equipment.setManagerId(userId);
        equipment.setCreatedAt(LocalDateTime.now());
        equipment.setUpdatedAt(LocalDateTime.now());
        
        // 保存到数据库
        equipmentMapper.insert(equipment);
        
        // 返回VO
        return convertToVO(equipment);
    }

    @Override
    @Transactional
    public EquipmentVO updateEquipment(Integer id, EquipmentCreationDTO dto, UserDetails userDetails) {
        // 获取设备
        EquipmentResource equipment = getEquipmentEntityById(id);
        
        // 检查权限
        if (!isEquipmentManager(equipment, userDetails) && !isAdmin(userDetails)) {
            throw new AccessDeniedException("只有设备管理员或系统管理员才能更新设备信息");
        }
        
        // 更新属性
        BeanUtils.copyProperties(dto, equipment);
        equipment.setUpdatedAt(LocalDateTime.now());
        
        // 保存更新
        equipmentMapper.updateById(equipment);
        
        // 返回更新后的VO
        return convertToVO(equipment);
    }

    @Override
    @Transactional
    public void deleteEquipment(Integer id, UserDetails userDetails) {
        // 获取设备
        EquipmentResource equipment = getEquipmentEntityById(id);
        
        // 检查权限
        if (!isEquipmentManager(equipment, userDetails) && !isAdmin(userDetails)) {
            throw new AccessDeniedException("只有设备管理员或系统管理员才能删除设备");
        }
        
        // 检查是否有未完成的预约
        LambdaQueryWrapper<EquipmentBooking> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EquipmentBooking::getEquipmentId, id)
                   .in(EquipmentBooking::getStatus, "PENDING", "APPROVED")
                   .ge(EquipmentBooking::getEndTime, LocalDateTime.now());
        
        if (bookingMapper.selectCount(queryWrapper) > 0) {
            throw new BusinessException("该设备有未完成的预约，无法删除");
        }
        
        // 删除设备
        equipmentMapper.deleteById(id);
    }

    @Override
    public IPage<EquipmentVO> getEquipments(int page, int size, String keyword, Integer organizationId, String status) {
        // 创建分页对象
        Page<EquipmentResource> pageParam = new Page<>(page, size);
        
        // 执行查询
        IPage<EquipmentResource> result = equipmentMapper.findEquipmentResourcesPage(
                pageParam, keyword, organizationId, status);
        
        // 转换为VO
        return result.convert(this::convertToVO);
    }

    @Override
    public EquipmentVO getEquipmentById(Integer id) {
        EquipmentResource equipment = getEquipmentEntityById(id);
        return convertToVO(equipment);
    }

    @Override
    @Transactional
    public BookingVO createBooking(BookingCreationDTO dto, UserDetails userDetails) {
        // 获取用户ID
        Integer userId = getUserIdFromUserDetails(userDetails);
        
        // 验证设备存在
        EquipmentResource equipment = getEquipmentEntityById(dto.getEquipmentId());
        
        // 验证设备状态
        if (!"AVAILABLE".equals(equipment.getStatus())) {
            throw new BusinessException("设备当前不可预约");
        }
        
        // 验证预约时间
        if (dto.getEndTime().isBefore(dto.getStartTime())) {
            throw new BusinessException("结束时间不能早于开始时间");
        }
        
        if (dto.getStartTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("开始时间不能早于当前时间");
        }
        
        // 检查时间冲突
        checkTimeConflict(dto.getEquipmentId(), dto.getStartTime(), dto.getEndTime(), null);
        
        // 创建预约
        EquipmentBooking booking = new EquipmentBooking();
        booking.setEquipmentId(dto.getEquipmentId());
        booking.setUserId(userId);
        booking.setStartTime(dto.getStartTime());
        booking.setEndTime(dto.getEndTime());
        booking.setPurpose(dto.getPurpose());
        booking.setStatus("PENDING");  // 默认为待审核
        booking.setCreatedAt(LocalDateTime.now());
        booking.setUpdatedAt(LocalDateTime.now());
        
        // 保存到数据库
        bookingMapper.insert(booking);
        
        // 返回VO
        return convertToBookingVO(booking);
    }

    @Override
    public IPage<BookingVO> getBookings(int page, int size, Integer userId, Integer equipmentId, 
                                     String status, Integer organizationId, UserDetails userDetails) {
        // 创建分页对象
        Page<EquipmentBooking> pageParam = new Page<>(page, size);
        
        // 执行查询
        IPage<EquipmentBooking> result = bookingMapper.findBookingsPage(
                pageParam, userId, equipmentId, status, organizationId);
        
        // 转换为VO
        return result.convert(this::convertToBookingVO);
    }

    @Override
    public BookingVO getBookingById(Integer id, UserDetails userDetails) {
        EquipmentBooking booking = getBookingEntityById(id);
        
        // 权限检查：只有预约用户、设备管理员或管理员可以查看预约详情
        Integer currentUserId = getUserIdFromUserDetails(userDetails);
        if (!booking.getUserId().equals(currentUserId) && 
            !isEquipmentManagerById(booking.getEquipmentId(), userDetails) && 
            !isAdmin(userDetails)) {
            throw new AccessDeniedException("没有权限查看该预约");
        }
        
        return convertToBookingVO(booking);
    }

    @Override
    @Transactional
    public BookingVO cancelBooking(Integer id, UserDetails userDetails) {
        // 获取预约
        EquipmentBooking booking = getBookingEntityById(id);
        
        // 权限检查：只有预约用户可以取消自己的预约
        Integer currentUserId = getUserIdFromUserDetails(userDetails);
        if (!booking.getUserId().equals(currentUserId) && !isAdmin(userDetails)) {
            throw new AccessDeniedException("只有预约用户或管理员可以取消预约");
        }
        
        // 检查状态
        if ("CANCELED".equals(booking.getStatus()) || "COMPLETED".equals(booking.getStatus())) {
            throw new BusinessException("该预约已取消或已完成，无法操作");
        }
        
        // 取消预约
        booking.setStatus("CANCELED");
        booking.setUpdatedAt(LocalDateTime.now());
        bookingMapper.updateById(booking);
        
        return convertToBookingVO(booking);
    }

    @Override
    @Transactional
    public BookingVO reviewBooking(BookingReviewDTO dto, UserDetails userDetails) {
        // 获取预约
        EquipmentBooking booking = getBookingEntityById(dto.getBookingId());
        
        // 权限检查：只有设备管理员或管理员才能审核预约
        if (!isEquipmentManagerById(booking.getEquipmentId(), userDetails) && !isAdmin(userDetails)) {
            throw new AccessDeniedException("只有设备管理员或系统管理员才能审核预约");
        }
        
        // 检查状态
        if (!"PENDING".equals(booking.getStatus())) {
            throw new BusinessException("只能审核待审核状态的预约");
        }
        
        // 获取用户信息
        Integer reviewerId = getUserIdFromUserDetails(userDetails);
        
        // 如果是批准，检查时间冲突
        if ("APPROVED".equals(dto.getStatus())) {
            checkTimeConflict(booking.getEquipmentId(), booking.getStartTime(), booking.getEndTime(), booking.getId());
            booking.setStatus("APPROVED");
        } else if ("REJECTED".equals(dto.getStatus())) {
            if (dto.getRejectReason() == null || dto.getRejectReason().trim().isEmpty()) {
                throw new BusinessException("拒绝预约时必须提供原因");
            }
            booking.setStatus("REJECTED");
            booking.setRejectReason(dto.getRejectReason());
        } else {
            throw new BusinessException("无效的审核结果");
        }
        
        // 更新预约
        booking.setReviewerId(reviewerId);
        booking.setUpdatedAt(LocalDateTime.now());
        bookingMapper.updateById(booking);
        
        return convertToBookingVO(booking);
    }

    // 辅助方法：获取用户ID
    private Integer getUserIdFromUserDetails(UserDetails userDetails) {
        // 从Principal获取用户名
        String username = userDetails.getUsername();
        
        // 根据用户名查询用户
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
            .eq(User::getAccount, username)
            .eq(User::getIsDeleted, 0));
            
        if (user == null) {
            throw new ResourceNotFoundException("用户不存在");
        }
        
        return user.getId();
    }

    // 辅助方法：判断用户是否有设备管理权限
    private boolean hasEquipmentManagementPermission(UserDetails userDetails) {
        return hasAnyRole(userDetails, ROLE_ADMIN, ROLE_SCHOOL_ADMIN, 
                        ROLE_ENTERPRISE_ADMIN, ROLE_TEACHER, 
                        ROLE_ENTERPRISE_MENTOR);
    }

    // 辅助方法：判断用户是否是设备的管理员
    private boolean isEquipmentManager(EquipmentResource equipment, UserDetails userDetails) {
        Integer currentUserId = getUserIdFromUserDetails(userDetails);
        return equipment.getManagerId().equals(currentUserId);
    }
    
    // 辅助方法：判断用户是否是设备的管理员（通过设备ID）
    private boolean isEquipmentManagerById(Integer equipmentId, UserDetails userDetails) {
        EquipmentResource equipment = getEquipmentEntityById(equipmentId);
        return isEquipmentManager(equipment, userDetails);
    }
    
    // 辅助方法：判断用户是否是管理员
    private boolean isAdmin(UserDetails userDetails) {
        return hasAnyRole(userDetails, ROLE_ADMIN, ROLE_SCHOOL_ADMIN, 
                        ROLE_ENTERPRISE_ADMIN);
    }
    
    // 辅助方法：判断用户是否有任一指定角色
    private boolean hasAnyRole(UserDetails userDetails, String... roles) {
        for (String role : roles) {
            if (userDetails.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals(role))) {
                return true;
            }
        }
        return false;
    }

    // 辅助方法：检查时间冲突
    private void checkTimeConflict(Integer equipmentId, LocalDateTime startTime, LocalDateTime endTime, Integer excludeBookingId) {
        List<EquipmentBooking> conflictBookings = bookingMapper.findConflictBookings(
                equipmentId, startTime, endTime, excludeBookingId);
        
        if (!conflictBookings.isEmpty()) {
            throw new BusinessException("该时间段已有其他预约，请选择其他时间");
        }
    }

    // 辅助方法：通过ID获取设备实体
    private EquipmentResource getEquipmentEntityById(Integer id) {
        EquipmentResource equipment = equipmentMapper.selectById(id);
        if (equipment == null) {
            throw new ResourceNotFoundException("设备不存在");
        }
        return equipment;
    }
    
    // 辅助方法：通过ID获取预约实体
    private EquipmentBooking getBookingEntityById(Integer id) {
        EquipmentBooking booking = bookingMapper.selectById(id);
        if (booking == null) {
            throw new ResourceNotFoundException("预约不存在");
        }
        return booking;
    }
    
    // 辅助方法：将设备实体转换为VO
    private EquipmentVO convertToVO(EquipmentResource equipment) {
        if (equipment == null) {
            return null;
        }
        
        EquipmentVO vo = new EquipmentVO();
        BeanUtils.copyProperties(equipment, vo);
        
        // 填充管理员姓名
        User manager = userMapper.selectById(equipment.getManagerId());
        if (manager != null) {
            vo.setManagerName(manager.getNickname());
        }
        
        // 填充组织名称
        Organization org = organizationMapper.selectById(equipment.getOrganizationId());
        if (org != null) {
            vo.setOrganizationName(org.getOrganizationName());
        }
        
        return vo;
    }
    
    // 辅助方法：将预约实体转换为VO
    private BookingVO convertToBookingVO(EquipmentBooking booking) {
        if (booking == null) {
            return null;
        }
        
        BookingVO vo = new BookingVO();
        BeanUtils.copyProperties(booking, vo);
        
        // 填充用户名
        User user = userMapper.selectById(booking.getUserId());
        if (user != null) {
            vo.setUserName(user.getNickname());
        }
        
        // 填充设备名称
        EquipmentResource equipment = equipmentMapper.selectById(booking.getEquipmentId());
        if (equipment != null) {
            vo.setEquipmentName(equipment.getName());
        }
        
        // 填充审核人姓名
        if (booking.getReviewerId() != null) {
            User reviewer = userMapper.selectById(booking.getReviewerId());
            if (reviewer != null) {
                vo.setReviewerName(reviewer.getNickname());
            }
        }
        
        return vo;
    }
} 