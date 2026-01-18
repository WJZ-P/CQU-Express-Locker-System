/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 80033
Source Host           : localhost:3306
Source Database       : express_locker

Target Server Type    : MYSQL
Target Server Version : 80033
File Encoding         : 65001

Date: 2024-01-18 20:00:00
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) DEFAULT NULL COMMENT '账号',
  `password` varchar(100) DEFAULT NULL COMMENT '密码',
  `name` varchar(50) DEFAULT NULL COMMENT '姓名',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `role` varchar(20) DEFAULT 'user' COMMENT '角色: admin, user',
  `status` varchar(20) DEFAULT '正常' COMMENT '状态: 正常, 禁用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ----------------------------
-- Table structure for couriers
-- ----------------------------
DROP TABLE IF EXISTS `couriers`;
CREATE TABLE `couriers` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) DEFAULT NULL COMMENT '工号',
  `name` varchar(50) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `company` varchar(50) DEFAULT NULL,
  `status` varchar(20) DEFAULT '正常',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_courier_no` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='快递员表';

-- ----------------------------
-- Table structure for lockers
-- ----------------------------
DROP TABLE IF EXISTS `lockers`;
CREATE TABLE `lockers` (
  `id` varchar(50) NOT NULL COMMENT '柜号 L001',
  `location` varchar(200) DEFAULT NULL,
  `compartment_count` int(11) DEFAULT NULL,
  `remark` varchar(500) DEFAULT NULL,
  `status` varchar(20) DEFAULT '正常',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='快递柜表';

-- ----------------------------
-- Table structure for compartments
-- ----------------------------
DROP TABLE IF EXISTS `compartments`;
CREATE TABLE `compartments` (
  `id` varchar(50) NOT NULL COMMENT '仓门号 L001-C01',
  `locker_id` varchar(50) DEFAULT NULL,
  `status` varchar(20) DEFAULT '空闲' COMMENT '空闲, 占用, 故障',
  PRIMARY KEY (`id`),
  KEY `idx_locker` (`locker_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='仓门表';

-- ----------------------------
-- Table structure for express_orders
-- ----------------------------
DROP TABLE IF EXISTS `express_orders`;
CREATE TABLE `express_orders` (
  `tracking_no` varchar(50) NOT NULL COMMENT '快递单号',
  `courier_id` bigint(20) DEFAULT NULL,
  `courier_name` varchar(50) DEFAULT NULL,
  `receiver_name` varchar(50) DEFAULT NULL,
  `receiver_phone` varchar(50) DEFAULT NULL,
  `locker_id` varchar(50) DEFAULT NULL,
  `compartment_id` varchar(50) DEFAULT NULL,
  `pickup_code` varchar(20) DEFAULT NULL,
  `in_time` datetime DEFAULT NULL COMMENT '入柜时间',
  `out_time` datetime DEFAULT NULL COMMENT '取件时间',
  `status` varchar(20) DEFAULT '待取件',
  PRIMARY KEY (`tracking_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='快递订单表';

-- ----------------------------
-- Table structure for storage_records
-- ----------------------------
DROP TABLE IF EXISTS `storage_records`;
CREATE TABLE `storage_records` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `depositor_name` varchar(50) DEFAULT NULL COMMENT '寄存人姓名',
  `depositor_phone` varchar(20) DEFAULT NULL COMMENT '寄存人手机',
  `locker_id` varchar(50) DEFAULT NULL COMMENT '快递柜编号',
  `compartment_id` varchar(50) DEFAULT NULL COMMENT '仓门编号',
  `pickup_code` varchar(20) DEFAULT NULL COMMENT '取件码',
  `deposit_time` datetime DEFAULT NULL COMMENT '寄存时间',
  `pickup_time` datetime DEFAULT NULL COMMENT '取出时间',
  `status` varchar(20) DEFAULT '寄存中' COMMENT '状态: 寄存中, 已取出',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='寄存记录表';

-- ----------------------------
-- Table structure for system_config
-- ----------------------------
DROP TABLE IF EXISTS `system_config`;
CREATE TABLE `system_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `system_name` varchar(100) DEFAULT '智能快递柜管理系统' COMMENT '系统名称',
  `pickup_timeout` int(11) DEFAULT 24 COMMENT '取件超时时长(小时)',
  `storage_timeout` int(11) DEFAULT 48 COMMENT '寄存超时时长(小时)',
  `sms_notify` bit(1) DEFAULT b'1' COMMENT '短信通知开关',
  `timeout_reminder` bit(1) DEFAULT b'1' COMMENT '超时提醒开关',
  `face_recognition` bit(1) DEFAULT b'0' COMMENT '人脸识别开关',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

-- ----------------------------
-- Initialization Data
-- ----------------------------
INSERT INTO `users` (`username`, `password`, `name`, `role`, `status`) VALUES ('admin', '123456', '系统管理员', 'admin', '正常');

-- Insert default system configuration
INSERT INTO `system_config` (`system_name`, `pickup_timeout`, `storage_timeout`, `sms_notify`, `timeout_reminder`, `face_recognition`) 
VALUES ('智能快递柜管理系统', 24, 48, 1, 1, 0);
