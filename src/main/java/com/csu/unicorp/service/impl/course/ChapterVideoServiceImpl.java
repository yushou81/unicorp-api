package com.csu.unicorp.service.impl.course;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.csu.unicorp.common.exception.BusinessException;
import com.csu.unicorp.dto.ChapterVideoDTO;
import com.csu.unicorp.entity.course.ChapterVideo;
import com.csu.unicorp.entity.course.CourseChapter;
import com.csu.unicorp.entity.course.VideoWatchRecord;
import com.csu.unicorp.entity.User;
import com.csu.unicorp.mapper.course.ChapterVideoMapper;
import com.csu.unicorp.mapper.course.CourseChapterMapper;
import com.csu.unicorp.mapper.course.VideoWatchRecordMapper;
import com.csu.unicorp.service.ChapterVideoService;
import com.csu.unicorp.service.FileService;
import com.csu.unicorp.service.UserService;
import com.csu.unicorp.vo.ChapterVideoVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import ws.schild.jave.EncoderException;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.info.MultimediaInfo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 章节视频服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ChapterVideoServiceImpl extends ServiceImpl<ChapterVideoMapper, ChapterVideo> implements ChapterVideoService {

    private final ChapterVideoMapper videoMapper;
    private final VideoWatchRecordMapper watchRecordMapper;
    private final CourseChapterMapper chapterMapper;
    private final UserService userService;
    private final FileService fileService;
    
    // 视频存储路径
    private static final String VIDEO_UPLOAD_PATH = "courses/videos/";
    private static final String VIDEO_COVER_PATH = "courses/videos/covers/";
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ChapterVideoVO uploadVideo(MultipartFile file, ChapterVideoDTO videoDTO, UserDetails userDetails) throws IOException {
        // 检查章节是否存在
        CourseChapter chapter = chapterMapper.selectById(videoDTO.getChapterId());
        if (chapter == null) {
            throw new BusinessException("章节不存在");
        }
        
        // 检查上传权限
        String username = userDetails.getUsername();
        Integer userId = getUserId(username);
        
        // 检查是否已有视频，如果有则先删除
        ChapterVideo existingVideo = videoMapper.selectByChapterId(videoDTO.getChapterId());
        if (existingVideo != null) {
            // 删除旧视频文件
            try {
                if (StringUtils.hasText(existingVideo.getFilePath())) {
                    fileService.deleteFile(existingVideo.getFilePath());
                }
                
                if (StringUtils.hasText(existingVideo.getCoverImage())) {
                    fileService.deleteFile(existingVideo.getCoverImage());
                }
            } catch (Exception e) {
                log.error("删除旧视频文件失败", e);
                // 文件删除失败不影响业务逻辑
            }
            
            // 逻辑删除数据库记录
            existingVideo.setIsDeleted(true);
            videoMapper.updateById(existingVideo);
        }
        
        // 上传视频文件
        String videoPath = fileService.uploadFile(file, "video");
        
        // 获取视频时长
        Integer duration = 0;
        try {
            Path filePath = Paths.get("upload", videoPath);
            duration = getVideoDuration(filePath.toFile());
        } catch (Exception e) {
            log.error("获取视频时长失败", e);
        }
        
        // 生成封面图（从第一帧提取）
        String coverImage = null;
        try {
            // 使用视频文件名创建封面图名称
            String videoFileName = videoPath.substring(videoPath.lastIndexOf('/') + 1);
            String coverFileName = videoFileName.substring(0, videoFileName.lastIndexOf('.')) + ".jpg";
            Path coverPath = Paths.get("upload", VIDEO_COVER_PATH, coverFileName);
            
            // 确保目录存在
            Files.createDirectories(coverPath.getParent());
            
            // 使用Java的ProcessBuilder调用FFmpeg提取视频第一帧作为封面
            ProcessBuilder pb = new ProcessBuilder(
                "ffmpeg", 
                "-i", Paths.get("upload", videoPath).toString(),
                "-ss", "00:00:01", // 从1秒处截取
                "-vframes", "1", // 只截取1帧
                "-q:v", "2", // 质量设置
                coverPath.toString()
            );
            
            Process process = pb.start();
            int exitCode = process.waitFor();
            
            if (exitCode == 0) {
                // FFmpeg成功生成封面
                coverImage = VIDEO_COVER_PATH + coverFileName;
                log.info("成功生成视频封面: {}", coverImage);
            } else {
                log.error("生成视频封面失败，FFmpeg返回码: {}", exitCode);
            }
        } catch (Exception e) {
            log.error("生成视频封面失败", e);
        }
        
        // 保存视频信息到数据库
        ChapterVideo video = new ChapterVideo();
        video.setChapterId(videoDTO.getChapterId());
        video.setTitle(videoDTO.getTitle());
        video.setDescription(videoDTO.getDescription());
        video.setFilePath(videoPath);
        video.setFileSize(file.getSize());
        video.setDuration(duration);
        video.setCoverImage(coverImage);
        video.setUploaderId(userId);
        video.setCreatedAt(LocalDateTime.now());
        video.setUpdatedAt(LocalDateTime.now());
        video.setIsDeleted(false);
        
        videoMapper.insert(video);
        
        // 转换为VO并返回
        ChapterVideoVO vo = convertToVO(video);
        vo.setChapterTitle(chapter.getTitle());
        vo.setUploaderName(getUserName(userId));
        
        return vo;
    }

    @Override
    public ChapterVideoVO getVideoById(Integer videoId, UserDetails userDetails) {
        ChapterVideo video = videoMapper.selectById(videoId);
        if (video == null || video.getIsDeleted()) {
            throw new BusinessException("视频不存在");
        }
        
        // 获取章节信息
        CourseChapter chapter = chapterMapper.selectById(video.getChapterId());
        if (chapter == null) {
            throw new BusinessException("章节不存在");
        }
        
        // 转换为VO
        ChapterVideoVO vo = convertToVO(video);
        vo.setChapterTitle(chapter.getTitle());
        vo.setUploaderName(getUserName(video.getUploaderId()));
        
        // 如果是学生，获取观看进度
        if (isStudent(userDetails)) {
            Integer userId = getUserId(userDetails.getUsername());
            VideoWatchRecord watchRecord = watchRecordMapper.selectByVideoAndUser(videoId, userId);
            if (watchRecord != null) {
                vo.setWatchProgress(watchRecord.getWatchProgress());
                vo.setIsCompleted(watchRecord.getIsCompleted());
                vo.setLastPosition(watchRecord.getLastPosition());
            } else {
                vo.setWatchProgress(0);
                vo.setIsCompleted(false);
                vo.setLastPosition(0);
            }
        }
        
        return vo;
    }

    @Override
    public ChapterVideoVO getVideoByChapterId(Integer chapterId, UserDetails userDetails) {
        // 检查章节是否存在
        CourseChapter chapter = chapterMapper.selectById(chapterId);
        if (chapter == null) {
            throw new BusinessException("章节不存在");
        }
        
        // 查询章节视频
        ChapterVideo video = videoMapper.selectByChapterId(chapterId);
        if (video == null) {
            return null; // 章节没有关联视频
        }
        
        // 转换为VO
        ChapterVideoVO vo = convertToVO(video);
        vo.setChapterTitle(chapter.getTitle());
        vo.setUploaderName(getUserName(video.getUploaderId()));
        
        // 如果是学生，获取观看进度
        if (isStudent(userDetails)) {
            Integer userId = getUserId(userDetails.getUsername());
            VideoWatchRecord watchRecord = watchRecordMapper.selectByVideoAndUser(video.getId(), userId);
            if (watchRecord != null) {
                vo.setWatchProgress(watchRecord.getWatchProgress());
                vo.setIsCompleted(watchRecord.getIsCompleted());
                vo.setLastPosition(watchRecord.getLastPosition());
            } else {
                vo.setWatchProgress(0);
                vo.setIsCompleted(false);
                vo.setLastPosition(0);
            }
        }
        
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteVideo(Integer videoId, UserDetails userDetails) {
        ChapterVideo video = videoMapper.selectById(videoId);
        if (video == null || video.getIsDeleted()) {
            throw new BusinessException("视频不存在");
        }
        
        // 检查删除权限
        String username = userDetails.getUsername();
        Integer userId = getUserId(username);
        
        // 检查是否有权限删除（教师或上传者）
        boolean isTeacher = hasRole(userDetails, "TEACHER");
        boolean isAdmin = hasRole(userDetails, "SCH_ADMIN");
        boolean isUploader = Objects.equals(video.getUploaderId(), userId);
        
        if (!isTeacher && !isAdmin && !isUploader) {
            throw new BusinessException("无权删除该视频");
        }
        
        // 删除视频文件
        try {
            if (StringUtils.hasText(video.getFilePath())) {
                fileService.deleteFile(video.getFilePath());
            }
            
            if (StringUtils.hasText(video.getCoverImage())) {
                fileService.deleteFile(video.getCoverImage());
            }
        } catch (Exception e) {
            log.error("删除视频文件失败", e);
            // 文件删除失败不影响业务逻辑
        }
        
        // 逻辑删除视频记录
        video.setIsDeleted(true);
        videoMapper.deleteById(video);
        
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ChapterVideoVO updateVideo(Integer videoId, ChapterVideoDTO videoDTO, UserDetails userDetails) {
        ChapterVideo video = videoMapper.selectById(videoId);
        if (video == null || video.getIsDeleted()) {
            throw new BusinessException("视频不存在");
        }
        
        // 检查更新权限
        String username = userDetails.getUsername();
        Integer userId = getUserId(username);
        
        // 检查是否有权限更新（教师或上传者）
        boolean isTeacher = hasRole(userDetails, "TEACHER");
        boolean isAdmin = hasRole(userDetails, "SCH_ADMIN");
        boolean isUploader = Objects.equals(video.getUploaderId(), userId);
        
        if (!isTeacher && !isAdmin && !isUploader) {
            throw new BusinessException("无权更新该视频");
        }
        
        // 更新视频信息
        video.setTitle(videoDTO.getTitle());
        video.setDescription(videoDTO.getDescription());
        video.setUpdatedAt(LocalDateTime.now());
        
        videoMapper.updateById(video);
        
        // 获取章节信息
        CourseChapter chapter = chapterMapper.selectById(video.getChapterId());
        
        // 转换为VO并返回
        ChapterVideoVO vo = convertToVO(video);
        vo.setChapterTitle(chapter != null ? chapter.getTitle() : "未知章节");
        vo.setUploaderName(getUserName(video.getUploaderId()));
        
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateWatchProgress(Integer videoId, Integer position, UserDetails userDetails) {
        if (!isStudent(userDetails)) {
            throw new BusinessException("只有学生可以更新观看进度");
        }
        
        ChapterVideo video = videoMapper.selectById(videoId);
        if (video == null || video.getIsDeleted()) {
            throw new BusinessException("视频不存在");
        }
        
        Integer userId = getUserId(userDetails.getUsername());
        
        // 查询是否已有观看记录
        VideoWatchRecord watchRecord = watchRecordMapper.selectByVideoAndUser(videoId, userId);
        
        if (watchRecord == null) {
            // 创建新的观看记录
            watchRecord = new VideoWatchRecord();
            watchRecord.setVideoId(videoId);
            watchRecord.setUserId(userId);
            watchRecord.setWatchProgress(position);
            watchRecord.setLastPosition(position);
            watchRecord.setIsCompleted(false);
            watchRecord.setCreatedAt(LocalDateTime.now());
            watchRecord.setUpdatedAt(LocalDateTime.now());
            
            watchRecordMapper.insert(watchRecord);
        } else {
            // 更新现有观看记录
            watchRecord.setWatchProgress(Math.max(watchRecord.getWatchProgress(), position));
            watchRecord.setLastPosition(position);
            watchRecord.setUpdatedAt(LocalDateTime.now());
            
            // 如果进度接近视频结尾，标记为已完成
            if (video.getDuration() != null && video.getDuration() > 0) {
                // 如果观看进度超过95%，视为已完成
                if (position >= video.getDuration() * 0.95) {
                    watchRecord.setIsCompleted(true);
                }
            }
            
            watchRecordMapper.updateById(watchRecord);
        }
        
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markVideoCompleted(Integer videoId, UserDetails userDetails) {
        if (!isStudent(userDetails)) {
            throw new BusinessException("只有学生可以标记视频完成状态");
        }
        
        ChapterVideo video = videoMapper.selectById(videoId);
        if (video == null || video.getIsDeleted()) {
            throw new BusinessException("视频不存在");
        }
        
        Integer userId = getUserId(userDetails.getUsername());
        
        // 查询是否已有观看记录
        VideoWatchRecord watchRecord = watchRecordMapper.selectByVideoAndUser(videoId, userId);
        
        if (watchRecord == null) {
            // 创建新的观看记录
            watchRecord = new VideoWatchRecord();
            watchRecord.setVideoId(videoId);
            watchRecord.setUserId(userId);
            watchRecord.setWatchProgress(video.getDuration() != null ? video.getDuration() : 0);
            watchRecord.setLastPosition(video.getDuration() != null ? video.getDuration() : 0);
            watchRecord.setIsCompleted(true);
            watchRecord.setCreatedAt(LocalDateTime.now());
            watchRecord.setUpdatedAt(LocalDateTime.now());
            
            watchRecordMapper.insert(watchRecord);
        } else {
            // 更新现有观看记录
            watchRecord.setIsCompleted(true);
            if (video.getDuration() != null) {
                watchRecord.setWatchProgress(video.getDuration());
            }
            watchRecord.setUpdatedAt(LocalDateTime.now());
            
            watchRecordMapper.updateById(watchRecord);
        }
        
        return true;
    }
    
    /**
     * 将实体转换为VO
     */
    private ChapterVideoVO convertToVO(ChapterVideo video) {
        ChapterVideoVO vo = new ChapterVideoVO();
        BeanUtils.copyProperties(video, vo);
        
        // 处理文件路径，生成完整URL
        if (StringUtils.hasText(video.getFilePath())) {
            vo.setFilePath(fileService.getFullFileUrl(video.getFilePath()));
        }
        
        // 处理封面图路径，生成完整URL
        if (StringUtils.hasText(video.getCoverImage())) {
            vo.setCoverImage(fileService.getFullFileUrl(video.getCoverImage()));
        }
        
        return vo;
    }
    
    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (!StringUtils.hasText(filename)) {
            return "";
        }
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex == -1) ? "" : filename.substring(dotIndex);
    }
    
    /**
     * 获取视频时长（秒）
     */
    private Integer getVideoDuration(File file) throws EncoderException {
        MultimediaObject multimediaObject = new MultimediaObject(file);
        MultimediaInfo info = multimediaObject.getInfo();
        return (int) (info.getDuration() / 1000);
    }
    
    /**
     * 从用户名获取用户ID
     */
    private Integer getUserId(String username) {
        User user = userService.getByAccount(username);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return user.getId();
    }
    
    /**
     * 获取用户姓名
     */
    private String getUserName(Integer userId) {
        User user = userService.getById(userId);
        return user != null ? user.getNickname() : "未知用户";
    }
    
    /**
     * 判断用户是否有指定角色
     */
    private boolean hasRole(UserDetails userDetails, String role) {
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        return authorities.stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + role));
    }
    
    /**
     * 判断是否是学生
     */
    private boolean isStudent(UserDetails userDetails) {
        return hasRole(userDetails, "STUDENT");
    }
} 