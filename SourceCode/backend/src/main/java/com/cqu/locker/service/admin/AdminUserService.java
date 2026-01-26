package com.cqu.locker.service.admin;

import com.cqu.locker.entity.SysUser;
import com.cqu.locker.entity.dto.admin.*;

/**
 * 管理端用户服务接口
 */
public interface AdminUserService {
    
    /**
     * 分页查询用户列表
     */
    PageResponse<SysUser> queryUsers(UserQueryRequest request);
    
    /**
     * 获取用户详情
     */
    SysUser getUserById(Long id);
    
    /**
     * 创建用户
     */
    SysUser createUser(UserCreateRequest request);
    
    /**
     * 更新用户
     */
    SysUser updateUser(Long id, UserUpdateRequest request);
    
    /**
     * 删除用户
     */
    void deleteUser(Long id);
    
    /**
     * 更新用户状态
     */
    void updateUserStatus(Long id, Integer status);
    
    /**
     * 重置用户密码
     */
    void resetPassword(Long id, String newPassword);
}
