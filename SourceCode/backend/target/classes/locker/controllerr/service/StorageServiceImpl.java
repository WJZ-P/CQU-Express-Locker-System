@Service
public class StorageServiceImpl implements StorageService {

    @Autowired
    private StorageOrderMapper storageOrderMapper;

    @Autowired
    private CompartmentMapper compartmentMapper;

    /**
     * 创建寄存订单
     */
    @Override
    @Transactional
    public StorageCreateVO createStorage(StorageCreateDTO dto) {

        Long userId = UserContext.getUserId();

        // 1. 查询并锁定一个可用格口
        Compartment compartment = compartmentMapper
                .selectAvailableForUpdate(dto.getLockerId(), dto.getCompartmentSize());

        if (compartment == null) {
            throw new RuntimeException("暂无可用格口");
        }

        // 2. 生成数据
        String storageId = "ST" + System.currentTimeMillis();
        String openCode = String.valueOf(
                (int) ((Math.random() * 9 + 1) * 100000)
        );

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expireTime = now.plusHours(dto.getDuration());

        BigDecimal fee = calculateFee(dto.getCompartmentSize(), dto.getDuration());

        // 3. 保存寄存订单
        StorageOrder order = new StorageOrder();
        order.setStorageId(storageId);
        order.setUserId(userId);
        order.setLockerId(dto.getLockerId());
        order.setCompartmentNo(compartment.getCompartmentNo());
        order.setCompartmentSize(dto.getCompartmentSize());
        order.setOpenCode(openCode);
        order.setItemDescription(dto.getItemDescription());
        order.setCreateTime(now);
        order.setExpireTime(expireTime);
        order.setFee(fee);
        order.setStatus("active");

        storageOrderMapper.insert(order);

        // 4. 更新格口状态
        compartment.setStatus("occupied");
        compartmentMapper.updateById(compartment);

        // 5. 返回 VO
        StorageCreateVO vo = new StorageCreateVO();
        vo.setStorageId(storageId);
        vo.setCompartmentNo(compartment.getCompartmentNo());
        vo.setOpenCode(openCode);
        vo.setExpireTime(expireTime);
        vo.setFee(fee);

        return vo;
    }

    /**
     * 查询当前用户寄存列表
     */
    @Override
    public List<StorageListItemVO> listMyStorage() {

        Long userId = UserContext.getUserId();

        List<StorageOrder> orders = storageOrderMapper.selectList(
                new LambdaQueryWrapper<StorageOrder>()
                        .eq(StorageOrder::getUserId, userId)
                        .eq(StorageOrder::getStatus, "active")
                        .orderByDesc(StorageOrder::getCreateTime)
        );

        return orders.stream().map(order -> {
            StorageListItemVO vo = new StorageListItemVO();
            BeanUtils.copyProperties(order, vo);
            return vo;
        }).toList();
    }

    /**
     * 费用计算（可单独抽策略）
     */
    private BigDecimal calculateFee(String size, Integer hours) {

        BigDecimal pricePerHour;

        switch (size) {
            case "small" -> pricePerHour = BigDecimal.valueOf(0.5);
            case "medium" -> pricePerHour = BigDecimal.valueOf(1.0);
            case "large" -> pricePerHour = BigDecimal.valueOf(1.5);
            default -> throw new RuntimeException("非法格口类型");
        }

        return pricePerHour.multiply(BigDecimal.valueOf(hours));
    }
}
