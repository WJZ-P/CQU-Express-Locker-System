package com.cqu.locker.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cqu.locker.entity.BusOrder;
import com.cqu.locker.entity.IotBox;
import com.cqu.locker.entity.IotLocker;
import com.cqu.locker.entity.SysCourier;
import com.cqu.locker.entity.SysUser;
import com.cqu.locker.entity.dto.*;
import com.cqu.locker.mapper.BusOrderMapper;
import com.cqu.locker.mapper.IotBoxMapper;
import com.cqu.locker.mapper.IotLockerMapper;
import com.cqu.locker.mapper.SysCourierMapper;
import com.cqu.locker.mapper.SysUserMapper;
import com.cqu.locker.service.CourierService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 快递员服务实现类
 */
@Slf4j
@Service
public class CourierServiceImpl implements CourierService {
    
    @Autowired
    private SysCourierMapper courierMapper;
    
    @Autowired
    private SysUserMapper userMapper;
    
    @Autowired
    private BusOrderMapper orderMapper;
    
    @Autowired
    private IotBoxMapper boxMapper;
    
    @Autowired
    private IotLockerMapper lockerMapper;
    
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Override
    public CourierProfileResponse getCourierProfile(Long courierId) {
        // 查询快递员信息（根据userId字段查询，而不是id字段）
        LambdaQueryWrapper<SysCourier> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysCourier::getUserId, courierId);
        SysCourier courier = courierMapper.selectOne(wrapper);
        if (courier == null) {
            throw new RuntimeException("快递员不存在");
        }
        
        // 查询关联的用户信息
        SysUser user = userMapper.selectById(courierId);
        if (user == null) {
            throw new RuntimeException("用户信息不存在");
        }
        
        // 模拟绑定的快递柜列表
        List<CourierProfileResponse.BindLocker> bindLockers = new ArrayList<>();
        bindLockers.add(CourierProfileResponse.BindLocker.builder()
                .lockerId("L001")
                .lockerName("A区1号柜")
                .build());
        bindLockers.add(CourierProfileResponse.BindLocker.builder()
                .lockerId("L002")
                .lockerName("B区2号柜")
                .build());
        
        // 模拟今日投递和揽收数量
        Integer todayDelivered = 25;
        Integer todayCollected = 10;
        
        return CourierProfileResponse.builder()
                .courierId(courierId.toString())
                .name(user.getUsername())
                .phone(user.getPhone())
                .company(courier.getCompany())
                .bindLockers(bindLockers)
                .todayDelivered(todayDelivered)
                .todayCollected(todayCollected)
                .build();
    }
    
    @Override
    @Transactional
    public DeliverResponse deliver(DeliverRequest request, Long courierId) {
        // 将字符串lockerId转换为Long类型
        Long lockerId = Long.parseLong(request.getLockerId());
        
        // 将字符串compartmentSize转换为数字类型
        Integer size = 2; // 默认中等
        switch (request.getCompartmentSize()) {
            case "small":
                size = 1;
                break;
            case "large":
                size = 3;
                break;
        }
        
        // 查找对应尺寸的空闲格口
        LambdaQueryWrapper<IotBox> boxWrapper = new LambdaQueryWrapper<>();
        boxWrapper.eq(IotBox::getLockerId, lockerId)
                .eq(IotBox::getSize, size)
                .eq(IotBox::getStatus, 0) // 0-空闲
                .last("limit 1"); // 只取第一条
        
        IotBox box = boxMapper.selectOne(boxWrapper);
        if (box == null) {
            throw new RuntimeException("该尺寸的空闲格口已用完");
        }
        
        // 查询收件人信息
        LambdaQueryWrapper<SysUser> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(SysUser::getPhone, request.getReceiverPhone())
                .last("limit 1"); // 只取第一条
        SysUser user = userMapper.selectOne(userWrapper);
        if (user == null) {
            throw new RuntimeException("收件人不存在");
        }
        
        // 生成快递订单号
        String orderNo = "EX" + DateUtil.format(LocalDateTime.now(), "yyyyMMddHHmmss") + RandomUtil.randomNumbers(3);
        
        // 生成6位取件码
        String pickupCode = RandomUtil.randomNumbers(6);
        
        // 创建快递订单
        BusOrder order = new BusOrder();
        order.setOrderNo(orderNo);
        order.setType(1); // 1-投递
        order.setTrackingNo(request.getTrackingNo());
        order.setPickupCode(pickupCode);
        order.setBoxId(box.getId());
        order.setCourierId(courierId);
        order.setUserId(user.getId());
        order.setReceiverPhone(request.getReceiverPhone());
        order.setStatus(0); // 0-待取
        order.setCreateTime(LocalDateTime.now());
        
        orderMapper.insert(order);
        
        // 更新格口状态为占用
        box.setStatus(1); // 1-占用
        boxMapper.updateById(box);
        
        return DeliverResponse.builder()
                .expressId(orderNo)
                .compartmentNo(box.getBoxNo())
                .pickupCode(pickupCode)
                .build();
    }
    
    @Override
    public ReceiverInfoResponse queryReceiver(String phone) {
        // 查询收件人信息
        LambdaQueryWrapper<SysUser> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(SysUser::getPhone, phone);
        SysUser user = userMapper.selectOne(userWrapper);
        if (user == null) {
            throw new RuntimeException("收件人不存在");
        }
        
        // 模拟默认快递柜
        ReceiverInfoResponse.DefaultLocker defaultLocker = ReceiverInfoResponse.DefaultLocker.builder()
                .lockerId("L001")
                .lockerName("A区1号柜")
                .build();
        
        // 手机号脱敏
        String maskedPhone = phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
        
        return ReceiverInfoResponse.builder()
                .userId(user.getId().toString())
                .name(user.getUsername())
                .phone(maskedPhone)
                .defaultLocker(defaultLocker)
                .build();
    }
    
    @Override
    public PendingCollectResponse getPendingCollectList(Long courierId) {
        // 查询待揽收的寄件订单（type=2表示寄件，status=0表示待揽收）
        LambdaQueryWrapper<BusOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BusOrder::getType, 2) // 寄件类型
                .eq(BusOrder::getStatus, 0) // 待揽收
                .orderByDesc(BusOrder::getCreateTime);
        
        List<BusOrder> orders = orderMapper.selectList(wrapper);
        
        List<PendingCollectResponse.CollectItem> items = new ArrayList<>();
        for (BusOrder order : orders) {
            // 获取用户信息
            SysUser user = userMapper.selectById(order.getUserId());
            if (user == null) continue;
            
            // 模拟寄件信息
            PendingCollectResponse.CollectItem item = PendingCollectResponse.CollectItem.builder()
                    .orderId(order.getOrderNo())
                    .senderName(user.getUsername())
                    .senderPhone(maskPhone(user.getPhone()))
                    .senderAddress("重庆大学A区学生宿舍") // 模拟地址
                    .receiverAddress("北京市海淀区xxx街道") // 模拟地址
                    .itemType("日用品") // 模拟物品类型
                    .weight("1.5") // 模拟重量
                    .createTime(order.getCreateTime().format(formatter))
                    .remark("易碎物品") // 模拟备注
                    .build();
            items.add(item);
        }
        
        return PendingCollectResponse.builder()
                .total(items.size())
                .list(items)
                .build();
    }
    
    @Override
    public PendingReturnResponse getPendingReturnList(Long courierId) {
        // 查询超时未取的快递订单（type=1表示投递，status=2表示超时）
        LambdaQueryWrapper<BusOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BusOrder::getType, 1) // 投递类型
                .eq(BusOrder::getStatus, 2) // 超时
                .orderByDesc(BusOrder::getCreateTime);
        
        List<BusOrder> orders = orderMapper.selectList(wrapper);
        
        List<PendingReturnResponse.ReturnItem> items = new ArrayList<>();
        for (BusOrder order : orders) {
            // 获取格口信息
            IotBox box = boxMapper.selectById(order.getBoxId());
            if (box == null) continue;
            
            // 获取快递柜信息
            IotLocker locker = lockerMapper.selectById(box.getLockerId());
            if (locker == null) continue;
            
            // 计算过期时间和逾期小时数
            LocalDateTime deadline = order.getCreateTime().plusHours(24);
            long overdueHours = Math.abs(deadline.until(LocalDateTime.now(), java.time.temporal.ChronoUnit.HOURS));
            
            PendingReturnResponse.ReturnItem item = PendingReturnResponse.ReturnItem.builder()
                    .expressId(order.getOrderNo())
                    .trackingNo(order.getTrackingNo())
                    .lockerName(locker.getLocation())
                    .compartmentNo(box.getBoxNo())
                    .arrivalTime(order.getCreateTime().format(formatter))
                    .deadline(deadline.format(formatter))
                    .overdueHours((int) overdueHours)
                    .receiverName("赵六") // 模拟收件人姓名
                    .receiverPhone(maskPhone(order.getReceiverPhone()))
                    .build();
            items.add(item);
        }
        
        return PendingReturnResponse.builder()
                .total(items.size())
                .list(items)
                .build();
    }
    
    @Override
    public void openCompartment(OpenCompartmentRequest request, Long courierId) {
        // 这里简化处理，实际应该验证快递员权限和格口状态
        // OpenCompartmentRequest只包含expressId，没有其他getter方法
        log.info("快递员开柜操作：快递ID={}", request.getExpressId());
        
        // 模拟开柜操作
        log.info("柜门已打开");
    }
    
    /**
     * 手机号脱敏
     */
    private String maskPhone(String phone) {
        if (phone == null || phone.length() != 11) {
            return phone;
        }
        // 手机号脱敏，保留前3位和后4位
        return phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
    }
}