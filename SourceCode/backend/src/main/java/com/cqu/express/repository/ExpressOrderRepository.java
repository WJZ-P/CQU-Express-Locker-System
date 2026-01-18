package com.cqu.express.repository;

import com.cqu.express.entity.ExpressOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;

public interface ExpressOrderRepository extends JpaRepository<ExpressOrder, String> {
    @Query("SELECT e FROM ExpressOrder e WHERE (:trackingNo IS NULL OR e.trackingNo LIKE %:trackingNo%) AND (:phone IS NULL OR e.receiverPhone LIKE %:phone%) AND (:status IS NULL OR e.status = :status)")
    Page<ExpressOrder> findByCondition(String trackingNo, String phone, String status, Pageable pageable);

    long countByInTimeAfter(LocalDateTime time);
}
