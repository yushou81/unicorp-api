package com.csu.unicorp.service.impl.achievement;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.unicorp.common.exception.BusinessException;
import com.csu.unicorp.dto.achievement.PortfolioItemCreationDTO;
import com.csu.unicorp.dto.achievement.PortfolioResourceUploadDTO;
import com.csu.unicorp.entity.User;
import com.csu.unicorp.entity.achievement.AchievementView;
import com.csu.unicorp.entity.achievement.PortfolioItem;
import com.csu.unicorp.entity.achievement.PortfolioResource;
import com.csu.unicorp.mapper.UserMapper;
import com.csu.unicorp.mapper.achievement.AchievementViewMapper;
import com.csu.unicorp.mapper.achievement.PortfolioItemMapper;
import com.csu.unicorp.mapper.achievement.PortfolioResourceMapper;
import com.csu.unicorp.service.FileService;
import com.csu.unicorp.service.PortfolioService;
import com.csu.unicorp.vo.achievement.PortfolioItemVO;
import com.csu.unicorp.vo.achievement.PortfolioResourceVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 作品集Service实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PortfolioServiceImpl implements PortfolioService {

    private final PortfolioItemMapper portfolioItemMapper;
    private final PortfolioResourceMapper portfolioResourceMapper;
    private final UserMapper userMapper;
    private final AchievementViewMapper achievementViewMapper;
    private final FileService fileService;
    
    @Override
    public List<PortfolioItemVO> getPortfolioItems(Integer userId) {
        List<PortfolioItem> portfolioItems = portfolioItemMapper.selectByUserId(userId);
        return portfolioItems.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }
    
    @Override
    public Page<PortfolioItemVO> getPortfolioItemPage(Integer userId, Integer page, Integer size) {
        Page<PortfolioItem> portfolioItemPage = new Page<>(page, size);
        portfolioItemPage = portfolioItemMapper.selectPageByUserId(portfolioItemPage, userId);
        
        return convertPageToVO(portfolioItemPage);
    }
    
    @Override
    public Page<PortfolioItemVO> getPublicPortfolioItemPage(Integer page, Integer size) {
        Page<PortfolioItem> portfolioItemPage = new Page<>(page, size);
        portfolioItemPage = portfolioItemMapper.selectPublicPage(portfolioItemPage);
        
        return convertPageToVO(portfolioItemPage);
    }
    
    @Override
    public Page<PortfolioItemVO> getPublicPortfolioItemPageByCategory(String category, Integer page, Integer size) {
        Page<PortfolioItem> portfolioItemPage = new Page<>(page, size);
        portfolioItemPage = portfolioItemMapper.selectPublicPageByCategory(portfolioItemPage, category);
        
        return convertPageToVO(portfolioItemPage);
    }
    
    @Override
    @Transactional
    public PortfolioItemVO getPortfolioItemDetail(Integer id, String viewerIp) {
        PortfolioItem portfolioItem = portfolioItemMapper.selectById(id);
        if (portfolioItem == null || portfolioItem.getIsDeleted()) {
            throw new BusinessException("作品不存在");
        }
        
        // 增加查看次数
        portfolioItemMapper.increaseViewCount(id);
        
        // 记录访问记录
        AchievementView achievementView = new AchievementView();
        achievementView.setAchievementType("portfolio");
        achievementView.setAchievementId(id);
        achievementView.setViewerIp(viewerIp);
        achievementView.setViewTime(LocalDateTime.now());
        achievementViewMapper.insert(achievementView);
        
        return convertToDetailVO(portfolioItem);
    }
    
    @Override
    @Transactional
    public PortfolioItemVO createPortfolioItem(Integer userId, PortfolioItemCreationDTO portfolioItemCreationDTO, MultipartFile coverImage) {
        // 验证用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 上传封面图片（如果有）
        String coverImageUrl = null;
        if (coverImage != null && !coverImage.isEmpty()) {
            coverImageUrl = fileService.uploadFile(coverImage, "portfolio/covers");
        }
        
        // 处理标签列表转换为逗号分隔字符串
        String tagsStr = null;
        if (portfolioItemCreationDTO.getTags() != null && !portfolioItemCreationDTO.getTags().isEmpty()) {
            tagsStr = String.join(",", portfolioItemCreationDTO.getTags());
        }
        
        // 创建作品
        PortfolioItem portfolioItem = new PortfolioItem();
        BeanUtils.copyProperties(portfolioItemCreationDTO, portfolioItem);
        portfolioItem.setTags(tagsStr); // 设置转换后的标签字符串
        portfolioItem.setUserId(userId);
        portfolioItem.setViewCount(0);
        portfolioItem.setLikeCount(0);
        
        // 设置封面图片URL
        if (coverImageUrl != null) {
            portfolioItem.setCoverImageUrl(coverImageUrl);
        }
        
        portfolioItem.setCreatedAt(LocalDateTime.now());
        portfolioItem.setUpdatedAt(LocalDateTime.now());
        portfolioItem.setIsDeleted(false);
        
        portfolioItemMapper.insert(portfolioItem);
        
        return convertToVO(portfolioItem);
    }
    
    @Override
    @Transactional
    public PortfolioItemVO updatePortfolioItem(Integer id, Integer userId, PortfolioItemCreationDTO portfolioItemCreationDTO, MultipartFile coverImage) {
        // 验证作品是否存在
        PortfolioItem portfolioItem = portfolioItemMapper.selectById(id);
        if (portfolioItem == null || portfolioItem.getIsDeleted()) {
            throw new BusinessException("作品不存在");
        }
        
        // 验证是否是作品所有者
        if (!portfolioItem.getUserId().equals(userId)) {
            throw new BusinessException("无权修改该作品");
        }
        
        // 上传封面图片（如果有）
        if (coverImage != null && !coverImage.isEmpty()) {
            String coverImageUrl = fileService.uploadFile(coverImage, "portfolio/covers");
            portfolioItem.setCoverImageUrl(coverImageUrl);
        }
        
        // 处理标签列表转换为逗号分隔字符串
        if (portfolioItemCreationDTO.getTags() != null && !portfolioItemCreationDTO.getTags().isEmpty()) {
            String tagsStr = String.join(",", portfolioItemCreationDTO.getTags());
            portfolioItem.setTags(tagsStr);
        }
        
        // 选择性更新非null字段，而不是全部覆盖
        if (portfolioItemCreationDTO.getTitle() != null) {
            portfolioItem.setTitle(portfolioItemCreationDTO.getTitle());
        }
        if (portfolioItemCreationDTO.getDescription() != null) {
            portfolioItem.setDescription(portfolioItemCreationDTO.getDescription());
        }
        if (portfolioItemCreationDTO.getProjectUrl() != null) {
            portfolioItem.setProjectUrl(portfolioItemCreationDTO.getProjectUrl());
        }
        if (portfolioItemCreationDTO.getCategory() != null) {
            portfolioItem.setCategory(portfolioItemCreationDTO.getCategory());
        }
        if (portfolioItemCreationDTO.getTeamMembers() != null) {
            portfolioItem.setTeamMembers(portfolioItemCreationDTO.getTeamMembers());
        }
        if (portfolioItemCreationDTO.getIsPublic() != null) {
            portfolioItem.setIsPublic(portfolioItemCreationDTO.getIsPublic());
        }
        
        // 更新时间戳
        portfolioItem.setUpdatedAt(LocalDateTime.now());
        
        portfolioItemMapper.updateById(portfolioItem);
        
        return convertToVO(portfolioItem);
    }
    
    @Override
    @Transactional
    public boolean deletePortfolioItem(Integer id, Integer userId) {
        // 验证作品是否存在
        PortfolioItem portfolioItem = portfolioItemMapper.selectById(id);
        if (portfolioItem == null || portfolioItem.getIsDeleted()) {
            throw new BusinessException("作品不存在");
        }
        
        // 验证是否是作品所有者
        if (!portfolioItem.getUserId().equals(userId)) {
            throw new BusinessException("无权删除该作品");
        }
        
        // 逻辑删除作品
        portfolioItemMapper.deleteById(id);
        
        // 检查是否有资源，并删除作品资源（物理删除）
        try {
            List<PortfolioResource> resources = portfolioResourceMapper.selectByPortfolioItemId(id);
            if (resources != null && !resources.isEmpty()) {
                // 有资源才删除
                portfolioResourceMapper.deleteByPortfolioItemId(id);
                log.info("已删除作品[{}]的{}个资源", id, resources.size());
            } else {
                log.info("作品[{}]没有资源需要删除", id);
            }
        } catch (Exception e) {
            log.error("删除作品[{}]的资源时发生错误: {}", id, e.getMessage());
            // 资源删除失败不影响作品删除的结果
        }
        
        return true;
    }
    
    @Override
    @Transactional
    public PortfolioResourceVO uploadResourceFile(Integer portfolioItemId, Integer userId, MultipartFile file, String resourceType, String description) {
        // 验证作品是否存在
        PortfolioItem portfolioItem = portfolioItemMapper.selectById(portfolioItemId);
        if (portfolioItem == null || portfolioItem.getIsDeleted()) {
            throw new BusinessException("作品不存在");
        }
        
        // 验证是否是作品所有者
        if (!portfolioItem.getUserId().equals(userId)) {
            throw new BusinessException("无权为该作品上传资源");
        }
        
        // 上传文件
        String resourceUrl = fileService.uploadFile(file, "portfolio");
        
        // 创建资源
        PortfolioResource portfolioResource = new PortfolioResource();
        portfolioResource.setPortfolioItemId(portfolioItemId);
        portfolioResource.setResourceType(resourceType);
        portfolioResource.setResourceUrl(resourceUrl);
        portfolioResource.setDescription(description);
        portfolioResource.setDisplayOrder(0);
        portfolioResource.setCreatedAt(LocalDateTime.now());
        
        portfolioResourceMapper.insert(portfolioResource);
        
        return convertResourceToVO(portfolioResource);
    }
    
    @Override
    @Transactional
    public boolean deleteResource(Integer portfolioItemId, Integer resourceId, Integer userId) {
        // 验证作品是否存在
        PortfolioItem portfolioItem = portfolioItemMapper.selectById(portfolioItemId);
        if (portfolioItem == null || portfolioItem.getIsDeleted()) {
            throw new BusinessException("作品不存在");
        }
        
        // 验证是否是作品所有者
        if (!portfolioItem.getUserId().equals(userId)) {
            throw new BusinessException("无权删除该资源");
        }
        
        // 验证资源是否存在
        PortfolioResource portfolioResource = portfolioResourceMapper.selectById(resourceId);
        if (portfolioResource == null) {
            throw new BusinessException("资源不存在");
        }
        
        // 验证资源是否属于该作品
        if (!portfolioResource.getPortfolioItemId().equals(portfolioItemId)) {
            throw new BusinessException("资源不属于该作品");
        }
        
        // 删除资源
        portfolioResourceMapper.deleteById(resourceId);
        
        return true;
    }
    
    @Override
    @Transactional
    public boolean likePortfolioItem(Integer id) {
        // 验证作品是否存在
        PortfolioItem portfolioItem = portfolioItemMapper.selectById(id);
        if (portfolioItem == null || portfolioItem.getIsDeleted()) {
            throw new BusinessException("作品不存在");
        }
        
        // 增加点赞数
        portfolioItemMapper.increaseLikeCount(id);
        
        return true;
    }
    
    /**
     * 将作品实体转换为VO
     * 
     * @param portfolioItem 作品实体
     * @return 作品VO
     */
    private PortfolioItemVO convertToVO(PortfolioItem portfolioItem) {
        if (portfolioItem == null) {
            return null;
        }
        
        PortfolioItemVO portfolioItemVO = new PortfolioItemVO();
        BeanUtils.copyProperties(portfolioItem, portfolioItemVO);
        
        // 处理标签字符串转换为列表
        if (portfolioItem.getTags() != null && !portfolioItem.getTags().isEmpty()) {
            portfolioItemVO.setTags(List.of(portfolioItem.getTags().split(",")));
        }
        
        // 转换封面图片URL为完整URL
        if (portfolioItem.getCoverImageUrl() != null && !portfolioItem.getCoverImageUrl().isEmpty()) {
            portfolioItemVO.setCoverImageUrl(fileService.getFullFileUrl(portfolioItem.getCoverImageUrl()));
        }
        
        // 获取用户信息
        User user = userMapper.selectById(portfolioItem.getUserId());
        if (user != null) {
            portfolioItemVO.setUserName(user.getNickname());
            
            // 获取组织信息
            if (user.getOrganizationId() != null) {
                portfolioItemVO.setOrganizationName(userMapper.selectOrganizationNameById(user.getOrganizationId()));
            }
        }
        
        return portfolioItemVO;
    }
    
    /**
     * 将作品实体转换为详细VO（包含资源列表）
     * 
     * @param portfolioItem 作品实体
     * @return 作品详细VO
     */
    private PortfolioItemVO convertToDetailVO(PortfolioItem portfolioItem) {
        PortfolioItemVO portfolioItemVO = convertToVO(portfolioItem);
        
        // 获取资源列表
        List<PortfolioResource> resources = portfolioResourceMapper.selectByPortfolioItemId(portfolioItem.getId());
        List<PortfolioResourceVO> resourceVOs = resources.stream()
                .map(this::convertResourceToVO)
                .collect(Collectors.toList());
        
        portfolioItemVO.setResources(resourceVOs);
        
        return portfolioItemVO;
    }
    
    /**
     * 将资源实体转换为VO
     * 
     * @param portfolioResource 资源实体
     * @return 资源VO
     */
    private PortfolioResourceVO convertResourceToVO(PortfolioResource portfolioResource) {
        if (portfolioResource == null) {
            return null;
        }
        log.info("portfolioResource: {}", portfolioResource);
        
        
        PortfolioResourceVO portfolioResourceVO = new PortfolioResourceVO();
        BeanUtils.copyProperties(portfolioResource, portfolioResourceVO);
        
        // 转换资源URL为完整URL
        if (portfolioResource.getResourceUrl() != null && !portfolioResource.getResourceUrl().isEmpty()) {
            portfolioResourceVO.setResourceUrl(fileService.getFullFileUrl(portfolioResource.getResourceUrl()));
        }
        
        return portfolioResourceVO;
    }
    
    /**
     * 将作品分页结果转换为VO分页结果
     * 
     * @param portfolioItemPage 作品分页结果
     * @return VO分页结果
     */
    private Page<PortfolioItemVO> convertPageToVO(Page<PortfolioItem> portfolioItemPage) {
        Page<PortfolioItemVO> portfolioItemVOPage = new Page<>(
                portfolioItemPage.getCurrent(),
                portfolioItemPage.getSize(),
                portfolioItemPage.getTotal(),
                portfolioItemPage.searchCount());
        
        List<PortfolioItemVO> portfolioItemVOs = portfolioItemPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        portfolioItemVOPage.setRecords(portfolioItemVOs);
        
        return portfolioItemVOPage;
    }
    
    @Override
    public Page<PortfolioItemVO> getSchoolStudentPortfolioItems(Integer userId, int page, int size) {
        // 获取当前教师或管理员所属的组织ID
        User currentUser = userMapper.selectById(userId);
        if (currentUser == null || currentUser.getOrganizationId() == null) {
            throw new BusinessException("用户不存在或未关联组织");
        }
        
        Integer organizationId = currentUser.getOrganizationId();
        
        // 查询该组织下的所有学生作品
        Page<PortfolioItem> pageParam = new Page<>(page, size);
        Page<PortfolioItem> portfolioItemPage = portfolioItemMapper.selectByOrganizationId(pageParam, organizationId);
        
        return convertPageToVO(portfolioItemPage);
    }
    
    @Override
    public Page<PortfolioItemVO> getSchoolStudentPortfolioItemsByCategory(Integer userId, String category, int page, int size) {
        // 获取当前教师或管理员所属的组织ID
        User currentUser = userMapper.selectById(userId);
        if (currentUser == null || currentUser.getOrganizationId() == null) {
            throw new BusinessException("用户不存在或未关联组织");
        }
        
        Integer organizationId = currentUser.getOrganizationId();
        
        // 查询该组织下的指定分类的学生作品
        Page<PortfolioItem> pageParam = new Page<>(page, size);
        Page<PortfolioItem> portfolioItemPage = portfolioItemMapper.selectByOrganizationIdAndCategory(pageParam, organizationId, category);
        
        return convertPageToVO(portfolioItemPage);
    }
    
    @Override
    public List<PortfolioItemVO> getSchoolStudentPortfolioItemsByStudent(Integer userId, Integer studentId) {
        // 获取当前教师或管理员所属的组织ID
        User currentUser = userMapper.selectById(userId);
        if (currentUser == null || currentUser.getOrganizationId() == null) {
            throw new BusinessException("用户不存在或未关联组织");
        }
        
        Integer organizationId = currentUser.getOrganizationId();
        
        // 验证学生是否属于该组织
        User student = userMapper.selectById(studentId);
        if (student == null) {
            throw new BusinessException("学生不存在");
        }
        
        if (!organizationId.equals(student.getOrganizationId())) {
            throw new BusinessException("无权访问该学生的作品");
        }
        
        // 获取学生的作品列表
        List<PortfolioItem> portfolioItems = portfolioItemMapper.selectByUserId(studentId);
        
        return portfolioItems.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }
    
    @Override
    public Map<String, Object> getSchoolPortfolioStatistics(Integer userId) {
        // 获取当前教师或管理员所属的组织ID
        User currentUser = userMapper.selectById(userId);
        if (currentUser == null || currentUser.getOrganizationId() == null) {
            throw new BusinessException("用户不存在或未关联组织");
        }
        
        Integer organizationId = currentUser.getOrganizationId();
        String organizationName = userMapper.selectOrganizationNameById(organizationId);
        
        // 获取该组织下的所有学生
        List<User> students = userMapper.selectList(null).stream()
                .filter(user -> organizationId.equals(user.getOrganizationId()))
                .filter(user -> userMapper.hasRole(user.getId(), "STUDENT"))
                .toList();
        
        // 获取该组织下的所有作品
        List<PortfolioItem> allPortfolioItems = new ArrayList<>();
        for (User student : students) {
            allPortfolioItems.addAll(portfolioItemMapper.selectByUserId(student.getId()));
        }
        
        // 统计数据
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("organizationId", organizationId);
        statistics.put("organizationName", organizationName);
        statistics.put("totalPortfolioItems", allPortfolioItems.size());
        
        // 统计公开作品数量
        long publicPortfolioItems = allPortfolioItems.stream()
                .filter(PortfolioItem::getIsPublic)
                .count();
        statistics.put("publicPortfolioItems", publicPortfolioItems);
        
        // 统计总访问量
        int totalViewCount = allPortfolioItems.stream()
                .mapToInt(PortfolioItem::getViewCount)
                .sum();
        statistics.put("totalViewCount", totalViewCount);
        
        // 统计总点赞数
        int totalLikeCount = allPortfolioItems.stream()
                .mapToInt(PortfolioItem::getLikeCount)
                .sum();
        statistics.put("totalLikeCount", totalLikeCount);
        
        // 统计分类分布
        Map<String, Long> categoryDistribution = allPortfolioItems.stream()
                .filter(item -> item.getCategory() != null && !item.getCategory().isEmpty())
                .collect(Collectors.groupingBy(
                        PortfolioItem::getCategory,
                        Collectors.counting()
                ));
        statistics.put("categoryDistribution", categoryDistribution);
        
        // 统计有作品的学生数量
        long studentsWithPortfolios = students.stream()
                .filter(student -> !portfolioItemMapper.selectByUserId(student.getId()).isEmpty())
                .count();
        statistics.put("studentsWithPortfolios", studentsWithPortfolios);
        
        // 计算平均每个学生的作品数量
        double avgPortfoliosPerStudent = students.isEmpty() ? 0 : (double) allPortfolioItems.size() / students.size();
        statistics.put("avgPortfoliosPerStudent", Math.round(avgPortfoliosPerStudent * 10) / 10.0);
        
        // 计算作品覆盖率（有作品的学生占总学生的比例）
        double portfolioCoverage = students.isEmpty() ? 0 : (double) studentsWithPortfolios / students.size() * 100;
        statistics.put("portfolioCoverage", Math.round(portfolioCoverage));
        
        return statistics;
    }
} 