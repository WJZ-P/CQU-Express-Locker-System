package com.cqu.express.repository;

import com.cqu.express.entity.StorageRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StorageRecordRepository extends JpaRepository<StorageRecord, Long> {
    Page<StorageRecord> findAll(Pageable pageable);
}
