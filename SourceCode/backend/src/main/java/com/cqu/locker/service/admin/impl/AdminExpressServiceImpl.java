package com.cqu.locker.service.admin.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cqu.locker.entity.BusOrder;
import com.cqu.locker.entity.IotBox;
import com.cqu.locker.entity.dto.admin.*;
import com.cqu.locker.mapper.BusOrderMapper;
import com.cqu.locker.mapper.IotBoxMapper;
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
    
    @Autowired
    private BusOrderMapper orderMapper;
    
    @Autowired
    private IotBoxMapper boxMapper;
    
    @Override
    public PageResponse<BusOrder> queryOrders(ExpressQueryRequest request) {
        LambdaQueryWrapper<BusOrder> wrapper = new LambdaQueryWrapper<>();
        
        // 关键词搜索
        if (StringUtils.hasText(request.getKeyword())) {
            wrapper.and(w -> w
                    .like(BusOrder::getExpressNo, request.getKeyword())
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
        
        return PageResponse.of(result.getRecords(), result.getTotal(), 
                request.getPage(), request.getPageSize());
    }
    
    @Override
    public BusOrder getOrderById(Long id) {
        BusOrder order = orderMapper.selectById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
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
