// package com.csu.unicorp.service;

// import com.baomidou.mybatisplus.core.metadata.IPage;
// import com.csu.unicorp.dto.OldProjectCreationDTO;
// import com.csu.unicorp.entity.Project;
// import com.csu.unicorp.vo.OldProjectMemberVO;
// import com.csu.unicorp.vo.OldProjectVO;
// import org.springframework.security.core.userdetails.UserDetails;

// import java.util.List;

// /**
//  * 项目服务接口
//  */
// public interface OldProjectService {
    
//     /**
//      * 分页查询项目列表，支持关键词搜索
//      * 
//      * @param page 页码
//      * @param size 每页大小
//      * @param keyword 搜索关键词
//      * @return 项目列表分页结果
//      */
//     IPage<OldProjectVO> getProjectList(
//     int page,
//     int size,
//     String keyword,
//     Integer organizationId,
//     List<String> difficulty,
//     List<String> supportLanguages,
//     List<String> techFields,
//     List<String> programmingLanguages,
//     Integer userId,
//     String needstatus
// );
//     /**
//      * 创建新项目
//      * 
//      * @param projectCreationDTO 项目创建DTO
//      * @param userDetails 当前登录用户
//      * @return 创建成功的项目
//      */
//     OldProjectVO createProject(OldProjectCreationDTO projectCreationDTO, UserDetails userDetails);
    
//     /**
//      * 根据ID获取项目详情
//      * 
//      * @param id 项目ID
//      * @return 项目详情
//      */
//     OldProjectVO getProjectById(Integer id);
    
//     /**
//      * 更新项目信息
//      * 
//      * @param id 项目ID
//      * @param projectCreationDTO 项目更新DTO
//      * @param userDetails 当前登录用户
//      * @return 更新后的项目
//      */
//     OldProjectVO updateProject(Integer id, OldProjectCreationDTO projectCreationDTO, UserDetails userDetails);
    
//     /**
//      * 删除项目
//      * 
//      * @param id 项目ID
//      * @param userDetails 当前登录用户
//      */
//     void deleteProject(Integer id, UserDetails userDetails);
    
//     /**
//      * 检查用户是否有权限操作项目
//      * 
//      * @param project 项目实体
//      * @param userDetails 当前登录用户
//      * @return 是否有权限
//      */
//     boolean hasProjectPermission(Project project, UserDetails userDetails);
    
//     /**
//      * 将项目实体转换为VO
//      * 
//      * @param project 项目实体
//      * @return 项目VO
//      */
//     OldProjectVO convertToVO(Project project);

//     void removeProjectMember(Integer projectId,Integer memberId);


    
// // ... 其他代码 ...
//     List<OldProjectMemberVO> getProjectMembers(Integer projectId);
// } 