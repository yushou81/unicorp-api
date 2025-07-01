package com.csu.unicorp.service;

import org.springframework.security.core.userdetails.UserDetails;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.csu.unicorp.dto.BookingCreationDTO;
import com.csu.unicorp.dto.BookingReviewDTO;
import com.csu.unicorp.dto.EquipmentCreationDTO;
import com.csu.unicorp.vo.BookingVO;
import com.csu.unicorp.vo.EquipmentVO;

/**
 * 实验设备服务接口
 */
public interface EquipmentService {
    
    /**
     * 创建新的实验设备
     */
    EquipmentVO createEquipment(EquipmentCreationDTO dto, UserDetails userDetails);
    
    /**
     * 更新实验设备信息
     */
    EquipmentVO updateEquipment(Integer id, EquipmentCreationDTO dto, UserDetails userDetails);
    
    /**
     * 删除实验设备
     */
    void deleteEquipment(Integer id, UserDetails userDetails);
    
    /**
     * 获取实验设备列表
     */
    IPage<EquipmentVO> getEquipments(int page, int size, String keyword, Integer organizationId, String status);
    
    /**
     * 获取实验设备详情
     */
    EquipmentVO getEquipmentById(Integer id);
    
    /**
     * 创建设备预约
     */
    BookingVO createBooking(BookingCreationDTO dto, UserDetails userDetails);
    
    /**
     * 获取预约列表
     */
    IPage<BookingVO> getBookings(int page, int size, Integer userId, Integer equipmentId, 
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
} 