package com.csu.unicorp.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus 元数据处理器
 * 用于自动填充字段，如createTime和updateTime
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * 插入操作时自动填充
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.debug("开始执行插入填充...");
        
        // 设置创建时间和更新时间为当前时间
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        
        // 如果有isDeleted字段，默认设置为0（未删除）
        if (metaObject.hasSetter("isDeleted")) {
            this.strictInsertFill(metaObject, "isDeleted", Integer.class, 0);
        }
    }

    /**
     * 更新操作时自动填充
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.debug("开始执行更新填充...");
        
        // 更新时只需要设置更新时间
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }
} 