package com.cqu.locker.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cqu.locker.entity.SysConfig;
import com.cqu.locker.mapper.SysConfigMapper;
import com.cqu.locker.service.SysConfigService;
import org.springframework.stereotype.Service;

@Service
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfig> implements SysConfigService {
}
