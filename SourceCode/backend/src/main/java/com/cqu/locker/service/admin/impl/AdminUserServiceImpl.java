package com.cqu.locker.service.admin.impl;

import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cqu.locker.entity.SysUser;
import com.cqu.locker.entity.dto.admin.*;
import com.cqu.locker.mapper.SysUserMapper;
import com.cqu.locker.service.admin.AdminUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 管理端用户服务实现类
 */
@Slf4j
@Service
public class AdminUserServiceImpl implements AdminUserService {
    
    private static final String DEFAULT_PASSWORD = "123456";
    
    @Autowired
    private SysUserMapper userMapper;
    
    @Override
    public PageResponse<SysUser> queryUsers(UserQueryRequest request) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        
        // 关键词搜索
        if (StringUtils.hasText(request.getKeyword())) {
            wrapper.and(w -> w
                    .like(SysUser::getUsername, request.getKeyword())
                    .or().like(SysUser::getNickname, request.getKeyword())
                    .or().like(SysUser::getPhone, request.getKeyword())
            );
        }
        
        // 状态筛选
        if (request.getStatus() != null) {
            wrapper.eq(SysUser::getStatus, request.getStatus());
        }
        
        // 角色筛选
        if (request.getRole() != null) {
            wrapper.eq(SysUser::getRole, request.getRole());
        }
        
        // 排序
        if (StringUtils.hasText(request.getSortBy())) {
            boolean isAsc = "asc".equalsIgnoreCase(request.getSortOrder());
            switch (request.getSortBy()) {
                case "createTime":
                    wrapper.orderBy(true, isAsc, SysUser::getCreateTime);
                    break;
                case "lastLoginTime":
                    wrapper.orderBy(true, isAsc, SysUser::getLastLoginTime);
                    break;
                default:
                    wrapper.orderByDesc(SysUser::getCreateTime);
            }
        } else {
            wrapper.orderByDesc(SysUser::getCreateTime);
        }
        
        // 分页查询
        Page<SysUser> page = new Page<>(request.getPage(), request.getPageSize());
        Page<SysUser> result = userMapper.selectPage(page, wrapper);
        
        // 清除密码信息
        result.getRecords().forEach(user -> user.setPassword(null));
        
        return PageResponse.of(result.getRecords(), result.getTotal(), 
                request.getPage(), request.getPageSize());
    }
    
    @Override
    public SysUser getUserById(Long id) {
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        user.setPassword(null);
        return user;
    }
    
    @Override
    @Transactional
    public SysUser createUser(UserCreateRequest request) {
        // 检查手机号是否已存在
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getPhone, request.getPhone());
        if (userMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("该手机号已被注册");
        }
        
        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setNickname(request.getNickname());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setRole(request.getRole());
        user.setStatus(request.getStatus());
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        
        // 密码加密
        String password = StringUtils.hasText(request.getPassword()) 
                ? request.getPassword() : DEFAULT_PASSWORD;
        user.setPassword(DigestUtil.md5Hex(password));
        
        userMapper.insert(user);
        user.setPassword(null);
        return user;
    }
    
    @Override
    @Transactional
    public SysUser updateUser(Long id, UserUpdateRequest request) {
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 检查手机号是否已被其他用户使用
        if (StringUtils.hasText(request.getPhone()) && !request.getPhone().equals(user.getPhone())) {
            LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysUser::getPhone, request.getPhone());
            wrapper.ne(SysUser::getId, id);
            if (userMapper.selectCount(wrapper) > 0) {
                throw new RuntimeException("该手机号已被其他用户使用");
            }
        }
        
        SysUser updateUser = new SysUser();
        updateUser.setId(id);
        
        if (StringUtils.hasText(request.getUsername())) {
            updateUser.setUsername(request.getUsername());
        }
        if (request.getNickname() != null) {
            updateUser.setNickname(request.getNickname());
        }
        if (StringUtils.hasText(request.getPhone())) {
            updateUser.setPhone(request.getPhone());
        }
        if (request.getEmail() != null) {
            updateUser.setEmail(request.getEmail());
        }
        if (request.getRole() != null) {
            updateUser.setRole(request.getRole());
        }
        if (request.getStatus() != null) {
            updateUser.setStatus(request.getStatus());
        }
        if (StringUtils.hasText(request.getPassword())) {
            updateUser.setPassword(DigestUtil.md5Hex(request.getPassword()));
        }
        updateUser.setUpdateTime(LocalDateTime.now());
        
        userMapper.updateById(updateUser);
        
        return getUserById(id);
    }
    
    @Override
    @Transactional
    public void deleteUser(Long id) {
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 不允许删除管理员
        if (user.getRole() != null && user.getRole() == 0) {
            throw new RuntimeException("不能删除管理员账号");
        }
        
        userMapper.deleteById(id);
    }
    
    @Override
    @Transactional
    public void updateUserStatus(Long id, Integer status) {
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        SysUser updateUser = new SysUser();
        updateUser.setId(id);
        updateUser.setStatus(status);
        updateUser.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(updateUser);
    }
    
    @Override
    @Transactional
    public void resetPassword(Long id, String newPassword) {
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        String password = StringUtils.hasText(newPassword) ? newPassword : DEFAULT_PASSWORD;
        
        SysUser updateUser = new SysUser();
        updateUser.setId(id);
        updateUser.setPassword(DigestUtil.md5Hex(password));
        updateUser.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(updateUser);
    }
}
