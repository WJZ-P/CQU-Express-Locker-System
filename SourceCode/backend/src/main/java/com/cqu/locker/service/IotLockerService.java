package com.cqu.locker.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cqu.locker.entity.IotLocker;

import java.util.List;

/**
 * 快递柜业务接口
 */
public interface IotLockerService extends IService<IotLocker> {
    
    /**
     * 获取启用的快递柜列表（供移动端使用）
     */
    List<IotLocker> listEnabled();
}
