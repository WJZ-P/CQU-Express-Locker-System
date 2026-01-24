package com.cqu.locker.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cqu.locker.entity.SysCourier;
import com.cqu.locker.entity.SysUser;
import com.cqu.locker.entity.dto.*;
import com.cqu.locker.mapper.SysCourierMapper;
import com.cqu.locker.mapper.SysUserMapper;
import com.cqu.locker.service.AuthService;
import com.cqu.locker.utils.JwtUtil;
import com.cqu.locker.utils.VerifyCodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 认证服务实现类
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {
    
    @Autowired
    private SysUserMapper userMapper;
    
    @Autowired
    private SysCourierMapper courierMapper;
    
    @Autowired
    private VerifyCodeUtil verifyCodeUtil;
    
    @Override
    public LoginResponse login(LoginRequest request) {
        // 查询用户
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getPhone, request.getPhone());
        SysUser user = userMapper.selectOne(wrapper);
        
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // Web端只允许管理员登录（role=0）
        if (user.getRole() != 0) {
            throw new RuntimeException("权限不足，Web管理平台仅限管理员登录");
        }
        
        // 验证密码（使用MD5加密）
        String encryptedPassword = DigestUtil.md5Hex(request.getPassword());
        if (!user.getPassword().equals(encryptedPassword)) {
            throw new RuntimeException("密码错误");
        }
        
        // 根据角色确定用户类型
        String userType;
        switch (user.getRole()) {
            case 0:
                userType = "admin";
                break;
            case 1:
                userType = "user";
                break;
            case 2:
                userType = "courier";
                break;
            default:
                userType = "user";
        }
        
        // 生成token
        String token = JwtUtil.generateToken(user.getId(), userType);
        
        return LoginResponse.builder()
                .token(token)
                .userId(user.getId().toString())
                .userType(userType)
                .nickname(user.getUsername())
                .build();
    }
    
    @Override
    public void sendCode(SendCodeRequest request) {
        // 检查手机号是否已注册（如果是注册类型）
        if ("register".equals(request.getType())) {
            LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysUser::getPhone, request.getPhone());
            Long count = userMapper.selectCount(wrapper);
            if (count > 0) {
                throw new RuntimeException("该手机号已注册");
            }
        } else if ("reset".equals(request.getType())) {
            // 重置密码时检查用户是否存在
            LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysUser::getPhone, request.getPhone());
            Long count = userMapper.selectCount(wrapper);
            if (count == 0) {
                throw new RuntimeException("该手机号未注册");
            }
        }
        
        // 生成并发送验证码
        verifyCodeUtil.generateCode(request.getPhone(), request.getType());
    }
    
    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        // 验证验证码
        boolean codeValid = verifyCodeUtil.verifyCode(
                request.getPhone(), 
                "register", 
                request.getVerifyCode()
        );
        if (!codeValid) {
            throw new RuntimeException("验证码错误或已过期");
        }
        
        // 检查手机号是否已注册
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getPhone, request.getPhone());
        Long count = userMapper.selectCount(wrapper);
        if (count > 0) {
            throw new RuntimeException("该手机号已注册");
        }
        
        // 创建用户
        SysUser user = new SysUser();
        user.setUsername(request.getPhone()); // 默认用户名为手机号
        user.setPassword(DigestUtil.md5Hex(request.getPassword())); // MD5加密
        user.setPhone(request.getPhone());
        
        // 设置角色
        if ("courier".equals(request.getUserType())) {
            user.setRole(2); // 快递员
        } else {
            user.setRole(1); // 普通用户
        }
        
        user.setCreateTime(LocalDateTime.now());
        userMapper.insert(user);
        
        // 如果是快递员，创建快递员信息
        if ("courier".equals(request.getUserType())) {
            SysCourier courier = new SysCourier();
            courier.setUserId(user.getId());
            courier.setCompany("未设置"); // 默认值
            courier.setCreateTime(LocalDateTime.now());
            courierMapper.insert(courier);
        }
        
        return RegisterResponse.builder()
                .userId(user.getId().toString())
                .build();
    }

    @Override
    public VerifyTokenResponse verifyToken(VerifyTokenRequest request) {
        // 验证token
        boolean valid = JwtUtil.verify(request.getToken());
        
        if (valid) {
            // 解析token获取用户信息
            Long userId = JwtUtil.getUserId(request.getToken());
            String userType = JwtUtil.getUserType(request.getToken());
            
            // 如果无法获取用户信息，视为无效token
            if (userId == null || userType == null) {
                return VerifyTokenResponse.builder()
                        .valid(false)
                        .build();
            }
            
            // 生成新token
            String newToken = JwtUtil.generateToken(userId, userType);
            
            return VerifyTokenResponse.builder()
                    .valid(true)
                    .token(newToken)
                    .expiresIn(7 * 24 * 60 * 60) // 7天，单位：秒
                    .userId(userId.toString())
                    .userType(userType)
                    .build();
        } else {
            return VerifyTokenResponse.builder()
                    .valid(false)
                    .build();
        }
    }
}
