package com.csu.unicorp.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.csu.unicorp.dto.BookingCreationDTO;
import com.csu.unicorp.dto.BookingReviewDTO;
import com.csu.unicorp.vo.BookingVO;
import com.csu.unicorp.vo.ResourceTimeSlotVO;

/**
 * 设备预约服务接口
 */
public interface EquipmentService {
    
    /**
     * 创建设备预约
     */
    BookingVO createBooking(BookingCreationDTO dto, UserDetails userDetails);
    
    /**
     * 获取预约列表
     */
    IPage<BookingVO> getBookings(int page, int size, Integer userId, Integer resourceId, 
            String status, Integer organizationId, UserDetails userDetails);
    
    /**
     * 获取预约详情
     */
    BookingVO getBookingById(Integer id, UserDetails userDetails);
    
    /**
     * 取消预约
     */
    BookingVO cancelBooking(Integer id, UserDetails userDetails);
    
    /**
     * 审核预约
     */
    BookingVO reviewBooking(BookingReviewDTO dto, UserDetails userDetails);
    
    /**
     * 获取当前用户的所有预约
     * 
     * @param userDetails 当前登录用户
     * @return 用户的所有预约列表
     */
    List<BookingVO> getCurrentUserBookings(UserDetails userDetails);
    
    /**
     * 获取资源的所有预约情况
     * 
     * @param resourceId 资源ID
     * @param userDetails 当前登录用户
     * @return 资源的所有预约列表（包含占用时间）
     */
    List<BookingVO> getResourceBookings(Integer resourceId, UserDetails userDetails);
    
    /**
     * 获取资源的占用时间段
     * 
     * @param resourceId 资源ID
     * @param userDetails 当前登录用户
     * @return 资源被占用的时间段列表
     */
    List<ResourceTimeSlotVO> getResourceTimeSlots(Integer resourceId, UserDetails userDetails);
} 