package com.cqu.locker.service.admin.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cqu.locker.entity.IotBox;
import com.cqu.locker.entity.IotLocker;
import com.cqu.locker.entity.dto.admin.*;
import com.cqu.locker.mapper.IotBoxMapper;
import com.cqu.locker.mapper.IotLockerMapper;
import com.cqu.locker.service.admin.AdminCompartmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 管理端格口服务实现类
 */
@Slf4j
@Service
public class AdminCompartmentServiceImpl implements AdminCompartmentService {
    
    @Autowired
    private IotBoxMapper boxMapper;
    
    @Autowired
    private IotLockerMapper lockerMapper;
    
    @Override
    public PageResponse<IotBox> queryCompartments(CompartmentQueryRequest request) {
        LambdaQueryWrapper<IotBox> wrapper = new LambdaQueryWrapper<>();
        
        // 快递柜筛选
        if (request.getLockerId() != null) {
            wrapper.eq(IotBox::getLockerId, request.getLockerId());
        }
        
        // 状态筛选
        if (request.getStatus() != null) {
            wrapper.eq(IotBox::getStatus, request.getStatus());
        }
        
        // 启用状态筛选
        if (request.getEnabled() != null) {
            wrapper.eq(IotBox::getEnabled, request.getEnabled());
        }
        
        // 格口类型筛选
        if (StringUtils.hasText(request.getBoxType())) {
            wrapper.eq(IotBox::getBoxType, request.getBoxType());
        }
        
        // 排序
        wrapper.orderByAsc(IotBox::getLockerId);
        wrapper.orderByAsc(IotBox::getBoxNo);
        
        // 分页查询
        Page<IotBox> page = new Page<>(request.getPage(), request.getPageSize());
        Page<IotBox> result = boxMapper.selectPage(page, wrapper);
        
        return PageResponse.of(result.getRecords(), result.getTotal(), 
                request.getPage(), request.getPageSize());
    }
    
    @Override
    public IotBox getCompartmentById(Long id) {
        IotBox box = boxMapper.selectById(id);
        if (box == null) {
            throw new RuntimeException("格口不存在");
        }
        return box;
    }
    
    @Override
    @Transactional
    public IotBox createCompartment(CompartmentCreateRequest request) {
        // 验证快递柜存在
        IotLocker locker = lockerMapper.selectById(request.getLockerId());
        if (locker == null) {
            throw new RuntimeException("快递柜不存在");
        }
        
        IotBox box = new IotBox();
        box.setLockerId(request.getLockerId());
        box.setBoxNo(request.getBoxNo());
        box.setBoxType(request.getBoxType());
        box.setEnabled(request.getEnabled());
        box.setStatus(0); // 默认空闲
        box.setCreateTime(LocalDateTime.now());
        box.setUpdateTime(LocalDateTime.now());
        
        boxMapper.insert(box);
        
        // 更新快递柜格口数
        updateLockerBoxCount(request.getLockerId());
        
        return box;
    }
    
    @Override
    @Transactional
    public IotBox updateCompartment(Long id, CompartmentUpdateRequest request) {
        IotBox box = boxMapper.selectById(id);
        if (box == null) {
            throw new RuntimeException("格口不存在");
        }
        
        IotBox updateBox = new IotBox();
        updateBox.setId(id);
        
        if (StringUtils.hasText(request.getBoxNo())) {
            updateBox.setBoxNo(request.getBoxNo());
        }
        if (StringUtils.hasText(request.getBoxType())) {
            updateBox.setBoxType(request.getBoxType());
        }
        if (request.getEnabled() != null) {
            updateBox.setEnabled(request.getEnabled());
        }
        if (request.getStatus() != null) {
            updateBox.setStatus(request.getStatus());
            // 如果恢复正常状态，清除故障信息
            if (request.getStatus() != 2) {
                updateBox.setFaultReason(null);
                updateBox.setFaultTime(null);
            }
        }
        if (request.getFaultReason() != null) {
            updateBox.setFaultReason(request.getFaultReason());
        }
        updateBox.setUpdateTime(LocalDateTime.now());
        
        boxMapper.updateById(updateBox);
        
        // 更新快递柜可用格口数
        updateLockerBoxCount(box.getLockerId());
        
        return getCompartmentById(id);
    }
    
    @Override
    @Transactional
    public void deleteCompartment(Long id) {
        IotBox box = boxMapper.selectById(id);
        if (box == null) {
            throw new RuntimeException("格口不存在");
        }
        
        // 检查是否有存件
        if (box.getStatus() != null && box.getStatus() == 1) {
            throw new RuntimeException("该格口有未取件的快递，无法删除");
        }
        
        Long lockerId = box.getLockerId();
        boxMapper.deleteById(id);
        
        // 更新快递柜格口数
        updateLockerBoxCount(lockerId);
    }
    
    @Override
    @Transactional
    public void updateCompartmentEnabled(Long id, Integer enabled) {
        IotBox box = boxMapper.selectById(id);
        if (box == null) {
            throw new RuntimeException("格口不存在");
        }
        
        IotBox updateBox = new IotBox();
        updateBox.setId(id);
        updateBox.setEnabled(enabled);
        updateBox.setUpdateTime(LocalDateTime.now());
        boxMapper.updateById(updateBox);
        
        // 更新快递柜可用格口数
        updateLockerBoxCount(box.getLockerId());
    }
    
    @Override
    public void openDoor(Long id) {
        IotBox box = boxMapper.selectById(id);
        if (box == null) {
            throw new RuntimeException("格口不存在");
        }
        
        // TODO: 调用IoT设备开门接口
        log.info("远程开门: 格口ID={}", id);
    }
    
    @Override
    @Transactional
    public void markFault(Long id, String reason) {
        IotBox box = boxMapper.selectById(id);
        if (box == null) {
            throw new RuntimeException("格口不存在");
        }
        
        IotBox updateBox = new IotBox();
        updateBox.setId(id);
        updateBox.setStatus(2); // 故障状态
        updateBox.setFaultReason(reason);
        updateBox.setFaultTime(LocalDateTime.now());
        updateBox.setUpdateTime(LocalDateTime.now());
        boxMapper.updateById(updateBox);
        
        // 更新快递柜可用格口数
        updateLockerBoxCount(box.getLockerId());
    }
    
    @Override
    @Transactional
    public void clearFault(Long id) {
        IotBox box = boxMapper.selectById(id);
        if (box == null) {
            throw new RuntimeException("格口不存在");
        }
        
        IotBox updateBox = new IotBox();
        updateBox.setId(id);
        updateBox.setStatus(0); // 空闲状态
        updateBox.setFaultReason(null);
        updateBox.setFaultTime(null);
        updateBox.setUpdateTime(LocalDateTime.now());
        boxMapper.updateById(updateBox);
        
        // 更新快递柜可用格口数
        updateLockerBoxCount(box.getLockerId());
    }
    
    /**
     * 更新快递柜格口统计数
     */
    private void updateLockerBoxCount(Long lockerId) {
        // 统计总格口数
        LambdaQueryWrapper<IotBox> totalWrapper = new LambdaQueryWrapper<>();
        totalWrapper.eq(IotBox::getLockerId, lockerId);
        long totalBox = boxMapper.selectCount(totalWrapper);
        
        // 统计可用格口数（启用且空闲）
        LambdaQueryWrapper<IotBox> availableWrapper = new LambdaQueryWrapper<>();
        availableWrapper.eq(IotBox::getLockerId, lockerId);
        availableWrapper.eq(IotBox::getEnabled, 1);
        availableWrapper.eq(IotBox::getStatus, 0);
        long availableBox = boxMapper.selectCount(availableWrapper);
        
        // 更新快递柜
        IotLocker updateLocker = new IotLocker();
        updateLocker.setId(lockerId);
        updateLocker.setTotalBox((int) totalBox);
        updateLocker.setAvailableBox((int) availableBox);
        updateLocker.setUpdateTime(LocalDateTime.now());
        lockerMapper.updateById(updateLocker);
    }
}
