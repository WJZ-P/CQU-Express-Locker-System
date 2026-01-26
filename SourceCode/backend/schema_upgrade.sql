-- =====================================================
-- 快递柜综合应用系统 数据库升级脚本
-- 版本: v1.0.0 -> v2.0.0
-- 更新日期: 2026-01-26
-- 说明: 此脚本用于升级已有数据库，不会删除现有数据
-- 使用方法: 在MySQL中执行此脚本
-- =====================================================

USE `express_locker`;

-- =====================================================
-- 1. 升级 sys_user 表
-- =====================================================
-- 检查并添加新字段
ALTER TABLE `sys_user` 
ADD COLUMN IF NOT EXISTS `nickname` varchar(50) DEFAULT NULL COMMENT '用户昵称',
ADD COLUMN IF NOT EXISTS `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
ADD COLUMN IF NOT EXISTS `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-正常',
ADD COLUMN IF NOT EXISTS `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
ADD COLUMN IF NOT EXISTS `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间';

-- 更新现有数据：用username填充nickname
UPDATE `sys_user` SET `nickname` = `username` WHERE `nickname` IS NULL;

-- =====================================================
-- 2. 升级 sys_courier 表
-- =====================================================
ALTER TABLE `sys_courier`
ADD COLUMN IF NOT EXISTS `name` varchar(50) DEFAULT NULL COMMENT '快递员姓名',
ADD COLUMN IF NOT EXISTS `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态：0-离职，1-在职',
ADD COLUMN IF NOT EXISTS `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间';

-- =====================================================
-- 3. 升级 iot_locker 表
-- =====================================================
ALTER TABLE `iot_locker`
ADD COLUMN IF NOT EXISTS `name` varchar(50) DEFAULT NULL COMMENT '快递柜名称',
ADD COLUMN IF NOT EXISTS `enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用：0-禁用，1-启用',
ADD COLUMN IF NOT EXISTS `temperature` decimal(5,2) DEFAULT NULL COMMENT '温度(°C)',
ADD COLUMN IF NOT EXISTS `humidity` decimal(5,2) DEFAULT NULL COMMENT '湿度(%)',
ADD COLUMN IF NOT EXISTS `last_online_time` datetime DEFAULT NULL COMMENT '最后在线时间',
ADD COLUMN IF NOT EXISTS `remark` varchar(255) DEFAULT NULL COMMENT '备注',
ADD COLUMN IF NOT EXISTS `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间';

-- 更新现有数据：用序列号生成名称
UPDATE `iot_locker` SET `name` = CONCAT('快递柜-', `serial_no`) WHERE `name` IS NULL;

-- =====================================================
-- 4. 升级 iot_box 表
-- =====================================================
ALTER TABLE `iot_box`
ADD COLUMN IF NOT EXISTS `enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用：0-禁用，1-启用',
ADD COLUMN IF NOT EXISTS `pressure` decimal(5,2) DEFAULT NULL COMMENT '压力传感器数值(kg)',
ADD COLUMN IF NOT EXISTS `temperature` decimal(5,2) DEFAULT NULL COMMENT '格口内温度(°C)',
ADD COLUMN IF NOT EXISTS `humidity` decimal(5,2) DEFAULT NULL COMMENT '格口内湿度(%)',
ADD COLUMN IF NOT EXISTS `fault_reason` varchar(100) DEFAULT NULL COMMENT '故障原因',
ADD COLUMN IF NOT EXISTS `fault_time` datetime DEFAULT NULL COMMENT '故障时间',
ADD COLUMN IF NOT EXISTS `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间';

-- =====================================================
-- 5. 升级 bus_order 表
-- =====================================================
ALTER TABLE `bus_order`
ADD COLUMN IF NOT EXISTS `company` varchar(50) DEFAULT NULL COMMENT '快递公司',
ADD COLUMN IF NOT EXISTS `sender_name` varchar(50) DEFAULT NULL COMMENT '寄件人姓名',
ADD COLUMN IF NOT EXISTS `sender_phone` varchar(20) DEFAULT NULL COMMENT '寄件人手机',
ADD COLUMN IF NOT EXISTS `receiver_name` varchar(50) DEFAULT NULL COMMENT '收件人姓名',
ADD COLUMN IF NOT EXISTS `item_description` varchar(100) DEFAULT NULL COMMENT '物品描述',
ADD COLUMN IF NOT EXISTS `duration` int DEFAULT NULL COMMENT '寄存时长(小时)',
ADD COLUMN IF NOT EXISTS `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
ADD COLUMN IF NOT EXISTS `fee` decimal(10,2) DEFAULT NULL COMMENT '费用',
ADD COLUMN IF NOT EXISTS `notification_sent` tinyint(1) DEFAULT '0' COMMENT '是否已发送通知',
ADD COLUMN IF NOT EXISTS `notification_time` datetime DEFAULT NULL COMMENT '通知发送时间',
ADD COLUMN IF NOT EXISTS `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间';

-- 添加索引（如果不存在）
-- 注意：MySQL 8.0+ 支持 IF NOT EXISTS，低版本需手动检查
CREATE INDEX IF NOT EXISTS `idx_user_id` ON `bus_order` (`user_id`);
CREATE INDEX IF NOT EXISTS `idx_courier_id` ON `bus_order` (`courier_id`);
CREATE INDEX IF NOT EXISTS `idx_box_id` ON `bus_order` (`box_id`);
CREATE INDEX IF NOT EXISTS `idx_type` ON `bus_order` (`type`);
CREATE INDEX IF NOT EXISTS `idx_status` ON `bus_order` (`status`);

-- =====================================================
-- 6. 升级 sys_log 表
-- =====================================================
ALTER TABLE `sys_log`
ADD COLUMN IF NOT EXISTS `user_type` tinyint(1) DEFAULT NULL COMMENT '操作人类型',
ADD COLUMN IF NOT EXISTS `module` varchar(50) DEFAULT NULL COMMENT '操作模块',
ADD COLUMN IF NOT EXISTS `ip_address` varchar(50) DEFAULT NULL COMMENT 'IP地址';

-- 添加索引
CREATE INDEX IF NOT EXISTS `idx_user_id` ON `sys_log` (`user_id`);
CREATE INDEX IF NOT EXISTS `idx_create_time` ON `sys_log` (`create_time`);

-- =====================================================
-- 7. 升级 sys_config 表
-- =====================================================
ALTER TABLE `sys_config`
ADD COLUMN IF NOT EXISTS `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间';

-- =====================================================
-- 8. 升级 iot_power_stats 表
-- =====================================================
ALTER TABLE `iot_power_stats`
ADD COLUMN IF NOT EXISTS `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间';

-- 添加唯一索引（防止重复记录）
-- 注意：如果有重复数据需要先清理
-- ALTER TABLE `iot_power_stats` ADD UNIQUE KEY `uk_locker_date` (`locker_id`, `record_date`);

-- =====================================================
-- 9. 创建新表：快递员-快递柜绑定表
-- =====================================================
CREATE TABLE IF NOT EXISTS `rel_courier_locker` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `courier_id` bigint(20) NOT NULL COMMENT '快递员ID',
  `locker_id` bigint(20) NOT NULL COMMENT '快递柜ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_courier_locker` (`courier_id`, `locker_id`),
  KEY `idx_courier_id` (`courier_id`),
  KEY `idx_locker_id` (`locker_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='快递员-快递柜绑定表';

-- =====================================================
-- 10. 创建新表：用户-快递柜绑定表
-- =====================================================
CREATE TABLE IF NOT EXISTS `rel_user_locker` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `locker_id` bigint(20) NOT NULL COMMENT '快递柜ID',
  `is_default` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否默认快递柜',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_locker` (`user_id`, `locker_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_locker_id` (`locker_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户-快递柜绑定表';

-- =====================================================
-- 11. 添加系统配置项
-- =====================================================
INSERT INTO `sys_config` (`param_key`, `param_value`, `description`) VALUES 
('storage_timeout', '24', '寄存超时时间(小时)'),
('sms_notify', '1', '是否开启短信通知：0-关闭，1-开启'),
('timeout_reminder', '1', '是否开启超时提醒：0-关闭，1-开启'),
('reminder_hours', '6', '超时提醒提前小时数'),
('price_small_per_hour', '0.5', '小仓每小时价格(元)'),
('price_medium_per_hour', '1.0', '中仓每小时价格(元)'),
('price_large_per_hour', '1.5', '大仓每小时价格(元)')
ON DUPLICATE KEY UPDATE `param_value` = VALUES(`param_value`);

-- =====================================================
-- 12. 为现有快递员绑定所有快递柜（可选）
-- =====================================================
-- 这里假设现有快递员可以访问所有快递柜
INSERT IGNORE INTO `rel_courier_locker` (`courier_id`, `locker_id`)
SELECT c.id, l.id 
FROM `sys_courier` c 
CROSS JOIN `iot_locker` l 
WHERE c.status = 1;

-- =====================================================
-- 升级完成
-- =====================================================
SELECT '数据库升级完成！版本: v2.0.0' AS message;
