@Data
public class StorageCreateDTO {

    private String lockerId;

    /**
     * small / medium / large
     */
    private String compartmentSize;

    /**
     * 单位：小时
     */
    private Integer duration;

    private String itemDescription;
}
