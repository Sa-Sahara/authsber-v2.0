package com.alevya.authsber.repository;

import com.alevya.authsber.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    Company findByShortName(String shortName);
    Company findByFullName(String fullName);
    boolean existsByShortName(String shortName);
    boolean existsByFullName(String fullName);
}
