-- =====================================================
-- 测试数据脚本
-- 用于Web管理端功能测试
-- =====================================================

USE `express_locker`;

-- =====================================================
-- 1. 添加更多用户（约20个）
-- =====================================================
INSERT INTO `sys_user` (`username`, `password`, `phone`, `nickname`, `role`, `status`) VALUES 
('user_zhangwei', 'e10adc3949ba59abbe56e057f20f883e', '13900001001', '张伟', 1, 1),
('user_wangfei', 'e10adc3949ba59abbe56e057f20f883e', '13900001002', '王飞', 1, 1),
('user_liuna', 'e10adc3949ba59abbe56e057f20f883e', '13900001003', '刘娜', 1, 1),
('user_chengang', 'e10adc3949ba59abbe56e057f20f883e', '13900001004', '陈刚', 1, 1),
('user_yangyang', 'e10adc3949ba59abbe56e057f20f883e', '13900001005', '杨洋', 1, 1),
('user_zhaoli', 'e10adc3949ba59abbe56e057f20f883e', '13900001006', '赵丽', 1, 1),
('user_huanglei', 'e10adc3949ba59abbe56e057f20f883e', '13900001007', '黄磊', 1, 1),
('user_zhoujie', 'e10adc3949ba59abbe56e057f20f883e', '13900001008', '周杰', 1, 1),
('user_wuqiang', 'e10adc3949ba59abbe56e057f20f883e', '13900001009', '吴强', 1, 1),
('user_xuming', 'e10adc3949ba59abbe56e057f20f883e', '13900001010', '徐明', 1, 1),
('user_sunli', 'e10adc3949ba59abbe56e057f20f883e', '13900001011', '孙丽', 1, 0),
('user_majun', 'e10adc3949ba59abbe56e057f20f883e', '13900001012', '马军', 1, 1),
('user_zhulin', 'e10adc3949ba59abbe56e057f20f883e', '13900001013', '朱林', 1, 1),
('user_hujing', 'e10adc3949ba59abbe56e057f20f883e', '13900001014', '胡静', 1, 1),
('user_guowei', 'e10adc3949ba59abbe56e057f20f883e', '13900001015', '郭伟', 1, 1),
('courier_wangwu', 'e10adc3949ba59abbe56e057f20f883e', '13900002001', '王五', 2, 1),
('courier_zhaoliu', 'e10adc3949ba59abbe56e057f20f883e', '13900002002', '赵六', 2, 1),
('courier_sunqi', 'e10adc3949ba59abbe56e057f20f883e', '13900002003', '孙七', 2, 1),
('courier_zhouba', 'e10adc3949ba59abbe56e057f20f883e', '13900002004', '周八', 2, 0),
('courier_wujiu', 'e10adc3949ba59abbe56e057f20f883e', '13900002005', '吴九', 2, 1);

-- =====================================================
-- 2. 添加更多快递员信息
-- =====================================================
INSERT INTO `sys_courier` (`user_id`, `name`, `company`, `employee_id`, `status`) VALUES 
((SELECT id FROM sys_user WHERE username='courier_wangwu'), '王五', '中通快递', 'ZT001', 1),
((SELECT id FROM sys_user WHERE username='courier_zhaoliu'), '赵六', '韵达快递', 'YD001', 1),
((SELECT id FROM sys_user WHERE username='courier_sunqi'), '孙七', '申通快递', 'ST001', 1),
((SELECT id FROM sys_user WHERE username='courier_zhouba'), '周八', '顺丰速运', 'SF002', 0),
((SELECT id FROM sys_user WHERE username='courier_wujiu'), '吴九', '京东物流', 'JD001', 1);

-- =====================================================
-- 3. 添加更多快递柜
-- =====================================================
INSERT INTO `iot_locker` (`serial_no`, `name`, `location`, `ip_address`, `status`, `enabled`, `temperature`, `humidity`) VALUES 
('LOCKER004', 'B区2号柜', '重庆大学B区学生公寓', '192.168.1.103', 1, 1, 25.0, 46.0),
('LOCKER005', 'C区1号柜', '重庆大学C区食堂旁', '192.168.1.104', 1, 1, 26.5, 50.0),
('LOCKER006', 'D区1号柜', '重庆大学D区体育馆', '192.168.1.105', 0, 1, 23.0, 40.0),
('LOCKER007', 'E区1号柜', '重庆大学E区实验楼', '192.168.1.106', 1, 0, 24.0, 45.0);

-- 快递柜4的格口
INSERT INTO `iot_box` (`locker_id`, `box_no`, `size`, `status`, `enabled`, `is_locked`) VALUES 
(4, 'A01', 1, 0, 1, 1), (4, 'A02', 1, 0, 1, 1), (4, 'A03', 1, 0, 1, 1), (4, 'A04', 1, 0, 1, 1),
(4, 'B01', 2, 0, 1, 1), (4, 'B02', 2, 0, 1, 1), (4, 'B03', 2, 0, 1, 1), (4, 'B04', 2, 0, 1, 1),
(4, 'C01', 3, 0, 1, 1), (4, 'C02', 3, 0, 1, 1);

-- 快递柜5的格口
INSERT INTO `iot_box` (`locker_id`, `box_no`, `size`, `status`, `enabled`, `is_locked`) VALUES 
(5, 'A01', 1, 0, 1, 1), (5, 'A02', 1, 0, 1, 1), (5, 'A03', 1, 0, 1, 1), (5, 'A04', 1, 0, 1, 1),
(5, 'B01', 2, 0, 1, 1), (5, 'B02', 2, 0, 1, 1), (5, 'B03', 2, 0, 1, 1), (5, 'B04', 2, 0, 1, 1),
(5, 'C01', 3, 0, 1, 1), (5, 'C02', 3, 0, 1, 1);

-- 快递柜6的格口（有故障）
INSERT INTO `iot_box` (`locker_id`, `box_no`, `size`, `status`, `enabled`, `is_locked`, `fault_reason`, `fault_time`) VALUES 
(6, 'A01', 1, 2, 1, 1, '门锁故障', NOW()), (6, 'A02', 1, 0, 1, 1, NULL, NULL),
(6, 'B01', 2, 2, 1, 1, '传感器异常', NOW()), (6, 'B02', 2, 0, 1, 1, NULL, NULL),
(6, 'C01', 3, 0, 1, 1, NULL, NULL);

-- =====================================================
-- 4. 添加大量订单数据（约80条）
-- 说明：courier_id 对应 sys_courier.id
--   1=测试快递员, 2=张三, 3=李四 (schema.sql初始数据)
--   4=王五, 5=赵六, 6=孙七, 7=周八, 8=吴九 (test_data.sql新增)
-- user_id 对应 sys_user.id
--   1=admin, 2=testuser, 3=testcourier, 4-7=schema初始用户
--   8-27=test_data.sql新增用户
-- =====================================================

-- 待取件订单（status=0）
INSERT INTO `bus_order` (`order_no`, `type`, `tracking_no`, `company`, `pickup_code`, `box_id`, `courier_id`, `user_id`, `receiver_name`, `receiver_phone`, `status`, `create_time`) VALUES 
('ORD20260126001', 1, 'SF1234567890001', '顺丰速运', '123456', 1, 1, 2, '测试用户', '13800138001', 0, DATE_SUB(NOW(), INTERVAL 2 HOUR)),
('ORD20260126002', 1, 'YT9876543210001', '圆通速递', '234567', 2, 3, 8, '张伟', '13900001001', 0, DATE_SUB(NOW(), INTERVAL 3 HOUR)),
('ORD20260126003', 1, 'ZT1122334455001', '中通快递', '345678', 3, 4, 9, '王飞', '13900001002', 0, DATE_SUB(NOW(), INTERVAL 4 HOUR)),
('ORD20260126004', 1, 'YD5544332211001', '韵达快递', '456789', 4, 5, 10, '刘娜', '13900001003', 0, DATE_SUB(NOW(), INTERVAL 5 HOUR)),
('ORD20260126005', 1, 'ST6677889900001', '申通快递', '567890', 5, 6, 11, '陈刚', '13900001004', 0, DATE_SUB(NOW(), INTERVAL 6 HOUR)),
('ORD20260126006', 1, 'SF1234567890002', '顺丰速运', '678901', 6, 1, 12, '杨洋', '13900001005', 0, DATE_SUB(NOW(), INTERVAL 1 DAY)),
('ORD20260126007', 1, 'JD8899001122001', '京东物流', '789012', 7, 8, 13, '赵丽', '13900001006', 0, DATE_SUB(NOW(), INTERVAL 1 DAY)),
('ORD20260126008', 1, 'SF1234567890003', '顺丰速运', '890123', 8, 1, 14, '黄磊', '13900001007', 0, DATE_SUB(NOW(), INTERVAL 2 DAY)),
('ORD20260126009', 1, 'YT9876543210002', '圆通速递', '901234', 21, 2, 15, '周杰', '13900001008', 0, DATE_SUB(NOW(), INTERVAL 1 HOUR)),
('ORD20260126010', 1, 'ZT1122334455002', '中通快递', '012345', 22, 3, 16, '吴强', '13900001009', 0, DATE_SUB(NOW(), INTERVAL 30 MINUTE));

-- 已取件订单（status=1）
INSERT INTO `bus_order` (`order_no`, `type`, `tracking_no`, `company`, `pickup_code`, `box_id`, `courier_id`, `user_id`, `receiver_name`, `receiver_phone`, `status`, `create_time`, `finish_time`) VALUES 
('ORD20260125001', 1, 'SF1234567890101', '顺丰速运', '111111', 1, 1, 2, '测试用户', '13800138001', 1, DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
('ORD20260125002', 1, 'YT9876543210101', '圆通速递', '222222', 2, 2, 8, '张伟', '13900001001', 1, DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),
('ORD20260125003', 1, 'ZT1122334455101', '中通快递', '333333', 3, 3, 9, '王飞', '13900001002', 1, DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY)),
('ORD20260125004', 1, 'YD5544332211101', '韵达快递', '444444', 4, 5, 10, '刘娜', '13900001003', 1, DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY)),
('ORD20260125005', 1, 'ST6677889900101', '申通快递', '555555', 5, 6, 11, '陈刚', '13900001004', 1, DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY)),
('ORD20260125006', 1, 'SF1234567890102', '顺丰速运', '666666', 6, 1, 12, '杨洋', '13900001005', 1, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
('ORD20260125007', 1, 'JD8899001122101', '京东物流', '777777', 7, 8, 13, '赵丽', '13900001006', 1, DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
('ORD20260125008', 1, 'SF1234567890103', '顺丰速运', '888888', 8, 1, 14, '黄磊', '13900001007', 1, DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),
('ORD20260125009', 1, 'YT9876543210102', '圆通速递', '999999', 21, 2, 15, '周杰', '13900001008', 1, DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY)),
('ORD20260125010', 1, 'ZT1122334455102', '中通快递', '000000', 22, 3, 16, '吴强', '13900001009', 1, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 12 HOUR)),
('ORD20260124001', 1, 'SF1234567890201', '顺丰速运', '111222', 1, 1, 2, '测试用户', '13800138001', 1, DATE_SUB(NOW(), INTERVAL 8 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY)),
('ORD20260124002', 1, 'YT9876543210201', '圆通速递', '222333', 2, 2, 8, '张伟', '13900001001', 1, DATE_SUB(NOW(), INTERVAL 9 DAY), DATE_SUB(NOW(), INTERVAL 8 DAY)),
('ORD20260124003', 1, 'ZT1122334455201', '中通快递', '333444', 3, 3, 9, '王飞', '13900001002', 1, DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 9 DAY)),
('ORD20260124004', 1, 'YD5544332211201', '韵达快递', '444555', 4, 5, 10, '刘娜', '13900001003', 1, DATE_SUB(NOW(), INTERVAL 11 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY)),
('ORD20260124005', 1, 'ST6677889900201', '申通快递', '555666', 5, 6, 11, '陈刚', '13900001004', 1, DATE_SUB(NOW(), INTERVAL 12 DAY), DATE_SUB(NOW(), INTERVAL 11 DAY)),
('ORD20260124006', 1, 'SF1234567890202', '顺丰速运', '666777', 6, 1, 17, '徐明', '13900001010', 1, DATE_SUB(NOW(), INTERVAL 13 DAY), DATE_SUB(NOW(), INTERVAL 12 DAY)),
('ORD20260124007', 1, 'JD8899001122201', '京东物流', '777888', 7, 8, 19, '马军', '13900001012', 1, DATE_SUB(NOW(), INTERVAL 14 DAY), DATE_SUB(NOW(), INTERVAL 13 DAY)),
('ORD20260124008', 1, 'SF1234567890203', '顺丰速运', '888999', 8, 1, 20, '朱林', '13900001013', 1, DATE_SUB(NOW(), INTERVAL 15 DAY), DATE_SUB(NOW(), INTERVAL 14 DAY)),
('ORD20260124009', 1, 'YT9876543210202', '圆通速递', '999000', 21, 2, 21, '胡静', '13900001014', 1, DATE_SUB(NOW(), INTERVAL 16 DAY), DATE_SUB(NOW(), INTERVAL 15 DAY)),
('ORD20260124010', 1, 'ZT1122334455202', '中通快递', '000111', 22, 4, 22, '郭伟', '13900001015', 1, DATE_SUB(NOW(), INTERVAL 17 DAY), DATE_SUB(NOW(), INTERVAL 16 DAY));

-- 超时订单（status=2）
INSERT INTO `bus_order` (`order_no`, `type`, `tracking_no`, `company`, `pickup_code`, `box_id`, `courier_id`, `user_id`, `receiver_name`, `receiver_phone`, `status`, `create_time`, `expire_time`) VALUES 
('ORD20260120001', 1, 'SF1234567890301', '顺丰速运', '112233', 1, 1, 2, '测试用户', '13800138001', 2, DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
('ORD20260120002', 1, 'YT9876543210301', '圆通速递', '223344', 2, 2, 8, '张伟', '13900001001', 2, DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),
('ORD20260120003', 1, 'ZT1122334455301', '中通快递', '334455', 3, 3, 9, '王飞', '13900001002', 2, DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY)),
('ORD20260120004', 1, 'YD5544332211301', '韵达快递', '445566', 4, 5, 10, '刘娜', '13900001003', 2, DATE_SUB(NOW(), INTERVAL 8 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY)),
('ORD20260120005', 1, 'ST6677889900301', '申通快递', '556677', 5, 6, 11, '陈刚', '13900001004', 2, DATE_SUB(NOW(), INTERVAL 9 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY));

-- 已退回订单（status=3）
INSERT INTO `bus_order` (`order_no`, `type`, `tracking_no`, `company`, `pickup_code`, `box_id`, `courier_id`, `user_id`, `receiver_name`, `receiver_phone`, `status`, `create_time`, `finish_time`) VALUES 
('ORD20260115001', 1, 'SF1234567890401', '顺丰速运', '667788', 1, 1, 2, '测试用户', '13800138001', 3, DATE_SUB(NOW(), INTERVAL 12 DAY), DATE_SUB(NOW(), INTERVAL 9 DAY)),
('ORD20260115002', 1, 'YT9876543210401', '圆通速递', '778899', 2, 2, 8, '张伟', '13900001001', 3, DATE_SUB(NOW(), INTERVAL 13 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY)),
('ORD20260115003', 1, 'ZT1122334455401', '中通快递', '889900', 3, 3, 9, '王飞', '13900001002', 3, DATE_SUB(NOW(), INTERVAL 14 DAY), DATE_SUB(NOW(), INTERVAL 11 DAY));

-- 寄存订单（type=3）
INSERT INTO `bus_order` (`order_no`, `type`, `box_id`, `user_id`, `receiver_name`, `receiver_phone`, `item_description`, `duration`, `fee`, `status`, `create_time`, `expire_time`) VALUES 
('STR20260126001', 3, 9, 2, '测试用户', '13800138001', '笔记本电脑', 24, 12.00, 0, DATE_SUB(NOW(), INTERVAL 2 HOUR), DATE_ADD(NOW(), INTERVAL 22 HOUR)),
('STR20260126002', 3, 10, 8, '张伟', '13900001001', '书籍', 12, 6.00, 0, DATE_SUB(NOW(), INTERVAL 1 HOUR), DATE_ADD(NOW(), INTERVAL 11 HOUR)),
('STR20260126003', 3, 11, 9, '王飞', '13900001002', '衣物', 48, 24.00, 0, DATE_SUB(NOW(), INTERVAL 5 HOUR), DATE_ADD(NOW(), INTERVAL 43 HOUR)),
('STR20260125001', 3, 12, 10, '刘娜', '13900001003', '电子产品', 24, 12.00, 1, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
('STR20260125002', 3, 13, 11, '陈刚', '13900001004', '食品', 12, 6.00, 1, DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
('STR20260124001', 3, 14, 12, '杨洋', '13900001005', '文件资料', 24, 12.00, 2, DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY)),
('STR20260124002', 3, 15, 13, '赵丽', '13900001006', '化妆品', 48, 24.00, 1, DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY));

-- 寄件订单（type=2）
INSERT INTO `bus_order` (`order_no`, `type`, `tracking_no`, `company`, `box_id`, `courier_id`, `user_id`, `sender_name`, `sender_phone`, `receiver_name`, `receiver_phone`, `status`, `create_time`) VALUES 
('SND20260126001', 2, 'SF2234567890001', '顺丰速运', 16, 1, 2, '测试用户', '13800138001', '李四', '13912345678', 0, DATE_SUB(NOW(), INTERVAL 1 HOUR)),
('SND20260126002', 2, 'YT2876543210001', '圆通速递', 23, 2, 8, '张伟', '13900001001', '王五', '13923456789', 0, DATE_SUB(NOW(), INTERVAL 2 HOUR)),
('SND20260125001', 2, 'SF2234567890002', '顺丰速运', 24, 1, 9, '王飞', '13900001002', '赵六', '13934567890', 1, DATE_SUB(NOW(), INTERVAL 1 DAY)),
('SND20260125002', 2, 'ZT2122334455001', '中通快递', 25, 4, 10, '刘娜', '13900001003', '孙七', '13945678901', 1, DATE_SUB(NOW(), INTERVAL 2 DAY));

-- 更多历史订单用于统计（修正courier_id: 使用1-8范围内的有效ID）
INSERT INTO `bus_order` (`order_no`, `type`, `tracking_no`, `company`, `pickup_code`, `box_id`, `courier_id`, `user_id`, `receiver_name`, `receiver_phone`, `status`, `create_time`, `finish_time`) VALUES 
('ORD20260110001', 1, 'SF3234567890001', '顺丰速运', 'A11111', 1, 1, 2, '测试用户', '13800138001', 1, DATE_SUB(NOW(), INTERVAL 20 DAY), DATE_SUB(NOW(), INTERVAL 19 DAY)),
('ORD20260110002', 1, 'YT3876543210001', '圆通速递', 'A22222', 2, 2, 8, '张伟', '13900001001', 1, DATE_SUB(NOW(), INTERVAL 21 DAY), DATE_SUB(NOW(), INTERVAL 20 DAY)),
('ORD20260110003', 1, 'ZT3122334455001', '中通快递', 'A33333', 3, 3, 9, '王飞', '13900001002', 1, DATE_SUB(NOW(), INTERVAL 22 DAY), DATE_SUB(NOW(), INTERVAL 21 DAY)),
('ORD20260110004', 1, 'YD3544332211001', '韵达快递', 'A44444', 4, 5, 10, '刘娜', '13900001003', 1, DATE_SUB(NOW(), INTERVAL 23 DAY), DATE_SUB(NOW(), INTERVAL 22 DAY)),
('ORD20260110005', 1, 'ST3677889900001', '申通快递', 'A55555', 5, 6, 11, '陈刚', '13900001004', 1, DATE_SUB(NOW(), INTERVAL 24 DAY), DATE_SUB(NOW(), INTERVAL 23 DAY)),
('ORD20260110006', 1, 'JD3899001122001', '京东物流', 'A66666', 6, 8, 12, '杨洋', '13900001005', 1, DATE_SUB(NOW(), INTERVAL 25 DAY), DATE_SUB(NOW(), INTERVAL 24 DAY)),
('ORD20260110007', 1, 'SF3234567890002', '顺丰速运', 'A77777', 7, 1, 13, '赵丽', '13900001006', 1, DATE_SUB(NOW(), INTERVAL 26 DAY), DATE_SUB(NOW(), INTERVAL 25 DAY)),
('ORD20260110008', 1, 'YT3876543210002', '圆通速递', 'A88888', 8, 2, 14, '黄磊', '13900001007', 1, DATE_SUB(NOW(), INTERVAL 27 DAY), DATE_SUB(NOW(), INTERVAL 26 DAY)),
('ORD20260110009', 1, 'ZT3122334455002', '中通快递', 'A99999', 21, 3, 15, '周杰', '13900001008', 1, DATE_SUB(NOW(), INTERVAL 28 DAY), DATE_SUB(NOW(), INTERVAL 27 DAY)),
('ORD20260110010', 1, 'YD3544332211002', '韵达快递', 'B11111', 22, 5, 16, '吴强', '13900001009', 1, DATE_SUB(NOW(), INTERVAL 29 DAY), DATE_SUB(NOW(), INTERVAL 28 DAY)),
('ORD20260105001', 1, 'SF4234567890001', '顺丰速运', 'B22222', 1, 1, 17, '徐明', '13900001010', 1, DATE_SUB(NOW(), INTERVAL 25 DAY), DATE_SUB(NOW(), INTERVAL 24 DAY)),
('ORD20260105002', 1, 'YT4876543210001', '圆通速递', 'B33333', 2, 2, 19, '马军', '13900001012', 1, DATE_SUB(NOW(), INTERVAL 26 DAY), DATE_SUB(NOW(), INTERVAL 25 DAY)),
('ORD20260105003', 1, 'ZT4122334455001', '中通快递', 'B44444', 3, 4, 20, '朱林', '13900001013', 1, DATE_SUB(NOW(), INTERVAL 27 DAY), DATE_SUB(NOW(), INTERVAL 26 DAY)),
('ORD20260105004', 1, 'YD4544332211001', '韵达快递', 'B55555', 4, 5, 21, '胡静', '13900001014', 1, DATE_SUB(NOW(), INTERVAL 28 DAY), DATE_SUB(NOW(), INTERVAL 27 DAY)),
('ORD20260105005', 1, 'ST4677889900001', '申通快递', 'B66666', 5, 6, 22, '郭伟', '13900001015', 1, DATE_SUB(NOW(), INTERVAL 29 DAY), DATE_SUB(NOW(), INTERVAL 28 DAY));

-- =====================================================
-- 5. 添加操作日志
-- =====================================================
INSERT INTO `sys_log` (`user_id`, `user_type`, `operation`, `module`, `content`, `ip_address`, `create_time`) VALUES 
(1, 0, '登录', '认证', '管理员登录系统', '192.168.1.100', DATE_SUB(NOW(), INTERVAL 1 HOUR)),
(1, 0, '查询', '用户管理', '查询用户列表', '192.168.1.100', DATE_SUB(NOW(), INTERVAL 55 MINUTE)),
(1, 0, '新增', '用户管理', '新增用户：张伟', '192.168.1.100', DATE_SUB(NOW(), INTERVAL 50 MINUTE)),
(1, 0, '查询', '快递柜管理', '查询快递柜列表', '192.168.1.100', DATE_SUB(NOW(), INTERVAL 45 MINUTE)),
(1, 0, '修改', '快递柜管理', '修改快递柜状态：LOCKER001', '192.168.1.100', DATE_SUB(NOW(), INTERVAL 40 MINUTE)),
(1, 0, '查询', '订单管理', '查询订单列表', '192.168.1.100', DATE_SUB(NOW(), INTERVAL 35 MINUTE)),
(1, 0, '操作', '订单管理', '强制取件：ORD20260126001', '192.168.1.100', DATE_SUB(NOW(), INTERVAL 30 MINUTE)),
(1, 0, '查询', '统计分析', '查看仪表盘统计', '192.168.1.100', DATE_SUB(NOW(), INTERVAL 25 MINUTE)),
(1, 0, '修改', '系统配置', '修改系统配置：pickup_timeout', '192.168.1.100', DATE_SUB(NOW(), INTERVAL 20 MINUTE)),
(1, 0, '查询', '快递员管理', '查询快递员列表', '192.168.1.100', DATE_SUB(NOW(), INTERVAL 15 MINUTE)),
(2, 1, '登录', '认证', '用户登录', '192.168.1.101', DATE_SUB(NOW(), INTERVAL 2 HOUR)),
(2, 1, '取件', '快递', '取件成功：ORD20260125001', '192.168.1.101', DATE_SUB(NOW(), INTERVAL 1 HOUR)),
(3, 2, '登录', '认证', '快递员登录', '192.168.1.102', DATE_SUB(NOW(), INTERVAL 3 HOUR)),
(3, 2, '投递', '快递', '投递成功：SF1234567890001', '192.168.1.102', DATE_SUB(NOW(), INTERVAL 2 HOUR)),
(1, 0, '登录', '认证', '管理员登录系统', '192.168.1.100', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(1, 0, '删除', '用户管理', '删除用户：test123', '192.168.1.100', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(1, 0, '修改', '快递员管理', '修改快递员状态：周八', '192.168.1.100', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(1, 0, '操作', '格口管理', '远程开门：A区1号柜-A01', '192.168.1.100', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(1, 0, '查询', '统计分析', '导出统计报表', '192.168.1.100', DATE_SUB(NOW(), INTERVAL 2 DAY)),
(1, 0, '修改', '系统配置', '批量修改配置', '192.168.1.100', DATE_SUB(NOW(), INTERVAL 2 DAY));

-- =====================================================
-- 6. 更新格口状态（部分被占用）
-- =====================================================
UPDATE `iot_box` SET `status` = 1 WHERE `id` IN (1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 21, 22, 23, 24, 25);

-- =====================================================
-- 7. 添加更多电量统计数据
-- =====================================================
INSERT INTO `iot_power_stats` (`locker_id`, `power_usage`, `record_date`) VALUES 
(4, 2.25, DATE_SUB(CURDATE(), INTERVAL 6 DAY)),
(4, 2.38, DATE_SUB(CURDATE(), INTERVAL 5 DAY)),
(4, 2.11, DATE_SUB(CURDATE(), INTERVAL 4 DAY)),
(4, 2.45, DATE_SUB(CURDATE(), INTERVAL 3 DAY)),
(4, 2.22, DATE_SUB(CURDATE(), INTERVAL 2 DAY)),
(4, 2.58, DATE_SUB(CURDATE(), INTERVAL 1 DAY)),
(4, 1.75, CURDATE()),
(5, 2.05, DATE_SUB(CURDATE(), INTERVAL 6 DAY)),
(5, 2.18, DATE_SUB(CURDATE(), INTERVAL 5 DAY)),
(5, 1.91, DATE_SUB(CURDATE(), INTERVAL 4 DAY)),
(5, 2.25, DATE_SUB(CURDATE(), INTERVAL 3 DAY)),
(5, 2.02, DATE_SUB(CURDATE(), INTERVAL 2 DAY)),
(5, 2.38, DATE_SUB(CURDATE(), INTERVAL 1 DAY)),
(5, 1.55, CURDATE()),
(6, 1.75, DATE_SUB(CURDATE(), INTERVAL 6 DAY)),
(6, 1.88, DATE_SUB(CURDATE(), INTERVAL 5 DAY)),
(6, 1.61, DATE_SUB(CURDATE(), INTERVAL 4 DAY)),
(6, 1.95, DATE_SUB(CURDATE(), INTERVAL 3 DAY)),
(6, 1.72, DATE_SUB(CURDATE(), INTERVAL 2 DAY)),
(6, 2.08, DATE_SUB(CURDATE(), INTERVAL 1 DAY)),
(6, 1.25, CURDATE());

SELECT '测试数据插入完成！' AS message;
SELECT CONCAT('用户总数: ', COUNT(*)) AS info FROM sys_user;
SELECT CONCAT('快递员总数: ', COUNT(*)) AS info FROM sys_courier;
SELECT CONCAT('快递柜总数: ', COUNT(*)) AS info FROM iot_locker;
SELECT CONCAT('格口总数: ', COUNT(*)) AS info FROM iot_box;
SELECT CONCAT('订单总数: ', COUNT(*)) AS info FROM bus_order;
SELECT CONCAT('日志总数: ', COUNT(*)) AS info FROM sys_log;
