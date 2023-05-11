package com.alevya.authsber.repository;

import com.alevya.authsber.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    Company findByShortName(String shortName);
    Company findByFullName(String fullName);
    boolean existsByShortName(String shortName);
    boolean existsByFullName(String fullName);
    @Query(
            value = "WITH RECURSIVE tmp(id, full_name, short_name, address, description, phone, parent_division_id, deleted) AS (" +
                    "    SELECT id, full_name, short_name, address, description, phone, parent_division_id, deleted FROM t_company WHERE id = ?1" +
                    "  UNION\n" +
                    "    SELECT tc.id, tc.full_name, tc.short_name, tc.address, tc.description, tc.phone, tc.parent_division_id, tc.deleted" +
                    "    FROM tmp t INNER JOIN t_company tc  ON tc.id = t.parent_division_id" +
                    "    )" +
                    "SELECT id, full_name, short_name, address, description, phone, parent_division_id, deleted " +
                    "FROM tmp",
            nativeQuery = true)
    Set<Company> getThisAndAllParentCompanies(@NonNull Long id);

    @Query(
            value = "WITH RECURSIVE tmp(id, full_name, short_name, description, address, phone, parent_division_id, deleted) AS (" +
                    "    SELECT id, full_name, short_name, description, address, phone, parent_division_id, deleted FROM t_company WHERE id = ?1" +
                    "  UNION" +
                    "    SELECT tc.id, tc.full_name, tc.short_name, tc.description, tc.address, tc.phone, tc.parent_division_id, tc.deleted" +
                    "    FROM tmp t INNER JOIN t_company tc  ON t.id = tc.parent_division_id" +
                    "    )" +
                    "SELECT id, full_name, short_name, address, description, phone, parent_division_id, deleted " +
                    "FROM tmp",
            nativeQuery = true)
    Set<Company> getThisAndAllChildCompanies(@NonNull Long id);
}
