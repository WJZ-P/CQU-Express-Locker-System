package com.cqu.express.repository;

import com.cqu.express.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    Page<User> findByNameContainingOrPhoneContaining(String name, String phone, Pageable pageable);
}
