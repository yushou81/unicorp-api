package com.csu.unicorp.service;

import com.csu.unicorp.vo.ProjectLogVO;
import java.util.List;

/**
 * 项目操作日志服务接口
 */
public interface ProjectLogService {
    List<ProjectLogVO> getProjectLogs(Integer projectId);
}
