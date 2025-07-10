package com.csu.unicorp.service.impl;

import com.csu.unicorp.common.exception.BusinessException;
import com.csu.unicorp.entity.job.Job;
import com.csu.unicorp.entity.recommendation.JobFeature;
import com.csu.unicorp.mapper.job.JobCategoryRelationMapper;
import com.csu.unicorp.mapper.job.JobMapper;
import com.csu.unicorp.mapper.recommendation.JobFeatureMapper;
import com.csu.unicorp.service.JobFeatureService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 岗位特征服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class JobFeatureServiceImpl implements JobFeatureService {

    private final JobMapper jobMapper;
    private final JobFeatureMapper jobFeatureMapper;
    private final JobCategoryRelationMapper jobCategoryRelationMapper;
    private final ObjectMapper objectMapper;
    
    /**
     * 常见技能关键词
     */
    private static final Set<String> SKILL_KEYWORDS = new HashSet<>(Arrays.asList(
            "技能", "能力", "要求", "掌握", "熟悉", "了解", "精通", "开发", "设计",
            "编程", "语言", "框架", "工具", "软件", "系统", "平台", "经验"
    ));
    
    /**
     * 常见停用词
     */
    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
            "的", "了", "是", "在", "我们", "公司", "岗位", "职位", "工作", "并且",
            "以及", "同时", "或者", "和", "与", "及", "等", "等等", "职责", "岗位职责"
    ));
    
    @Override
    @Transactional
    public boolean generateJobFeature(Integer jobId) {
        // 查询岗位信息
        Job job = jobMapper.selectById(jobId);
        if (job == null) {
            log.error("岗位不存在，无法生成特征，jobId={}", jobId);
            return false;
        }
        
        // 查询是否已存在特征，如存在则更新
        JobFeature jobFeature = jobFeatureMapper.selectByJobId(jobId);
        boolean isNew = false;
        if (jobFeature == null) {
            jobFeature = new JobFeature();
            jobFeature.setJobId(jobId);
            jobFeature.setCreatedAt(LocalDateTime.now());
            isNew = true;
        }
        
        // 提取技能要求
        List<String> requiredSkills = extractSkills(job.getJobRequirements(), job.getDescription());
        
        // 提取关键词
        List<String> keywords = extractKeywords(job.getDescription(), job.getTitle(), job.getJobType());
        
        // 获取分类ID
        List<Integer> categoryIds = jobCategoryRelationMapper.selectCategoryIdsByJobId(jobId);
        if (!categoryIds.isEmpty()) {
            jobFeature.setCategoryId(categoryIds.get(0));
        }
        
        try {
            // 转换为JSON字符串
            jobFeature.setRequiredSkills(objectMapper.writeValueAsString(requiredSkills));
            jobFeature.setKeywords(objectMapper.writeValueAsString(keywords));
        } catch (JsonProcessingException e) {
            log.error("转换特征为JSON失败", e);
            throw new BusinessException("生成岗位特征失败: " + e.getMessage());
        }
        
        jobFeature.setUpdatedAt(LocalDateTime.now());
        
        // 保存特征
        if (isNew) {
            return jobFeatureMapper.insert(jobFeature) > 0;
        } else {
            return jobFeatureMapper.updateById(jobFeature) > 0;
        }
    }
    
    /**
     * 从岗位要求和描述中提取技能
     */
    private List<String> extractSkills(String jobRequirements, String description) {
        Set<String> skills = new HashSet<>();
        
        // 从岗位要求中提取技能
        if (StringUtils.hasText(jobRequirements)) {
            // 按行分割，提取包含常见技能关键词的行
            String[] lines = jobRequirements.split("\\r?\\n");
            for (String line : lines) {
                if (containsSkillKeywords(line)) {
                    // 提取技能名称
                    List<String> extractedSkills = extractSkillsFromLine(line);
                    skills.addAll(extractedSkills);
                }
            }
        }
        
        // 如果岗位要求中没有提取到足够的技能，尝试从描述中提取
        if (skills.size() < 3 && StringUtils.hasText(description)) {
            String[] lines = description.split("\\r?\\n");
            for (String line : lines) {
                if (containsSkillKeywords(line)) {
                    List<String> extractedSkills = extractSkillsFromLine(line);
                    skills.addAll(extractedSkills);
                }
            }
        }
        
        // 如果没有提取到技能，使用预设的通用技能
        if (skills.isEmpty()) {
            skills.add("沟通能力");
            skills.add("团队协作");
            skills.add("问题解决");
        }
        
        // 限制技能数量，最多10个
        List<String> result = new ArrayList<>(skills);
        if (result.size() > 10) {
            result = result.subList(0, 10);
        }
        
        return result;
    }
    
    /**
     * 判断文本是否包含技能关键词
     */
    private boolean containsSkillKeywords(String text) {
        if (!StringUtils.hasText(text)) {
            return false;
        }
        
        for (String keyword : SKILL_KEYWORDS) {
            if (text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 从行文本中提取技能名称
     */
    private List<String> extractSkillsFromLine(String line) {
        List<String> skills = new ArrayList<>();
        
        // 处理常见的技能列表格式
        if (line.contains("：") || line.contains(":")) {
            String content = line.contains("：") ? 
                    line.substring(line.indexOf("：") + 1) : 
                    line.substring(line.indexOf(":") + 1);
            
            // 按分隔符分割
            String[] parts = content.split("[,，、/\\s]+");
            for (String part : parts) {
                String skill = cleanSkill(part);
                if (StringUtils.hasText(skill) && skill.length() > 1 && !isStopWord(skill)) {
                    skills.add(skill);
                }
            }
        } else {
            // 处理没有明确分隔符的情况
            // 尝试提取常见技术名词、编程语言等
            Pattern pattern = Pattern.compile("([A-Za-z0-9+#]+|[\\u4e00-\\u9fa5]{2,})");
            Matcher matcher = pattern.matcher(line);
            while (matcher.find()) {
                String skill = cleanSkill(matcher.group());
                if (StringUtils.hasText(skill) && skill.length() > 1 && !isStopWord(skill)) {
                    skills.add(skill);
                }
            }
        }
        
        return skills;
    }
    
    /**
     * 清理提取的技能文本
     */
    private String cleanSkill(String skill) {
        if (!StringUtils.hasText(skill)) {
            return "";
        }
        
        // 移除括号内容
        skill = skill.replaceAll("\\(.*?\\)", "").replaceAll("（.*?）", "");
        
        // 移除标点符号
        skill = skill.replaceAll("[,.;，。；!?！？]", "");
        
        return skill.trim();
    }
    
    /**
     * 从岗位描述和标题中提取关键词
     */
    private List<String> extractKeywords(String description, String title, String jobType) {
        Set<String> keywords = new HashSet<>();
        
        // 添加标题中的关键词
        if (StringUtils.hasText(title)) {
            String[] titleWords = title.split("[\\s+,，。、；：]+");
            for (String word : titleWords) {
                if (word.length() > 1 && !isStopWord(word)) {
                    keywords.add(word.trim());
                }
            }
        }
        
        // 添加工作类型作为关键词
        if (StringUtils.hasText(jobType)) {
            switch (jobType) {
                case "full_time":
                    keywords.add("全职");
                    break;
                case "part_time":
                    keywords.add("兼职");
                    break;
                case "internship":
                    keywords.add("实习");
                    break;
                case "remote":
                    keywords.add("远程");
                    break;
                default:
                    keywords.add(jobType);
            }
        }
        
        // 从描述中提取关键词
        if (StringUtils.hasText(description)) {
            // 提取第一段作为关键信息来源
            String firstParagraph = description;
            int endOfFirstParagraph = description.indexOf("\n\n");
            if (endOfFirstParagraph > 0) {
                firstParagraph = description.substring(0, endOfFirstParagraph);
            }
            
            // 分词并提取关键词
            String[] words = firstParagraph.split("[\\s+,，。、；：]+");
            for (String word : words) {
                if (word.length() > 1 && !isStopWord(word)) {
                    keywords.add(word.trim());
                }
            }
            
            // 提取技术名词和专业术语
            Pattern pattern = Pattern.compile("([A-Za-z0-9+#]+|[\\u4e00-\\u9fa5]{2,})");
            Matcher matcher = pattern.matcher(description);
            while (matcher.find()) {
                String word = matcher.group().trim();
                if (word.length() > 1 && !isStopWord(word)) {
                    keywords.add(word);
                }
            }
        }
        
        // 限制关键词数量，最多15个
        List<String> result = new ArrayList<>(keywords);
        if (result.size() > 15) {
            result = result.subList(0, 15);
        }
        
        return result;
    }
    
    /**
     * 判断是否为停用词
     */
    private boolean isStopWord(String word) {
        return STOP_WORDS.contains(word);
    }
} 