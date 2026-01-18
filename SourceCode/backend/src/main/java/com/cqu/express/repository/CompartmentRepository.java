package com.cqu.express.repository;

import com.cqu.express.entity.Compartment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CompartmentRepository extends JpaRepository<Compartment, String> {
    List<Compartment> findByLockerId(String lockerId);
}
