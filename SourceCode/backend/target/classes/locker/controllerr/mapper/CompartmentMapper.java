@Mapper
public interface CompartmentMapper extends BaseMapper<Compartment> {

    /**
     * 查询一个可用格口（行锁）
     */
    @Select("""
        SELECT * FROM locker_compartment
        WHERE locker_id = #{lockerId}
          AND size = #{size}
          AND status = 'available'
        LIMIT 1
        FOR UPDATE
    """)
    Compartment selectAvailableForUpdate(
            @Param("lockerId") String lockerId,
            @Param("size") String size
    );
}
