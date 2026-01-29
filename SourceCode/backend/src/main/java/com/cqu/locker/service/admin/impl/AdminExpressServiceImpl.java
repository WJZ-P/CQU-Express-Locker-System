package com.cqu.locker.service.admin.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cqu.locker.entity.BusOrder;
import com.cqu.locker.entity.IotBox;
import com.cqu.locker.entity.IotLocker;
import com.cqu.locker.entity.SysCourier;
import com.cqu.locker.entity.SysUser;
import com.cqu.locker.entity.dto.admin.*;
import com.cqu.locker.mapper.BusOrderMapper;
import com.cqu.locker.mapper.IotBoxMapper;
import com.cqu.locker.mapper.IotLockerMapper;
import com.cqu.locker.mapper.SysCourierMapper;
import com.cqu.locker.mapper.SysUserMapper;
import com.cqu.locker.service.admin.AdminExpressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 管理端快递订单服务实现类
 */
@Slf4j
@Service
public class AdminExpressServiceImpl implements AdminExpressService {
    
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Autowired
    private BusOrderMapper orderMapper;
    
    @Autowired
    private IotBoxMapper boxMapper;
    
    @Autowired
    private IotLockerMapper lockerMapper;
    
    @Autowired
    private SysCourierMapper courierMapper;
    
    @Autowired
    private SysUserMapper userMapper;
    
    @Override
    public PageResponse<BusOrder> queryOrders(ExpressQueryRequest request) {
        LambdaQueryWrapper<BusOrder> wrapper = new LambdaQueryWrapper<>();
        
        // 关键词搜索（使用trackingNo替代expressNo，因为expressNo是非数据库字段）
        if (StringUtils.hasText(request.getKeyword())) {
            wrapper.and(w -> w
                    .like(BusOrder::getTrackingNo, request.getKeyword())
                    .or().like(BusOrder::getOrderNo, request.getKeyword())
                    .or().like(BusOrder::getReceiverPhone, request.getKeyword())
                    .or().like(BusOrder::getPickupCode, request.getKeyword())
            );
        }
        
        // 状态筛选
        if (request.getStatus() != null) {
            wrapper.eq(BusOrder::getStatus, request.getStatus());
        }
        
        // 快递公司筛选
        if (StringUtils.hasText(request.getCompany())) {
            wrapper.eq(BusOrder::getCompany, request.getCompany());
        }
        
        // 快递柜筛选（通过boxId关联）
        if (request.getLockerId() != null) {
            LambdaQueryWrapper<IotBox> boxWrapper = new LambdaQueryWrapper<>();
            boxWrapper.eq(IotBox::getLockerId, request.getLockerId());
            boxWrapper.select(IotBox::getId);
            List<IotBox> boxes = boxMapper.selectList(boxWrapper);
            if (!boxes.isEmpty()) {
                List<Long> boxIds = boxes.stream().map(IotBox::getId).collect(java.util.stream.Collectors.toList());
                wrapper.in(BusOrder::getBoxId, boxIds);
            } else {
                // 该快递柜没有格口，返回空结果
                wrapper.eq(BusOrder::getId, -1);
            }
        }
        
        // 快递员筛选
        if (request.getCourierId() != null) {
            wrapper.eq(BusOrder::getCourierId, request.getCourierId());
        }
        
        // 时间范围筛选
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (StringUtils.hasText(request.getStartTime())) {
            wrapper.ge(BusOrder::getCreateTime, LocalDateTime.parse(request.getStartTime(), formatter));
        }
        if (StringUtils.hasText(request.getEndTime())) {
            wrapper.le(BusOrder::getCreateTime, LocalDateTime.parse(request.getEndTime(), formatter));
        }
        
        // 排序
        if (StringUtils.hasText(request.getSortBy())) {
            boolean isAsc = "asc".equalsIgnoreCase(request.getSortOrder());
            switch (request.getSortBy()) {
                case "createTime":
                    wrapper.orderBy(true, isAsc, BusOrder::getCreateTime);
                    break;
                case "finishTime":
                    wrapper.orderBy(true, isAsc, BusOrder::getFinishTime);
                    break;
                default:
                    wrapper.orderByDesc(BusOrder::getCreateTime);
            }
        } else {
            wrapper.orderByDesc(BusOrder::getCreateTime);
        }
        
        // 分页查询
        Page<BusOrder> page = new Page<>(request.getPage(), request.getPageSize());
        Page<BusOrder> result = orderMapper.selectPage(page, wrapper);
        
        // 填充关联信息
        List<BusOrder> orders = result.getRecords();
        for (BusOrder order : orders) {
            fillOrderDetails(order);
        }
        
        return PageResponse.of(orders, result.getTotal(), 
                request.getPage(), request.getPageSize());
    }
    
    /**
     * 填充订单的关联信息
     */
    private void fillOrderDetails(BusOrder order) {
        // 设置expressNo为trackingNo
        order.setExpressNo(order.getTrackingNo());
        
        // 格式化时间
        if (order.getCreateTime() != null) {
            order.setDepositTime(order.getCreateTime().format(TIME_FORMATTER));
        }
        if (order.getFinishTime() != null) {
            order.setPickupTime(order.getFinishTime().format(TIME_FORMATTER));
        }
        
        // 获取格口和快递柜信息
        if (order.getBoxId() != null) {
            IotBox box = boxMapper.selectById(order.getBoxId());
            if (box != null) {
                order.setBoxNo(box.getBoxNo());
                order.setLockerId(box.getLockerId());
                
                // 获取快递柜信息
                IotLocker locker = lockerMapper.selectById(box.getLockerId());
                if (locker != null) {
                    order.setLockerName(locker.getName() != null ? locker.getName() : locker.getSerialNo());
                }
            }
        }
        
        // 获取快递员信息
        if (order.getCourierId() != null) {
            SysCourier courier = courierMapper.selectById(order.getCourierId());
            if (courier != null) {
                order.setCourierName(courier.getName());
                // 从关联的用户获取电话
                if (courier.getUserId() != null) {
                    SysUser courierUser = userMapper.selectById(courier.getUserId());
                    if (courierUser != null) {
                        order.setCourierPhone(courierUser.getPhone());
                    }
                }
            }
        }
    }
    
    @Override
    public BusOrder getOrderById(Long id) {
        BusOrder order = orderMapper.selectById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        // 填充关联信息
        fillOrderDetails(order);
        return order;
    }
    
    @Override
    @Transactional
    public void deleteOrder(Long id) {
        BusOrder order = orderMapper.selectById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        // 如果订单未取件，需要释放格口
        if (order.getStatus() == 0 && order.getBoxId() != null) {
            IotBox updateBox = new IotBox();
            updateBox.setId(order.getBoxId());
            updateBox.setStatus(0); // 空闲
            updateBox.setUpdateTime(LocalDateTime.now());
            boxMapper.updateById(updateBox);
        }
        
        orderMapper.deleteById(id);
    }
    
    @Override
    @Transactional
    public void forcePickup(Long id) {
        BusOrder order = orderMapper.selectById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        if (order.getStatus() != 0) {
            throw new RuntimeException("该订单不是待取件状态");
        }
        
        // 更新订单状态
        BusOrder updateOrder = new BusOrder();
        updateOrder.setId(id);
        updateOrder.setStatus(1); // 已取件
        updateOrder.setFinishTime(LocalDateTime.now());
        updateOrder.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(updateOrder);
        
        // 释放格口
        if (order.getBoxId() != null) {
            IotBox updateBox = new IotBox();
            updateBox.setId(order.getBoxId());
            updateBox.setStatus(0); // 空闲
            updateBox.setUpdateTime(LocalDateTime.now());
            boxMapper.updateById(updateBox);
        }
        
        log.info("管理员强制取件: 订单ID={}", id);
    }
    
    @Override
    @Transactional
    public void markOvertime(Long id) {
        BusOrder order = orderMapper.selectById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        if (order.getStatus() != 0) {
            throw new RuntimeException("该订单不是待取件状态");
        }
        
        BusOrder updateOrder = new BusOrder();
        updateOrder.setId(id);
        updateOrder.setStatus(2); // 已超时
        updateOrder.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(updateOrder);
        
        log.info("管理员标记超时: 订单ID={}", id);
    }
    
    @Override
    @Transactional
    public void returnExpress(Long id) {
        BusOrder order = orderMapper.selectById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        if (order.getStatus() != 0 && order.getStatus() != 2) {
            throw new RuntimeException("只能退回待取件或超时的订单");
        }
        
        // 更新订单状态
        BusOrder updateOrder = new BusOrder();
        updateOrder.setId(id);
        updateOrder.setStatus(3); // 已退回
        updateOrder.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(updateOrder);
        
        // 释放格口
        if (order.getBoxId() != null) {
            IotBox updateBox = new IotBox();
            updateBox.setId(order.getBoxId());
            updateBox.setStatus(0); // 空闲
            updateBox.setUpdateTime(LocalDateTime.now());
            boxMapper.updateById(updateBox);
        }
        
        log.info("管理员退回快递: 订单ID={}", id);
    }
}
