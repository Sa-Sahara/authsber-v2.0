package com.alevya.authsber.repository;

import com.alevya.authsber.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);

    User findByPhone(String phone);
    User findByEmail(String email);
}
