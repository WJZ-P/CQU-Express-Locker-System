@Data
@TableName("locker_compartment")
public class Compartment {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String lockerId;

    private String compartmentNo;

    private String size;

    /**
     * available / occupied
     */
    private String status;
}
