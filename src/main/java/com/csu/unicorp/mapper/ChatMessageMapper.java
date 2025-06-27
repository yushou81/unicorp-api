package com.csu.unicorp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csu.unicorp.entity.ChatMessage;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {
}
