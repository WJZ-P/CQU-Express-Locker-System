package com.cqu.locker.service;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cqu.locker.entity.SysUser;

public interface SysUserService extends IService<SysUser> {
    SysUser login(String username, String password);
}
