package com.cqu.locker.service;

import com.cqu.locker.entity.dto.UpdateProfileRequest;
import com.cqu.locker.entity.dto.UserProfileResponse;

/**
 * 用户服务接口
 */
public interface UserService {
    
    /**
     * 获取用户信息
     * @param userId 用户ID
     * @return 用户信息
     */
    UserProfileResponse getUserProfile(Long userId);
    
    /**
     * 更新用户信息
     * @param userId 用户ID
     * @param request 更新请求
     */
    void updateUserProfile(Long userId, UpdateProfileRequest request);
}
