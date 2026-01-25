CREATE DATABASE IF NOT EXISTS `express_locker` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `express_locker`;

-- 3.1 用户表 (sys_user)
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '加密密码',
  `phone` varchar(20) NOT NULL COMMENT '手机号',
  `role` tinyint(1) NOT NULL COMMENT '角色：0-管理员，1-用户，2-快递员',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 3.2 快递员信息表 (sys_courier)
DROP TABLE IF EXISTS `sys_courier`;
CREATE TABLE `sys_courier` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint(20) NOT NULL COMMENT '关联sys_user.id',
  `company` varchar(50) NOT NULL COMMENT '所属快递公司',
  `employee_id` varchar(50) DEFAULT NULL COMMENT '工号',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='快递员信息表';

-- 3.3 快递柜表 (iot_locker)
DROP TABLE IF EXISTS `iot_locker`;
CREATE TABLE `iot_locker` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `serial_no` varchar(50) NOT NULL COMMENT '设备序列号',
  `location` varchar(100) NOT NULL COMMENT '部署地址',
  `ip_address` varchar(50) DEFAULT NULL COMMENT '设备IP地址',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态：0-离线，1-在线',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_serial_no` (`serial_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='快递柜表';

-- 3.4 格口表 (iot_box)
DROP TABLE IF EXISTS `iot_box`;
CREATE TABLE `iot_box` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `locker_id` bigint(20) NOT NULL COMMENT '关联iot_locker.id',
  `box_no` varchar(10) NOT NULL COMMENT '格口编号（如A01）',
  `size` tinyint(1) NOT NULL COMMENT '尺寸：1-小，2-中，3-大',
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态：0-空闲，1-占用，2-故障',
  `is_locked` tinyint(1) NOT NULL DEFAULT '1' COMMENT '门锁状态：0-开，1-关',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_locker_id` (`locker_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='格口表';

-- 3.5 订单表 (bus_order)
DROP TABLE IF EXISTS `bus_order`;
CREATE TABLE `bus_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `order_no` varchar(50) NOT NULL COMMENT '业务订单号',
  `type` tinyint(1) NOT NULL COMMENT '类型：1-投递，2-寄件，3-寄存',
  `tracking_no` varchar(50) DEFAULT NULL COMMENT '快递单号',
  `pickup_code` varchar(10) DEFAULT NULL COMMENT '取件码',
  `box_id` bigint(20) NOT NULL COMMENT '关联格口ID',
  `courier_id` bigint(20) DEFAULT NULL COMMENT '投递员ID',
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户ID',
  `receiver_phone` varchar(20) NOT NULL COMMENT '收件人手机',
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态：0-待取，1-已取，2-超时',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '投递时间',
  `finish_time` datetime DEFAULT NULL COMMENT '取件时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_pickup_code` (`pickup_code`),
  KEY `idx_receiver_phone` (`receiver_phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 3.6 操作日志表 (sys_log)
DROP TABLE IF EXISTS `sys_log`;
CREATE TABLE `sys_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint(20) DEFAULT NULL COMMENT '操作人ID',
  `operation` varchar(50) NOT NULL COMMENT '操作类型',
  `content` text COMMENT '详细内容',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- 3.7 系统配置表 (sys_config)
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `param_key` varchar(50) NOT NULL COMMENT '参数键名',
  `param_value` varchar(255) NOT NULL COMMENT '参数值',
  `description` varchar(255) DEFAULT NULL COMMENT '参数说明',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_param_key` (`param_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

-- 3.8 电量统计表 (iot_power_stats)
DROP TABLE IF EXISTS `iot_power_stats`;
CREATE TABLE `iot_power_stats` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `locker_id` bigint(20) NOT NULL COMMENT '关联快递柜',
  `power_usage` decimal(10,2) NOT NULL COMMENT '用电量(kWh)',
  `record_date` date NOT NULL COMMENT '记录日期',
  PRIMARY KEY (`id`),
  KEY `idx_record_date` (`record_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='电量统计表';

-- 初始数据
-- 管理员账号：手机号 13800138000，密码 123456（已MD5加密）
INSERT INTO `sys_user` (`username`, `password`, `phone`, `role`) VALUES ('admin', 'e10adc3949ba59abbe56e057f20f883e', '13800138000', 0);
-- 测试用户：手机号 13800138001，密码 123456
INSERT INTO `sys_user` (`username`, `password`, `phone`, `role`) VALUES ('testuser', 'e10adc3949ba59abbe56e057f20f883e', '13800138001', 1);
-- 测试快递员：手机号 13800138002，密码 123456
INSERT INTO `sys_user` (`username`, `password`, `phone`, `role`) VALUES ('testcourier', 'e10adc3949ba59abbe56e057f20f883e', '13800138002', 2);

-- 快递员信息
INSERT INTO `sys_courier` (`user_id`, `company`, `employee_id`) VALUES (3, '顺丰速运', 'SF123456');

-- 测试快递柜和格口数据
INSERT INTO `iot_locker` (`serial_no`, `location`, `ip_address`, `status`) VALUES ('LOCKER001', '测试位置1', '192.168.1.100', 1);
INSERT INTO `iot_box` (`locker_id`, `box_no`, `size`, `status`, `is_locked`) VALUES (1, 'A01', 1, 0, 1);
INSERT INTO `iot_box` (`locker_id`, `box_no`, `size`, `status`, `is_locked`) VALUES (1, 'A02', 2, 0, 1);
INSERT INTO `iot_box` (`locker_id`, `box_no`, `size`, `status`, `is_locked`) VALUES (1, 'A03', 3, 0, 1);

INSERT INTO `sys_config` (`param_key`, `param_value`, `description`) VALUES ('sys_name', '快递柜综合应用系统', '系统名称'), ('pickup_timeout', '24', '取件超时时间(h)');
