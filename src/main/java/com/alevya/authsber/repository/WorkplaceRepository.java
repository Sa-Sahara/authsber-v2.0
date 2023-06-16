package com.alevya.authsber.repository;

import com.alevya.authsber.model.Workplace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkplaceRepository extends JpaRepository<Workplace, Long> {
    Workplace findByName(String name);
    boolean existsByName(String name);
    @Query(value = "select * from t_workplace w " +
            "where w.company_id = ?1", nativeQuery = true)
    List<Workplace> findAllByCompanyId(@NonNull Long companyId);
}
