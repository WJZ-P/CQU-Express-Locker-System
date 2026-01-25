package com.cqu.locker.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cqu.locker.entity.BusOrder;
import com.cqu.locker.entity.IotBox;
import com.cqu.locker.entity.IotLocker;
import com.cqu.locker.entity.dto.*;
import com.cqu.locker.mapper.BusOrderMapper;
import com.cqu.locker.mapper.IotBoxMapper;
import com.cqu.locker.mapper.IotLockerMapper;
import com.cqu.locker.service.ExpressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 快递服务实现类
 */
@Slf4j
@Service
public class ExpressServiceImpl implements ExpressService {
    
    @Autowired
    private BusOrderMapper orderMapper;
    
    @Autowired
    private IotBoxMapper boxMapper;
    
    @Autowired
    private IotLockerMapper lockerMapper;
    
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Override
    public ExpressPendingResponse getPendingList(Long userId) {
        // 查询用户的待取快递订单（type=1表示投递，status=0表示待取）
        LambdaQueryWrapper<BusOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BusOrder::getUserId, userId)
               .eq(BusOrder::getType, 1) // 投递类型
               .eq(BusOrder::getStatus, 0) // 待取状态
               .orderByDesc(BusOrder::getCreateTime);
        
        List<BusOrder> orders = orderMapper.selectList(wrapper);
        
        List<ExpressPendingResponse.ExpressItem> items = new ArrayList<>();
        for (BusOrder order : orders) {
            // 获取格口信息
            IotBox box = boxMapper.selectById(order.getBoxId());
            if (box == null) continue;
            
            // 获取快递柜信息
            IotLocker locker = lockerMapper.selectById(box.getLockerId());
            if (locker == null) continue;
            
            // 计算截止时间（创建时间+24小时）
            LocalDateTime deadline = order.getCreateTime().plusHours(24);
            
            ExpressPendingResponse.ExpressItem item = ExpressPendingResponse.ExpressItem.builder()
                    .expressId(order.getOrderNo())
                    .trackingNo(order.getTrackingNo())
                    .company(mapCompany(order.getTrackingNo()))
                    .lockerName(locker.getLocation())
                    .compartmentNo(box.getBoxNo())
                    .status("pending")
                    .arrivalTime(order.getCreateTime().format(formatter))
                    .pickupCode(order.getPickupCode())
                    .deadline(deadline.format(formatter))
                    .build();
            items.add(item);
        }
        
        return ExpressPendingResponse.builder()
                .total(items.size())
                .list(items)
                .build();
    }
    
    @Override
    public ExpressDetailResponse getExpressDetail(String expressId, Long userId) {
        // 查询订单：支持订单号或追踪号查询
        LambdaQueryWrapper<BusOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BusOrder::getUserId, userId)
               .and(w -> w.eq(BusOrder::getOrderNo, expressId)
                         .or()
                         .eq(BusOrder::getTrackingNo, expressId));
        BusOrder order = orderMapper.selectOne(wrapper);
        
        if (order == null) {
            throw new RuntimeException("快递不存在");
        }
        
        // 获取格口信息
        IotBox box = boxMapper.selectById(order.getBoxId());
        if (box == null) {
            throw new RuntimeException("格口信息不存在");
        }
        
        // 获取快递柜信息
        IotLocker locker = lockerMapper.selectById(box.getLockerId());
        if (locker == null) {
            throw new RuntimeException("快递柜信息不存在");
        }
        
        // 格口尺寸映射
        String sizeStr = "medium";
        if (box.getSize() == 1) sizeStr = "small";
        else if (box.getSize() == 3) sizeStr = "large";
        
        // 计算截止时间
        LocalDateTime deadline = order.getCreateTime().plusHours(24);
        
        return ExpressDetailResponse.builder()
                .expressId(order.getOrderNo())
                .trackingNo(order.getTrackingNo())
                .company(mapCompany(order.getTrackingNo()))
                .lockerName(locker.getLocation())
                .lockerAddress(locker.getLocation())
                .compartmentNo(box.getBoxNo())
                .compartmentSize(sizeStr)
                .status(order.getStatus() == 0 ? "pending" : "picked")
                .arrivalTime(order.getCreateTime().format(formatter))
                .pickupCode(order.getPickupCode())
                .deadline(deadline.format(formatter))
                .senderName("寄件人") // 数据库中没有该字段
                .senderPhone("139****9000")
                .receiverName("收件人")
                .receiverPhone(maskPhone(order.getReceiverPhone()))
                .build();
    }
    
    @Override
    @Transactional
    public PickupResponse pickup(PickupRequest request, Long userId) {
        // 查询订单：支持订单号或追踪号查询
        LambdaQueryWrapper<BusOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BusOrder::getUserId, userId)
               .and(w -> w.eq(BusOrder::getOrderNo, request.getExpressId())
                         .or()
                         .eq(BusOrder::getTrackingNo, request.getExpressId()));
        BusOrder order = orderMapper.selectOne(wrapper);
        
        if (order == null) {
            throw new RuntimeException("快递不存在");
        }
        
        if (order.getStatus() != 0) {
            throw new RuntimeException("该快递已取件");
        }
        
        // 验证取件码
        if (!order.getPickupCode().equals(request.getPickupCode())) {
            throw new RuntimeException("取件码错误");
        }
        
        // 获取格口信息
        IotBox box = boxMapper.selectById(order.getBoxId());
        if (box == null) {
            throw new RuntimeException("格口信息不存在");
        }
        
        // 获取快递柜信息
        IotLocker locker = lockerMapper.selectById(box.getLockerId());
        if (locker == null) {
            throw new RuntimeException("快递柜信息不存在");
        }
        
        // 更新订单状态为已取
        order.setStatus(1);
        order.setFinishTime(LocalDateTime.now());
        orderMapper.updateById(order);
        
        // 更新格口状态为空闲
        box.setStatus(0);
        boxMapper.updateById(box);
        
        // 模拟开柜操作
        log.info("开启柜门：快递柜={}, 格口={}", locker.getLocation(), box.getBoxNo());
        
        return PickupResponse.builder()
                .compartmentNo(box.getBoxNo())
                .lockerName(locker.getLocation())
                .build();
    }
    
    @Override
    public void openCompartment(OpenCompartmentRequest request, Long userId) {
        // 查询订单：支持订单号或追踪号查询
        LambdaQueryWrapper<BusOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BusOrder::getUserId, userId)
               .and(w -> w.eq(BusOrder::getOrderNo, request.getExpressId())
                         .or()
                         .eq(BusOrder::getTrackingNo, request.getExpressId()));
        BusOrder order = orderMapper.selectOne(wrapper);
        
        if (order == null) {
            throw new RuntimeException("快递不存在");
        }
        
        if (order.getStatus() != 1) {
            throw new RuntimeException("该快递未取件或已超时");
        }
        
        // 获取格口信息
        IotBox box = boxMapper.selectById(order.getBoxId());
        if (box == null) {
            throw new RuntimeException("格口信息不存在");
        }
        
        // 获取快递柜信息
        IotLocker locker = lockerMapper.selectById(box.getLockerId());
        if (locker == null) {
            throw new RuntimeException("快递柜信息不存在");
        }
        
        // 模拟开柜操作
        log.info("再次开启柜门：快递柜={}, 格口={}", locker.getLocation(), box.getBoxNo());
    }
    
    @Override
    @Transactional
    public SendExpressResponse sendExpress(SendExpressRequest request, Long userId) {
        // 生成订单号
        String orderNo = "SO" + DateUtil.format(LocalDateTime.now(), "yyyyMMddHHmmss") + RandomUtil.randomNumbers(3);
        
        // 创建寄件订单（暂不分配格口，等待快递员揽收）
        BusOrder order = new BusOrder();
        order.setOrderNo(orderNo);
        order.setType(2); // 寄件类型
        order.setUserId(userId);
        order.setReceiverPhone(request.getReceiverPhone());
        order.setStatus(0); // 待揽收
        order.setCreateTime(LocalDateTime.now());
        
        // 这里暂时设置一个临时的boxId（实际应该等快递员揽收后分配）
        // 为了满足数据库非空约束，先设置为1
        order.setBoxId(1L);
        
        orderMapper.insert(order);
        
        // 预估揽收时间（2小时后）
        LocalDateTime estimatedTime = LocalDateTime.now().plusHours(2);
        
        // 预估费用（简单计算）
        BigDecimal fee = new BigDecimal("15.00");
        
        return SendExpressResponse.builder()
                .orderId(orderNo)
                .estimatedPickupTime(estimatedTime.format(formatter))
                .estimatedFee(fee)
                .build();
    }
    
    /**
     * 根据追踪单号前缀映射快递公司名称
     */
    private String mapCompany(String trackingNo) {
        if (trackingNo == null || trackingNo.isEmpty()) {
            return "未知快递";
        }
        
        String upperTrackingNo = trackingNo.toUpperCase();
        
        if (upperTrackingNo.startsWith("SF")) {
            return "顺丰快递";
        } else if (upperTrackingNo.startsWith("YT")) {
            return "圆通快递";
        } else if (upperTrackingNo.startsWith("ZTO")) {
            return "中通快递";
        } else if (upperTrackingNo.startsWith("STO")) {
            return "申通快递";
        } else if (upperTrackingNo.startsWith("JD")) {
            return "京东快递";
        } else {
            return "未知快递";
        }
    }
    
    /**
     * 手机号脱敏
     */
    private String maskPhone(String phone) {
        if (phone == null || phone.length() != 11) {
            return phone;
        }
        return phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
    }
}
