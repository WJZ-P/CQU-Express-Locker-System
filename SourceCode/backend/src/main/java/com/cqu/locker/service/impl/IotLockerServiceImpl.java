package com.cqu.locker.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cqu.locker.entity.IotLocker;
import com.cqu.locker.mapper.IotLockerMapper;
import com.cqu.locker.service.IotLockerService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 快递柜业务实现类
 */
@Service
public class IotLockerServiceImpl extends ServiceImpl<IotLockerMapper, IotLocker> implements IotLockerService {
    
    @Override
    public List<IotLocker> listEnabled() {
        LambdaQueryWrapper<IotLocker> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(IotLocker::getEnabled, 1);
        wrapper.orderByDesc(IotLocker::getCreateTime);
        return this.list(wrapper);
    }
}
