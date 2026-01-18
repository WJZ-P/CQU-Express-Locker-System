package com.cqu.locker.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cqu.locker.entity.BusOrder;
import com.cqu.locker.mapper.BusOrderMapper;
import com.cqu.locker.service.BusOrderService;
import org.springframework.stereotype.Service;

@Service
public class BusOrderServiceImpl extends ServiceImpl<BusOrderMapper, BusOrder> implements BusOrderService {
}
