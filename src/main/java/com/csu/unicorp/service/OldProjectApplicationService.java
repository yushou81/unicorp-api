// package com.csu.unicorp.service;

// import com.baomidou.mybatisplus.core.metadata.IPage;
// import com.csu.unicorp.dto.OldProjectApplicationCreationDTO;
// import com.csu.unicorp.dto.OldProjectApplicationStatusUpdateDTO;
// import com.csu.unicorp.vo.MyProjectApplicationDetailVO;
// import com.csu.unicorp.vo.OldProjectApplicationDetailVO;
// import com.csu.unicorp.vo.OldProjectVO;
// import org.springframework.security.core.userdetails.UserDetails;

// import java.util.List;

// /**
//  * 项目申请服务接口
//  */
// public interface OldProjectApplicationService {
    
//     /**
//      * 学生申请加入项目
//      * 
//      * @param projectId 项目ID
//      * @param dto 申请创建DTO
//      * @param userDetails 当前登录用户
//      * @return 申请结果
//      */
//     OldProjectApplicationDetailVO applyForProject(Integer projectId, OldProjectApplicationCreationDTO dto, UserDetails userDetails);
    
//     /**
//      * 获取项目的申请列表
//      * 
//      * @param projectId 项目ID
//      * @param userDetails 当前登录用户
//      * @return 申请列表
//      */
//     List<OldProjectApplicationDetailVO> getProjectApplications(Integer projectId, UserDetails userDetails);
    
//     /**
//      * 更新项目申请状态
//      * 
//      * @param applicationId 申请ID
//      * @param dto 状态更新DTO
//      * @param userDetails 当前登录用户
//      * @return 更新后的申请
//      */
//     OldProjectApplicationDetailVO updateApplicationStatus(Integer applicationId, OldProjectApplicationStatusUpdateDTO dto, UserDetails userDetails);
    
//     /**
//      * 获取当前学生的项目申请列表
//      * 
//      * @param page 页码
//      * @param size 每页大小
//      * @param userDetails 当前登录用户
//      * @return 申请列表
//      */
//     IPage<MyProjectApplicationDetailVO> getMyProjectApplications(
//         int page,
//         int size,
//         String keyword,
//         Integer userId,
//         List<String> difficulty,
//         List<String> supportLanguages,
//         List<String> techFields,
//         List<String> programmingLanguages
//     );

// } 