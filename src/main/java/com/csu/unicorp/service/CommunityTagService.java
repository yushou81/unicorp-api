package com.csu.unicorp.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.csu.unicorp.entity.community.CommunityTag;
import com.csu.unicorp.vo.community.TagVO;

/**
 * 社区标签Service接口
 */
public interface CommunityTagService extends IService<CommunityTag> {
    
    /**
     * 创建标签
     * @param name 标签名称
     * @param description 标签描述
     * @return 标签ID
     */
    Long createTag(String name, String description);
    
    /**
     * 更新标签
     * @param tagId 标签ID
     * @param name 标签名称
     * @param description 标签描述
     * @return 是否成功
     */
    boolean updateTag(Long tagId, String name, String description);
    
    /**
     * 删除标签
     * @param tagId 标签ID
     * @return 是否成功
     */
    boolean deleteTag(Long tagId);
    
    /**
     * 获取标签详情
     * @param tagId 标签ID
     * @return 标签详情
     */
    TagVO getTagDetail(Long tagId);
    
    /**
     * 获取热门标签列表
     * @param limit 限制数量
     * @return 热门标签列表
     */
    List<TagVO> getHotTags(int limit);
    
    /**
     * 获取所有标签（分页）
     * @param page 页码
     * @param size 每页大小
     * @return 标签列表
     */
    Page<TagVO> getAllTags(int page, int size);
    
    /**
     * 根据内容获取标签列表
     * @param contentType 内容类型
     * @param contentId 内容ID
     * @return 标签列表
     */
    List<TagVO> getTagsByContent(String contentType, Long contentId);
    
    /**
     * 为内容添加标签
     * @param contentType 内容类型
     * @param contentId 内容ID
     * @param tagIds 标签ID列表
     * @return 是否成功
     */
    boolean addTagsToContent(String contentType, Long contentId, List<Long> tagIds);
    
    /**
     * 从内容中移除标签
     * @param contentType 内容类型
     * @param contentId 内容ID
     * @param tagId 标签ID
     * @return 是否成功
     */
    boolean removeTagFromContent(String contentType, Long contentId, Long tagId);
    
    /**
     * 更新内容的标签
     * @param contentType 内容类型
     * @param contentId 内容ID
     * @param tagIds 标签ID列表
     * @return 是否成功
     */
    boolean updateContentTags(String contentType, Long contentId, List<Long> tagIds);
    
    /**
     * 搜索标签
     * @param keyword 关键词
     * @return 标签列表
     */
    List<TagVO> searchTags(String keyword);
    
    /**
     * 搜索标签
     * @param keyword 关键词
     * @param limit 限制数量
     * @return 标签列表
     */
    List<TagVO> searchTags(String keyword, Integer limit);
    
    /**
     * 根据标签获取内容ID列表
     * @param contentType 内容类型
     * @param tagId 标签ID
     * @return 内容ID列表
     */
    List<Long> getContentIdsByTag(String contentType, Long tagId);
    
    /**
     * 检查内容是否包含指定标签
     * @param contentType 内容类型
     * @param contentId 内容ID
     * @param tagId 标签ID
     * @return 是否包含
     */
    boolean checkContentHasTag(String contentType, Long contentId, Long tagId);
    
    /**
     * 检查标签名是否已存在
     * @param name 标签名
     * @return 是否存在
     */
    boolean existsByName(String name);
    
    /**
     * 检查标签ID是否存在
     * @param tagId 标签ID
     * @return 是否存在
     */
    boolean existsById(Long tagId);
    
    /**
     * 检查标签名是否已被其他标签使用
     * @param name 标签名
     * @param tagId 当前标签ID
     * @return 是否被其他标签使用
     */
    boolean existsByNameExcludeId(String name, Long tagId);
    
    /**
     * 获取标签列表
     * @param page 页码
     * @param size 每页大小
     * @return 标签列表
     */
    Page<TagVO> getTags(Integer page, Integer size);
    
    /**
     * 获取话题的标签列表
     * @param topicId 话题ID
     * @return 标签列表
     */
    List<TagVO> getTopicTags(Long topicId);
    
    /**
     * 获取问题的标签列表
     * @param questionId 问题ID
     * @return 标签列表
     */
    List<TagVO> getQuestionTags(Long questionId);
} 