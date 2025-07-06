package com.csu.unicorp.service.impl.community;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.csu.unicorp.common.constants.CacheConstants;
import com.csu.unicorp.entity.community.CommunityContentTag;
import com.csu.unicorp.entity.community.CommunityTag;
import com.csu.unicorp.mapper.community.CommunityContentTagMapper;
import com.csu.unicorp.mapper.community.CommunityTagMapper;
import com.csu.unicorp.service.CacheService;
import com.csu.unicorp.service.CommunityTagService;
import com.csu.unicorp.vo.community.TagVO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 社区标签Service实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CommunityTagServiceImpl extends ServiceImpl<CommunityTagMapper, CommunityTag>
        implements CommunityTagService {
    
    private final CommunityTagMapper tagMapper;
    private final CommunityContentTagMapper contentTagMapper;
    private final CacheService cacheService;
    
    @Override
    @Transactional
    public Long createTag(String name, String description) {
        CommunityTag tag = new CommunityTag();
        tag.setName(name);
        tag.setDescription(description);
        tag.setUsageCount(0);
        tag.setCreatedAt(LocalDateTime.now());
        tag.setUpdatedAt(LocalDateTime.now());
        
        save(tag);
        
        // 清除热门标签缓存
        cacheService.delete(CacheConstants.HOT_TAGS_CACHE_KEY);
        
        return tag.getId();
    }
    
    @Override
    @Transactional
    public boolean updateTag(Long tagId, String name, String description) {
        CommunityTag tag = getById(tagId);
        if (tag == null) {
            return false;
        }
        
        tag.setName(name);
        tag.setDescription(description);
        tag.setUpdatedAt(LocalDateTime.now());
        
        boolean result = updateById(tag);
        
        if (result) {
            // 清除相关缓存
            cacheService.delete(CacheConstants.TAG_DETAIL_CACHE_KEY_PREFIX + tagId);
            cacheService.delete(CacheConstants.HOT_TAGS_CACHE_KEY);
        }
        
        return result;
    }
    
    @Override
    @Transactional
    public boolean deleteTag(Long tagId) {
        boolean result = removeById(tagId);
        
        if (result) {
            // 清除相关缓存
            cacheService.delete(CacheConstants.TAG_DETAIL_CACHE_KEY_PREFIX + tagId);
            cacheService.delete(CacheConstants.HOT_TAGS_CACHE_KEY);
        }
        
        return result;
    }
    
    @Override
    public TagVO getTagDetail(Long tagId) {
        // 尝试从缓存获取
        String cacheKey = CacheConstants.TAG_DETAIL_CACHE_KEY_PREFIX + tagId;
        TagVO cachedTag = cacheService.get(cacheKey, TagVO.class);
        if (cachedTag != null) {
            log.debug("从缓存获取标签详情: {}", tagId);
            return cachedTag;
        }
        
        CommunityTag tag = getById(tagId);
        if (tag == null) {
            return null;
        }
        
        TagVO tagVO = convertToTagVO(tag);
        
        // 缓存标签详情
        cacheService.set(cacheKey, tagVO, CacheConstants.TAG_CACHE_EXPIRE_TIME, TimeUnit.SECONDS);
        
        return tagVO;
    }
    
    @Override
    public List<TagVO> getHotTags(int limit) {
        // 尝试从缓存获取
        String cacheKey = CacheConstants.HOT_TAGS_CACHE_KEY + ":" + limit;
        List<TagVO> cachedTags = cacheService.getList(cacheKey, TagVO.class);
        if (cachedTags != null && !cachedTags.isEmpty()) {
            log.debug("从缓存获取热门标签列表");
            return cachedTags;
        }
        
        LambdaQueryWrapper<CommunityTag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(CommunityTag::getUsageCount)
                   .last("LIMIT " + limit);
        
        List<CommunityTag> tags = list(queryWrapper);
        List<TagVO> result = tags.stream().map(this::convertToTagVO).collect(Collectors.toList());
        
        // 缓存热门标签
        cacheService.setList(cacheKey, result, CacheConstants.HOT_TAGS_CACHE_EXPIRE_TIME, TimeUnit.SECONDS);
        
        return result;
    }
    
    @Override
    public Page<TagVO> getAllTags(int page, int size) {
        // 对于分页数据，只缓存第一页
        if (page == 1) {
            String cacheKey = CacheConstants.ALL_TAGS_CACHE_KEY + ":" + size;
            Page<TagVO> cachedPage = cacheService.get(cacheKey, Page.class);
            if (cachedPage != null) {
                log.debug("从缓存获取标签分页列表");
                return cachedPage;
            }
        }
        
        Page<CommunityTag> tagPage = new Page<>(page, size);
        Page<CommunityTag> resultPage = page(tagPage, new LambdaQueryWrapper<CommunityTag>()
                .orderByDesc(CommunityTag::getUsageCount));
        
        Page<TagVO> voPage = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        voPage.setRecords(resultPage.getRecords().stream()
                .map(this::convertToTagVO)
                .collect(Collectors.toList()));
        
        // 缓存第一页数据
        if (page == 1) {
            String cacheKey = CacheConstants.ALL_TAGS_CACHE_KEY + ":" + size;
            cacheService.set(cacheKey, voPage, CacheConstants.TAG_CACHE_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        
        return voPage;
    }
    
    @Override
    public List<TagVO> getTagsByContent(String contentType, Long contentId) {
        // 尝试从缓存获取
        String cacheKey = CacheConstants.CONTENT_TAGS_CACHE_KEY_PREFIX + contentType + ":" + contentId;
        List<TagVO> cachedTags = cacheService.getList(cacheKey, TagVO.class);
        if (cachedTags != null) {
            log.debug("从缓存获取内容标签: {}:{}", contentType, contentId);
            return cachedTags;
        }
        
        // 获取内容关联的标签ID列表
        LambdaQueryWrapper<CommunityContentTag> contentTagQuery = new LambdaQueryWrapper<>();
        contentTagQuery.eq(CommunityContentTag::getContentType, contentType)
                      .eq(CommunityContentTag::getContentId, contentId);
        
        List<CommunityContentTag> contentTags = contentTagMapper.selectList(contentTagQuery);
        if (contentTags.isEmpty()) {
            return Collections.emptyList();
        }
        
        // 获取标签信息
        List<Long> tagIds = contentTags.stream()
                .map(CommunityContentTag::getTagId)
                .collect(Collectors.toList());
        
        LambdaQueryWrapper<CommunityTag> tagQuery = new LambdaQueryWrapper<>();
        tagQuery.in(CommunityTag::getId, tagIds);
        
        List<CommunityTag> tags = list(tagQuery);
        List<TagVO> result = tags.stream().map(this::convertToTagVO).collect(Collectors.toList());
        
        // 缓存结果
        cacheService.setList(cacheKey, result, CacheConstants.TAG_CACHE_EXPIRE_TIME, TimeUnit.SECONDS);
        
        return result;
    }
    
    @Override
    @Transactional
    public boolean addTagsToContent(String contentType, Long contentId, List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return true;
        }
        
        List<CommunityContentTag> contentTags = new ArrayList<>();
        for (Long tagId : tagIds) {
            if (existsById(tagId) && !checkContentHasTag(contentType, contentId, tagId)) {
                CommunityContentTag contentTag = new CommunityContentTag();
                contentTag.setContentType(contentType);
                contentTag.setContentId(contentId);
                contentTag.setTagId(tagId);
                contentTag.setCreatedAt(LocalDateTime.now());
                contentTags.add(contentTag);
                
                // 更新标签使用计数
                incrementTagUsageCount(tagId);
            }
        }
        
        if (!contentTags.isEmpty()) {
            for (CommunityContentTag contentTag : contentTags) {
                contentTagMapper.insert(contentTag);
            }
            
            // 清除内容标签缓存
            cacheService.delete(CacheConstants.CONTENT_TAGS_CACHE_KEY_PREFIX + contentType + ":" + contentId);
            // 清除热门标签缓存
            cacheService.delete(CacheConstants.HOT_TAGS_CACHE_KEY);
        }
        
        return true;
    }
    
    @Override
    @Transactional
    public boolean removeTagFromContent(String contentType, Long contentId, Long tagId) {
        LambdaQueryWrapper<CommunityContentTag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CommunityContentTag::getContentType, contentType)
                   .eq(CommunityContentTag::getContentId, contentId)
                   .eq(CommunityContentTag::getTagId, tagId);
        
        int result = contentTagMapper.delete(queryWrapper);
        if (result > 0) {
            // 减少标签使用计数
            decrementTagUsageCount(tagId);
            
            // 清除内容标签缓存
            cacheService.delete(CacheConstants.CONTENT_TAGS_CACHE_KEY_PREFIX + contentType + ":" + contentId);
            // 清除热门标签缓存
            cacheService.delete(CacheConstants.HOT_TAGS_CACHE_KEY);
        }
        
        return result > 0;
    }
    
    @Override
    @Transactional
    public boolean updateContentTags(String contentType, Long contentId, List<Long> tagIds) {
        // 先删除所有关联
        LambdaQueryWrapper<CommunityContentTag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CommunityContentTag::getContentType, contentType)
                   .eq(CommunityContentTag::getContentId, contentId);
        
        contentTagMapper.delete(queryWrapper);
        
        // 清除内容标签缓存
        cacheService.delete(CacheConstants.CONTENT_TAGS_CACHE_KEY_PREFIX + contentType + ":" + contentId);
        
        // 重新添加关联
        return addTagsToContent(contentType, contentId, tagIds);
    }
    
    @Override
    public List<TagVO> searchTags(String keyword) {
        return searchTags(keyword, 10); // 默认限制10个结果
    }
    
    @Override
    public List<TagVO> searchTags(String keyword, Integer limit) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Collections.emptyList();
        }
        
        // 搜索结果不缓存，因为关键词变化较多
        LambdaQueryWrapper<CommunityTag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(CommunityTag::getName, keyword)
                   .orderByDesc(CommunityTag::getUsageCount)
                   .last("LIMIT " + limit);
        
        List<CommunityTag> tags = list(queryWrapper);
        return tags.stream().map(this::convertToTagVO).collect(Collectors.toList());
    }
    
    @Override
    public List<Long> getContentIdsByTag(String contentType, Long tagId) {
        // 尝试从缓存获取
        String cacheKey = CacheConstants.TAG_CONTENT_IDS_CACHE_KEY_PREFIX + contentType + ":" + tagId;
        List<Long> cachedIds = cacheService.getList(cacheKey, Long.class);
        if (cachedIds != null) {
            log.debug("从缓存获取标签内容ID列表: {}:{}", contentType, tagId);
            return cachedIds;
        }
        
        LambdaQueryWrapper<CommunityContentTag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CommunityContentTag::getContentType, contentType)
                   .eq(CommunityContentTag::getTagId, tagId);
        
        List<CommunityContentTag> contentTags = contentTagMapper.selectList(queryWrapper);
        List<Long> result = contentTags.stream()
                .map(CommunityContentTag::getContentId)
                .collect(Collectors.toList());
        
        // 缓存结果
        cacheService.setList(cacheKey, result, CacheConstants.TAG_CACHE_EXPIRE_TIME, TimeUnit.SECONDS);
        
        return result;
    }
    
    @Override
    public boolean checkContentHasTag(String contentType, Long contentId, Long tagId) {
        LambdaQueryWrapper<CommunityContentTag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CommunityContentTag::getContentType, contentType)
                   .eq(CommunityContentTag::getContentId, contentId)
                   .eq(CommunityContentTag::getTagId, tagId);
        
        return contentTagMapper.selectCount(queryWrapper) > 0;
    }
    
    @Override
    public boolean existsByName(String name) {
        return count(new LambdaQueryWrapper<CommunityTag>()
                .eq(CommunityTag::getName, name)) > 0;
    }
    
    @Override
    public boolean existsById(Long tagId) {
        // 先检查缓存
        String cacheKey = CacheConstants.TAG_DETAIL_CACHE_KEY_PREFIX + tagId;
        TagVO cachedTag = cacheService.get(cacheKey, TagVO.class);
        if (cachedTag != null) {
            return true;
        }
        
        return getById(tagId) != null;
    }
    
    @Override
    public boolean existsByNameExcludeId(String name, Long tagId) {
        return count(new LambdaQueryWrapper<CommunityTag>()
                .eq(CommunityTag::getName, name)
                .ne(CommunityTag::getId, tagId)) > 0;
    }
    
    @Override
    public Page<TagVO> getTags(Integer page, Integer size) {
        return getAllTags(page, size);
    }
    
    @Override
    public List<TagVO> getTopicTags(Long topicId) {
        return getTagsByContent("TOPIC", topicId);
    }
    
    @Override
    public List<TagVO> getQuestionTags(Long questionId) {
        return getTagsByContent("QUESTION", questionId);
    }
    
    /**
     * 转换标签实体为标签VO
     * @param tag 标签实体
     * @return 标签VO
     */
    private TagVO convertToTagVO(CommunityTag tag) {
        if (tag == null) {
            return null;
        }
        
        TagVO tagVO = new TagVO();
        tagVO.setId(tag.getId());
        tagVO.setName(tag.getName());
        tagVO.setDescription(tag.getDescription());
        tagVO.setUsageCount(tag.getUsageCount());
        tagVO.setCreatedAt(tag.getCreatedAt());
        
        return tagVO;
    }
    
    /**
     * 增加标签使用计数
     * @param tagId 标签ID
     */
    private void incrementTagUsageCount(Long tagId) {
        CommunityTag tag = getById(tagId);
        if (tag != null) {
            tag.setUsageCount(tag.getUsageCount() + 1);
            updateById(tag);
            
            // 清除标签缓存
            cacheService.delete(CacheConstants.TAG_DETAIL_CACHE_KEY_PREFIX + tagId);
            cacheService.delete(CacheConstants.HOT_TAGS_CACHE_KEY);
        }
    }
    
    /**
     * 减少标签使用计数
     * @param tagId 标签ID
     */
    private void decrementTagUsageCount(Long tagId) {
        CommunityTag tag = getById(tagId);
        if (tag != null && tag.getUsageCount() > 0) {
            tag.setUsageCount(tag.getUsageCount() - 1);
            updateById(tag);
            
            // 清除标签缓存
            cacheService.delete(CacheConstants.TAG_DETAIL_CACHE_KEY_PREFIX + tagId);
            cacheService.delete(CacheConstants.HOT_TAGS_CACHE_KEY);
        }
    }
} 