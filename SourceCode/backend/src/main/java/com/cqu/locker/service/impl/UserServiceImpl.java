package com.cqu.locker.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cqu.locker.entity.SysUser;
import com.cqu.locker.entity.dto.UpdateProfileRequest;
import com.cqu.locker.entity.dto.UserProfileResponse;
import com.cqu.locker.mapper.SysUserMapper;
import com.cqu.locker.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private SysUserMapper userMapper;
    
    @Override
    public UserProfileResponse getUserProfile(Long userId) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 脱敏手机号
        String maskedPhone = user.getPhone().replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
        
        return UserProfileResponse.builder()
                .userId(user.getId().toString())
                .phone(maskedPhone)
                .nickname(user.getUsername())
                .bindLockers(new ArrayList<>()) // 暂时返回空列表，后续可扩展
                .build();
    }
    
    @Override
    public void updateUserProfile(Long userId, UpdateProfileRequest request) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 更新昵称
        if (request.getNickname() != null && !request.getNickname().isEmpty()) {
            user.setUsername(request.getNickname());
            userMapper.updateById(user);
        }
    }
}
