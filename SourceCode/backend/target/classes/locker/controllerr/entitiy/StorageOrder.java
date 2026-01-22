@Data
@TableName("storage_order")
public class StorageOrder {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String storageId;

    private Long userId;

    private String lockerId;

    private String compartmentNo;

    private String compartmentSize;

    private String openCode;

    private String itemDescription;

    private LocalDateTime createTime;

    private LocalDateTime expireTime;

    private BigDecimal fee;

    /**
     * active / expired / completed
     */
    private String status;
}
