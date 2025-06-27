package com.csu.unicorp.vo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 分页结果视图对象
 * @param <T> 列表项类型
 */
@Data
@Schema(description = "分页结果视图对象")
public class PageResultVO<T> {
    
    @Schema(description = "总记录数", example = "100")
    private Long total;
    
    @Schema(description = "总页数", example = "10")
    private Long pages;
    
    @Schema(description = "当前页码", example = "1")
    private Long current;
    
    @Schema(description = "每页大小", example = "10")
    private Long size;
    
    @Schema(description = "数据列表")
    private List<T> list;
    
    /**
     * 构造方法
     * @param total 总记录数
     * @param pages 总页数
     * @param current 当前页码
     * @param size 每页大小
     * @param list 数据列表
     */
    public PageResultVO(Long total, Long pages, Long current, Long size, List<T> list) {
        this.total = total;
        this.pages = pages;
        this.current = current;
        this.size = size;
        this.list = list;
    }
    
    /**
     * 基于MyBatis-Plus的IPage构造方法
     * @param page MyBatis-Plus分页对象
     */
    public PageResultVO(IPage<T> page) {
        this.total = page.getTotal();
        this.pages = page.getPages();
        this.current = page.getCurrent();
        this.size = page.getSize();
        this.list = page.getRecords();
    }
    
    /**
     * 空构造方法
     */
    public PageResultVO() {
    }
} 