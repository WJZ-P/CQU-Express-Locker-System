package com.cqu.locker.service.admin.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cqu.locker.entity.IotLocker;
import com.cqu.locker.entity.dto.admin.*;
import com.cqu.locker.mapper.IotLockerMapper;
import com.cqu.locker.service.admin.AdminLockerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 管理端快递柜服务实现类
 */
@Slf4j
@Service
public class AdminLockerServiceImpl implements AdminLockerService {
    
    @Autowired
    private IotLockerMapper lockerMapper;
    
    @Override
    public PageResponse<IotLocker> queryLockers(LockerQueryRequest request) {
        LambdaQueryWrapper<IotLocker> wrapper = new LambdaQueryWrapper<>();
        
        // 关键词搜索
        if (StringUtils.hasText(request.getKeyword())) {
            wrapper.and(w -> w
                    .like(IotLocker::getLockerNo, request.getKeyword())
                    .or().like(IotLocker::getName, request.getKeyword())
                    .or().like(IotLocker::getLocation, request.getKeyword())
            );
        }
        
        // 在线状态筛选
        if (request.getStatus() != null) {
            wrapper.eq(IotLocker::getStatus, request.getStatus());
        }
        
        // 启用状态筛选
        if (request.getEnabled() != null) {
            wrapper.eq(IotLocker::getEnabled, request.getEnabled());
        }
        
        // 排序
        if (StringUtils.hasText(request.getSortBy())) {
            boolean isAsc = "asc".equalsIgnoreCase(request.getSortOrder());
            switch (request.getSortBy()) {
                case "createTime":
                    wrapper.orderBy(true, isAsc, IotLocker::getCreateTime);
                    break;
                case "status":
                    wrapper.orderBy(true, isAsc, IotLocker::getStatus);
                    break;
                default:
                    wrapper.orderByDesc(IotLocker::getCreateTime);
            }
        } else {
            wrapper.orderByDesc(IotLocker::getCreateTime);
        }
        
        // 分页查询
        Page<IotLocker> page = new Page<>(request.getPage(), request.getPageSize());
        Page<IotLocker> result = lockerMapper.selectPage(page, wrapper);
        
        return PageResponse.of(result.getRecords(), result.getTotal(), 
                request.getPage(), request.getPageSize());
    }
    
    @Override
    public IotLocker getLockerById(Long id) {
        IotLocker locker = lockerMapper.selectById(id);
        if (locker == null) {
            throw new RuntimeException("快递柜不存在");
        }
        return locker;
    }
    
    @Override
    @Transactional
    public IotLocker createLocker(LockerCreateRequest request) {
        // 检查编号是否已存在
        LambdaQueryWrapper<IotLocker> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(IotLocker::getLockerNo, request.getLockerNo());
        if (lockerMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("该快递柜编号已存在");
        }
        
        IotLocker locker = new IotLocker();
        locker.setLockerNo(request.getLockerNo());
        locker.setName(request.getName());
        locker.setLocation(request.getLocation());
        locker.setTotalBox(request.getTotalBox());
        locker.setAvailableBox(request.getTotalBox());
        locker.setEnabled(request.getEnabled());
        locker.setStatus(0); // 默认离线
        locker.setRemark(request.getRemark());
        locker.setCreateTime(LocalDateTime.now());
        locker.setUpdateTime(LocalDateTime.now());
        
        lockerMapper.insert(locker);
        return locker;
    }
    
    @Override
    @Transactional
    public IotLocker updateLocker(Long id, LockerUpdateRequest request) {
        IotLocker locker = lockerMapper.selectById(id);
        if (locker == null) {
            throw new RuntimeException("快递柜不存在");
        }
        
        // 检查编号是否被其他快递柜使用
        if (StringUtils.hasText(request.getLockerNo()) && !request.getLockerNo().equals(locker.getLockerNo())) {
            LambdaQueryWrapper<IotLocker> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(IotLocker::getLockerNo, request.getLockerNo());
            wrapper.ne(IotLocker::getId, id);
            if (lockerMapper.selectCount(wrapper) > 0) {
                throw new RuntimeException("该快递柜编号已被使用");
            }
        }
        
        IotLocker updateLocker = new IotLocker();
        updateLocker.setId(id);
        
        if (StringUtils.hasText(request.getLockerNo())) {
            updateLocker.setLockerNo(request.getLockerNo());
        }
        if (request.getName() != null) {
            updateLocker.setName(request.getName());
        }
        if (StringUtils.hasText(request.getLocation())) {
            updateLocker.setLocation(request.getLocation());
        }
        if (request.getEnabled() != null) {
            updateLocker.setEnabled(request.getEnabled());
        }
        if (request.getRemark() != null) {
            updateLocker.setRemark(request.getRemark());
        }
        updateLocker.setUpdateTime(LocalDateTime.now());
        
        lockerMapper.updateById(updateLocker);
        
        return getLockerById(id);
    }
    
    @Override
    @Transactional
    public void deleteLocker(Long id) {
        IotLocker locker = lockerMapper.selectById(id);
        if (locker == null) {
            throw new RuntimeException("快递柜不存在");
        }
        
        // TODO: 检查是否有未取件的快递
        
        lockerMapper.deleteById(id);
    }
    
    @Override
    @Transactional
    public void updateLockerEnabled(Long id, Integer enabled) {
        IotLocker locker = lockerMapper.selectById(id);
        if (locker == null) {
            throw new RuntimeException("快递柜不存在");
        }
        
        IotLocker updateLocker = new IotLocker();
        updateLocker.setId(id);
        updateLocker.setEnabled(enabled);
        updateLocker.setUpdateTime(LocalDateTime.now());
        lockerMapper.updateById(updateLocker);
    }
}
