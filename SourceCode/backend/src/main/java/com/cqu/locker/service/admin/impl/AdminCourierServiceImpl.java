package com.cqu.locker.service.admin.impl;

import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cqu.locker.entity.SysCourier;
import com.cqu.locker.entity.SysUser;
import com.cqu.locker.entity.dto.admin.*;
import com.cqu.locker.mapper.SysCourierMapper;
import com.cqu.locker.mapper.SysUserMapper;
import com.cqu.locker.service.admin.AdminCourierService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 管理端快递员服务实现类
 */
@Slf4j
@Service
public class AdminCourierServiceImpl implements AdminCourierService {
    
    private static final String DEFAULT_PASSWORD = "123456";
    
    @Autowired
    private SysCourierMapper courierMapper;
    
    @Autowired
    private SysUserMapper userMapper;
    
    @Override
    public PageResponse<SysCourier> queryCouriers(CourierQueryRequest request) {
        LambdaQueryWrapper<SysCourier> wrapper = new LambdaQueryWrapper<>();
        
        // 关键词搜索
        if (StringUtils.hasText(request.getKeyword())) {
            wrapper.and(w -> w
                    .like(SysCourier::getName, request.getKeyword())
                    .or().like(SysCourier::getPhone, request.getKeyword())
                    .or().like(SysCourier::getCompany, request.getKeyword())
            );
        }
        
        // 状态筛选
        if (request.getStatus() != null) {
            wrapper.eq(SysCourier::getStatus, request.getStatus());
        }
        
        // 公司筛选
        if (StringUtils.hasText(request.getCompany())) {
            wrapper.eq(SysCourier::getCompany, request.getCompany());
        }
        
        // 排序
        if (StringUtils.hasText(request.getSortBy())) {
            boolean isAsc = "asc".equalsIgnoreCase(request.getSortOrder());
            switch (request.getSortBy()) {
                case "createTime":
                    wrapper.orderBy(true, isAsc, SysCourier::getCreateTime);
                    break;
                default:
                    wrapper.orderByDesc(SysCourier::getCreateTime);
            }
        } else {
            wrapper.orderByDesc(SysCourier::getCreateTime);
        }
        
        // 分页查询
        Page<SysCourier> page = new Page<>(request.getPage(), request.getPageSize());
        Page<SysCourier> result = courierMapper.selectPage(page, wrapper);
        
        return PageResponse.of(result.getRecords(), result.getTotal(), 
                request.getPage(), request.getPageSize());
    }
    
    @Override
    public SysCourier getCourierById(Long id) {
        SysCourier courier = courierMapper.selectById(id);
        if (courier == null) {
            throw new RuntimeException("快递员不存在");
        }
        return courier;
    }
    
    @Override
    @Transactional
    public SysCourier createCourier(CourierCreateRequest request) {
        // 检查手机号是否已存在
        LambdaQueryWrapper<SysUser> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(SysUser::getPhone, request.getPhone());
        if (userMapper.selectCount(userWrapper) > 0) {
            throw new RuntimeException("该手机号已被注册");
        }
        
        // 1. 创建用户账号
        SysUser user = new SysUser();
        user.setUsername(request.getName());
        user.setNickname(request.getName());
        user.setPhone(request.getPhone());
        user.setRole(2); // 快递员角色
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        
        String password = StringUtils.hasText(request.getPassword()) 
                ? request.getPassword() : DEFAULT_PASSWORD;
        user.setPassword(DigestUtil.md5Hex(password));
        
        userMapper.insert(user);
        
        // 2. 创建快递员信息
        SysCourier courier = new SysCourier();
        courier.setUserId(user.getId());
        courier.setName(request.getName());
        courier.setPhone(request.getPhone());
        courier.setCompany(request.getCompany());
        courier.setStatus(request.getStatus());
        courier.setCreateTime(LocalDateTime.now());
        courier.setUpdateTime(LocalDateTime.now());
        
        courierMapper.insert(courier);
        
        return courier;
    }
    
    @Override
    @Transactional
    public SysCourier updateCourier(Long id, CourierUpdateRequest request) {
        SysCourier courier = courierMapper.selectById(id);
        if (courier == null) {
            throw new RuntimeException("快递员不存在");
        }
        
        // 检查手机号是否已被其他用户使用
        if (StringUtils.hasText(request.getPhone()) && !request.getPhone().equals(courier.getPhone())) {
            LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysUser::getPhone, request.getPhone());
            wrapper.ne(SysUser::getId, courier.getUserId());
            if (userMapper.selectCount(wrapper) > 0) {
                throw new RuntimeException("该手机号已被其他用户使用");
            }
        }
        
        // 更新快递员信息
        SysCourier updateCourier = new SysCourier();
        updateCourier.setId(id);
        
        if (StringUtils.hasText(request.getName())) {
            updateCourier.setName(request.getName());
        }
        if (StringUtils.hasText(request.getPhone())) {
            updateCourier.setPhone(request.getPhone());
        }
        if (StringUtils.hasText(request.getCompany())) {
            updateCourier.setCompany(request.getCompany());
        }
        if (request.getStatus() != null) {
            updateCourier.setStatus(request.getStatus());
        }
        updateCourier.setUpdateTime(LocalDateTime.now());
        
        courierMapper.updateById(updateCourier);
        
        // 同步更新用户信息
        if (courier.getUserId() != null) {
            SysUser updateUser = new SysUser();
            updateUser.setId(courier.getUserId());
            if (StringUtils.hasText(request.getName())) {
                updateUser.setUsername(request.getName());
                updateUser.setNickname(request.getName());
            }
            if (StringUtils.hasText(request.getPhone())) {
                updateUser.setPhone(request.getPhone());
            }
            if (StringUtils.hasText(request.getPassword())) {
                updateUser.setPassword(DigestUtil.md5Hex(request.getPassword()));
            }
            // 同步状态：快递员离职时禁用用户账号
            if (request.getStatus() != null) {
                updateUser.setStatus(request.getStatus());
            }
            updateUser.setUpdateTime(LocalDateTime.now());
            userMapper.updateById(updateUser);
        }
        
        return getCourierById(id);
    }
    
    @Override
    @Transactional
    public void deleteCourier(Long id) {
        SysCourier courier = courierMapper.selectById(id);
        if (courier == null) {
            throw new RuntimeException("快递员不存在");
        }
        
        // 删除快递员信息
        courierMapper.deleteById(id);
        
        // 同时删除关联的用户账号
        if (courier.getUserId() != null) {
            userMapper.deleteById(courier.getUserId());
        }
    }
    
    @Override
    @Transactional
    public void updateCourierStatus(Long id, Integer status) {
        SysCourier courier = courierMapper.selectById(id);
        if (courier == null) {
            throw new RuntimeException("快递员不存在");
        }
        
        // 更新快递员状态
        SysCourier updateCourier = new SysCourier();
        updateCourier.setId(id);
        updateCourier.setStatus(status);
        updateCourier.setUpdateTime(LocalDateTime.now());
        courierMapper.updateById(updateCourier);
        
        // 同步更新用户状态
        if (courier.getUserId() != null) {
            SysUser updateUser = new SysUser();
            updateUser.setId(courier.getUserId());
            updateUser.setStatus(status);
            updateUser.setUpdateTime(LocalDateTime.now());
            userMapper.updateById(updateUser);
        }
    }
}
