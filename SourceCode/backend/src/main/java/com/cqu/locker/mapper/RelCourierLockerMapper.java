package com.cqu.locker.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cqu.locker.entity.RelCourierLocker;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 快递员-快递柜绑定关系Mapper
 */
@Mapper
public interface RelCourierLockerMapper extends BaseMapper<RelCourierLocker> {
    
    /**
     * 获取快递员绑定的快递柜ID列表
     */
    @Select("SELECT locker_id FROM rel_courier_locker WHERE courier_id = #{courierId}")
    List<Long> getLockerIdsByCourierId(@Param("courierId") Long courierId);
}
