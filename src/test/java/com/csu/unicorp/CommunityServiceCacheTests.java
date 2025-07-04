package com.csu.unicorp;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

import com.csu.unicorp.service.impl.community.CommunityCategoryServiceImplTest;
import com.csu.unicorp.service.impl.community.CommunityCommentServiceImplTest;
import com.csu.unicorp.service.impl.community.CommunityQuestionServiceImplTest;

/**
 * 社区服务缓存测试套件
 */
@Suite
@SelectClasses({
    CommunityQuestionServiceImplTest.class,
    CommunityCategoryServiceImplTest.class,
    CommunityCommentServiceImplTest.class
})
public class CommunityServiceCacheTests {
    // 测试套件入口
} 