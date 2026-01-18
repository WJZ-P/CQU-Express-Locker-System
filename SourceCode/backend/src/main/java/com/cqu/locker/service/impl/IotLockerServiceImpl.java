package com.cqu.locker.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cqu.locker.entity.IotLocker;
import com.cqu.locker.mapper.IotLockerMapper;
import com.cqu.locker.service.IotLockerService;
import org.springframework.stereotype.Service;

/**
 * 快递柜业务实现类
 */
@Service
public class IotLockerServiceImpl extends ServiceImpl<IotLockerMapper, IotLocker> implements IotLockerService {
}
