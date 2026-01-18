package com.cqu.locker;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 快递柜系统后端启动类
 */
@SpringBootApplication
@MapperScan("com.cqu.locker.mapper")
public class LockerApplication {
    public static void main(String[] args) {
        SpringApplication.run(LockerApplication.class, args);
    }
}
