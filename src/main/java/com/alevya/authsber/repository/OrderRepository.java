package com.alevya.authsber.repository;

import com.alevya.authsber.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByClientId(Long id);
    List<Order> findByWorkerId(Long id);
    List<Order> findByWorkplaceId(Long id);
    List<Order> findAllByWorkplaceIdAndDate(Long workplaceId, @NotNull LocalDate date);
    List<Order> findAllByClientId(Long client_id);
    @Query(value = "select * from t_order o " +
            "where o.workplace_id = ?1 " +
            "and o.date = ?2 and o.time_start = ?3", nativeQuery = true)
    List<Order> findOrderByAllParam(@NotNull Long workplace_id, @NotNull LocalDate date, @NotNull LocalTime start);

    @Query(value = "select * from t_order o " +
            "where o.workplace_id = ?1 " +
            "and o.date = ?2", nativeQuery = true)
    List<Order> findOrderByWorkplaceIdAndDate(@NotNull Long workplace_id, @NotNull LocalDate date);

    @Query(value = "select * from t_order o " +
            "where o.worker_id = ?1 ", nativeQuery = true)
    List<Order> findAllByWorkerId(Long worker_id);

    @Query(value = "select * from t_order o " +
            "where o.workplace_id = ?1 ", nativeQuery = true)
    List<Order> findAllByWorkplaceId(Long workplace_id);
}
