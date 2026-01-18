package com.cqu.locker.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cqu.locker.entity.IotLocker;
import org.apache.ibatis.annotations.Mapper;

/**
 * 快递柜数据访问层接口
 */
@Mapper
public interface IotLockerMapper extends BaseMapper<IotLocker> {
}
