package com.cqu.locker.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cqu.locker.entity.RelUserLocker;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户-快递柜绑定关系Mapper
 */
@Mapper
public interface RelUserLockerMapper extends BaseMapper<RelUserLocker> {
}
