CREATE DATABASE IF NOT EXISTS `express_locker` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `express_locker`;

-- =====================================================
-- 快递柜综合应用系统 数据库设计
-- 版本: v2.0.0
-- 更新日期: 2026-01-26
-- 说明: 支持Web管理端功能
-- =====================================================

-- 3.1 用户表 (sys_user)
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '加密密码',
  `phone` varchar(20) NOT NULL COMMENT '手机号',
  `nickname` varchar(50) DEFAULT NULL COMMENT '用户昵称',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `role` tinyint(1) NOT NULL COMMENT '角色：0-管理员，1-用户，2-快递员',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-正常',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 3.2 快递员信息表 (sys_courier)
DROP TABLE IF EXISTS `sys_courier`;
CREATE TABLE `sys_courier` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint(20) NOT NULL COMMENT '关联sys_user.id',
  `name` varchar(50) DEFAULT NULL COMMENT '快递员姓名',
  `company` varchar(50) NOT NULL COMMENT '所属快递公司',
  `employee_id` varchar(50) DEFAULT NULL COMMENT '工号',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态：0-离职，1-在职',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='快递员信息表';

-- 3.3 快递柜表 (iot_locker)
DROP TABLE IF EXISTS `iot_locker`;
CREATE TABLE `iot_locker` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `serial_no` varchar(50) NOT NULL COMMENT '设备序列号',
  `name` varchar(50) DEFAULT NULL COMMENT '快递柜名称（如A区1号柜）',
  `location` varchar(100) NOT NULL COMMENT '部署地址',
  `ip_address` varchar(50) DEFAULT NULL COMMENT '设备IP地址',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '在线状态：0-离线，1-在线',
  `enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用：0-禁用，1-启用',
  `temperature` decimal(5,2) DEFAULT NULL COMMENT '温度(°C)',
  `humidity` decimal(5,2) DEFAULT NULL COMMENT '湿度(%)',
  `last_online_time` datetime DEFAULT NULL COMMENT '最后在线时间',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
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
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '业务状态：0-空闲，1-占用，2-故障',
  `enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用：0-禁用，1-启用',
  `is_locked` tinyint(1) NOT NULL DEFAULT '1' COMMENT '门锁状态：0-开，1-关',
  `pressure` decimal(5,2) DEFAULT NULL COMMENT '压力传感器数值(kg)',
  `temperature` decimal(5,2) DEFAULT NULL COMMENT '格口内温度(°C)',
  `humidity` decimal(5,2) DEFAULT NULL COMMENT '格口内湿度(%)',
  `fault_reason` varchar(100) DEFAULT NULL COMMENT '故障原因',
  `fault_time` datetime DEFAULT NULL COMMENT '故障时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
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
  `company` varchar(50) DEFAULT NULL COMMENT '快递公司',
  `pickup_code` varchar(10) DEFAULT NULL COMMENT '取件码',
  `box_id` bigint(20) NOT NULL COMMENT '关联格口ID',
  `courier_id` bigint(20) DEFAULT NULL COMMENT '投递员ID',
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户ID',
  `sender_name` varchar(50) DEFAULT NULL COMMENT '寄件人姓名',
  `sender_phone` varchar(20) DEFAULT NULL COMMENT '寄件人手机',
  `receiver_name` varchar(50) DEFAULT NULL COMMENT '收件人姓名',
  `receiver_phone` varchar(20) NOT NULL COMMENT '收件人手机',
  `item_description` varchar(100) DEFAULT NULL COMMENT '物品描述（寄存用）',
  `duration` int DEFAULT NULL COMMENT '寄存时长(小时)',
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
  `fee` decimal(10,2) DEFAULT NULL COMMENT '费用',
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态：0-待取，1-已取，2-超时，3-已退回',
  `notification_sent` tinyint(1) DEFAULT '0' COMMENT '是否已发送通知：0-否，1-是',
  `notification_time` datetime DEFAULT NULL COMMENT '通知发送时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '投递/创建时间',
  `finish_time` datetime DEFAULT NULL COMMENT '取件/完成时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_pickup_code` (`pickup_code`),
  KEY `idx_receiver_phone` (`receiver_phone`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_courier_id` (`courier_id`),
  KEY `idx_box_id` (`box_id`),
  KEY `idx_type` (`type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 3.6 操作日志表 (sys_log)
DROP TABLE IF EXISTS `sys_log`;
CREATE TABLE `sys_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint(20) DEFAULT NULL COMMENT '操作人ID',
  `user_type` tinyint(1) DEFAULT NULL COMMENT '操作人类型：0-管理员，1-用户，2-快递员',
  `operation` varchar(50) NOT NULL COMMENT '操作类型',
  `module` varchar(50) DEFAULT NULL COMMENT '操作模块',
  `content` text COMMENT '详细内容',
  `ip_address` varchar(50) DEFAULT NULL COMMENT 'IP地址',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- 3.7 系统配置表 (sys_config)
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `param_key` varchar(50) NOT NULL COMMENT '参数键名',
  `param_value` varchar(255) NOT NULL COMMENT '参数值',
  `description` varchar(255) DEFAULT NULL COMMENT '参数说明',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
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
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_locker_date` (`locker_id`, `record_date`),
  KEY `idx_record_date` (`record_date`),
  KEY `idx_locker_id` (`locker_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='电量统计表';



-- =====================================================
-- 初始数据
-- =====================================================

-- 用户数据
-- 管理员账号：手机号 13800138000，密码 123456（已MD5加密）
INSERT INTO `sys_user` (`username`, `password`, `phone`, `nickname`, `role`, `status`) VALUES 
('admin', 'e10adc3949ba59abbe56e057f20f883e', '13800138000', '超级管理员', 0, 1);
-- 测试用户：手机号 13800138001，密码 123456
INSERT INTO `sys_user` (`username`, `password`, `phone`, `nickname`, `role`, `status`) VALUES 
('testuser', 'e10adc3949ba59abbe56e057f20f883e', '13800138001', '测试用户', 1, 1);
-- 测试快递员：手机号 13800138002，密码 123456
INSERT INTO `sys_user` (`username`, `password`, `phone`, `nickname`, `role`, `status`) VALUES 
('testcourier', 'e10adc3949ba59abbe56e057f20f883e', '13800138002', '测试快递员', 2, 1);

-- 更多测试用户
INSERT INTO `sys_user` (`username`, `password`, `phone`, `nickname`, `role`, `status`) VALUES 
('user_liming', 'e10adc3949ba59abbe56e057f20f883e', '13812341234', '李明', 1, 1),
('user_wangfang', 'e10adc3949ba59abbe56e057f20f883e', '13956785678', '王芳', 1, 1),
('courier_zhangsan', 'e10adc3949ba59abbe56e057f20f883e', '13711111111', '张三', 2, 1),
('courier_lisi', 'e10adc3949ba59abbe56e057f20f883e', '13722222222', '李四', 2, 1);

-- 快递员信息
INSERT INTO `sys_courier` (`user_id`, `name`, `company`, `employee_id`, `status`) VALUES 
(3, '测试快递员', '顺丰速运', 'SF123456', 1),
(6, '张三', '顺丰速运', 'SF001', 1),
(7, '李四', '圆通速递', 'YT001', 1);

-- 快递柜数据
INSERT INTO `iot_locker` (`serial_no`, `name`, `location`, `ip_address`, `status`, `enabled`, `temperature`, `humidity`) VALUES 
('LOCKER001', 'A区1号柜', '重庆大学A区第一教学楼', '192.168.1.100', 1, 1, 25.5, 45.0),
('LOCKER002', 'A区2号柜', '重庆大学A区学生宿舍', '192.168.1.101', 1, 1, 26.0, 48.0),
('LOCKER003', 'B区1号柜', '重庆大学B区图书馆', '192.168.1.102', 1, 1, 24.5, 42.0);

-- 快递柜1的格口：小仓(size=1) 8个、中仓(size=2) 8个、大仓(size=3) 4个
-- 小仓 (size=1)
INSERT INTO `iot_box` (`locker_id`, `box_no`, `size`, `status`, `enabled`, `is_locked`) VALUES 
(1, 'A01', 1, 0, 1, 1),
(1, 'A02', 1, 0, 1, 1),
(1, 'A03', 1, 0, 1, 1),
(1, 'A04', 1, 0, 1, 1),
(1, 'A05', 1, 0, 1, 1),
(1, 'A06', 1, 0, 1, 1),
(1, 'A07', 1, 0, 1, 1),
(1, 'A08', 1, 0, 1, 1);

-- 中仓 (size=2)
INSERT INTO `iot_box` (`locker_id`, `box_no`, `size`, `status`, `enabled`, `is_locked`) VALUES 
(1, 'B01', 2, 0, 1, 1),
(1, 'B02', 2, 0, 1, 1),
(1, 'B03', 2, 0, 1, 1),
(1, 'B04', 2, 0, 1, 1),
(1, 'B05', 2, 0, 1, 1),
(1, 'B06', 2, 0, 1, 1),
(1, 'B07', 2, 0, 1, 1),
(1, 'B08', 2, 0, 1, 1);

-- 大仓 (size=3)
INSERT INTO `iot_box` (`locker_id`, `box_no`, `size`, `status`, `enabled`, `is_locked`) VALUES 
(1, 'C01', 3, 0, 1, 1),
(1, 'C02', 3, 0, 1, 1),
(1, 'C03', 3, 0, 1, 1),
(1, 'C04', 3, 0, 1, 1);

-- 快递柜2的格口
INSERT INTO `iot_box` (`locker_id`, `box_no`, `size`, `status`, `enabled`, `is_locked`) VALUES 
(2, 'A01', 1, 0, 1, 1),
(2, 'A02', 1, 0, 1, 1),
(2, 'A03', 1, 0, 1, 1),
(2, 'A04', 1, 0, 1, 1),
(2, 'B01', 2, 0, 1, 1),
(2, 'B02', 2, 0, 1, 1),
(2, 'B03', 2, 0, 1, 1),
(2, 'B04', 2, 0, 1, 1),
(2, 'C01', 3, 0, 1, 1),
(2, 'C02', 3, 0, 1, 1);

-- 快递柜3的格口
INSERT INTO `iot_box` (`locker_id`, `box_no`, `size`, `status`, `enabled`, `is_locked`) VALUES 
(3, 'A01', 1, 0, 1, 1),
(3, 'A02', 1, 0, 1, 1),
(3, 'A03', 1, 0, 1, 1),
(3, 'A04', 1, 0, 1, 1),
(3, 'B01', 2, 0, 1, 1),
(3, 'B02', 2, 0, 1, 1),
(3, 'C01', 3, 0, 1, 1),
(3, 'C02', 3, 0, 1, 1);

-- 系统配置
INSERT INTO `sys_config` (`param_key`, `param_value`, `description`) VALUES 
('sys_name', '快递柜综合应用系统', '系统名称'),
('pickup_timeout', '72', '取件超时时间(小时)'),
('storage_timeout', '24', '寄存超时时间(小时)'),
('sms_notify', '1', '是否开启短信通知：0-关闭，1-开启'),
('timeout_reminder', '1', '是否开启超时提醒：0-关闭，1-开启'),
('reminder_hours', '6', '超时提醒提前小时数'),
('price_small_per_hour', '0.5', '小仓每小时价格(元)'),
('price_medium_per_hour', '1.0', '中仓每小时价格(元)'),
('price_large_per_hour', '1.5', '大仓每小时价格(元)');

-- 电量统计测试数据（最近7天）
INSERT INTO `iot_power_stats` (`locker_id`, `power_usage`, `record_date`) VALUES 
(1, 2.35, DATE_SUB(CURDATE(), INTERVAL 6 DAY)),
(1, 2.48, DATE_SUB(CURDATE(), INTERVAL 5 DAY)),
(1, 2.21, DATE_SUB(CURDATE(), INTERVAL 4 DAY)),
(1, 2.55, DATE_SUB(CURDATE(), INTERVAL 3 DAY)),
(1, 2.32, DATE_SUB(CURDATE(), INTERVAL 2 DAY)),
(1, 2.68, DATE_SUB(CURDATE(), INTERVAL 1 DAY)),
(1, 1.85, CURDATE()),
(2, 2.15, DATE_SUB(CURDATE(), INTERVAL 6 DAY)),
(2, 2.28, DATE_SUB(CURDATE(), INTERVAL 5 DAY)),
(2, 2.01, DATE_SUB(CURDATE(), INTERVAL 4 DAY)),
(2, 2.35, DATE_SUB(CURDATE(), INTERVAL 3 DAY)),
(2, 2.12, DATE_SUB(CURDATE(), INTERVAL 2 DAY)),
(2, 2.48, DATE_SUB(CURDATE(), INTERVAL 1 DAY)),
(2, 1.65, CURDATE()),
(3, 1.85, DATE_SUB(CURDATE(), INTERVAL 6 DAY)),
(3, 1.98, DATE_SUB(CURDATE(), INTERVAL 5 DAY)),
(3, 1.71, DATE_SUB(CURDATE(), INTERVAL 4 DAY)),
(3, 2.05, DATE_SUB(CURDATE(), INTERVAL 3 DAY)),
(3, 1.82, DATE_SUB(CURDATE(), INTERVAL 2 DAY)),
(3, 2.18, DATE_SUB(CURDATE(), INTERVAL 1 DAY)),
(3, 1.35, CURDATE());
