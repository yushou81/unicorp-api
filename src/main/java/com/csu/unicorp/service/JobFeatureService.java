package com.csu.unicorp.service;

/**
 * 岗位特征服务接口
 */
public interface JobFeatureService {
    
    /**
     * 为岗位生成特征
     * @param jobId 岗位ID
     * @return 是否成功
     */
    boolean generateJobFeature(Integer jobId);
} 