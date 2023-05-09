package com.alevya.authsber.repository;

import com.alevya.authsber.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Permission findByName(String name);
    boolean existsByName(String name);
    @Query(value = "select * from permission o " +
            "where o.name like ?1 ", nativeQuery = true)
    List<Permission> findAllByNameLike(@NonNull String partName);
}
