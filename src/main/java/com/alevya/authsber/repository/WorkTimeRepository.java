package com.alevya.authsber.repository;

import com.alevya.authsber.model.WorkTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface WorkTimeRepository extends JpaRepository<WorkTime, Long> {
    @Query(value = "select * from worktime w " +
            "where w.user_id = ?1 " +
            "and w.date = ?2", nativeQuery = true)
    List<WorkTime> findAllByWorkerAndDate(@NonNull Long worker, @NonNull LocalDate date);

    @Query(value = "select * from worktime w " +
            "where w.workplace_id = ?1 " +
            "and w.date = ?2", nativeQuery = true)
    List<WorkTime> findAllByWorkplaceAndDate(@NonNull Long workplace, @NonNull LocalDate date);

    List<WorkTime> findAllByDateBetweenAndWorkplaceId(LocalDate start, LocalDate finish, Long id);

    List<WorkTime> findAllByWorkplaceId(@NonNull Long workplaceId);
}
