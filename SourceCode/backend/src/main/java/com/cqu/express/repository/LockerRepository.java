package com.cqu.express.repository;

import com.cqu.express.entity.Locker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LockerRepository extends JpaRepository<Locker, String> {
    @Query("SELECT l FROM Locker l WHERE (:id IS NULL OR l.id LIKE %:id%) AND (:location IS NULL OR l.location LIKE %:location%)")
    Page<Locker> findByCondition(String id, String location, Pageable pageable);
}
