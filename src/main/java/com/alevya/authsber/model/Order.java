package com.alevya.authsber.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "t_order")
@Getter
@Setter
public final class Order {

    @Id
    @SequenceGenerator(name = "default_generator",
            sequenceName = "order_seq",
            allocationSize = 20)
    private Long id;

    @NotNull
    private LocalDate date;

    @NotNull
    @Column(name = "time_start")
    private LocalTime timeStart;

    @NotNull
    @Column(name = "time_finish")
    private LocalTime timeFinish;

    @Column(name = "worker_id")
    private Long workerId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "workplace_id")
    private Workplace workplace;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "order_service",
            joinColumns = @JoinColumn(name = "service_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "order_id", referencedColumnName = "id"))
    @Singular("service")
    private Set<Service> services = new HashSet<>();

    private String comment;

    @Column(name = "client_id")
    private Long clientId;

    public Order() {
    }

    @Builder
    public Order(Long id,
                 LocalDate date,
                 LocalTime timeStart,
                 LocalTime timeFinish,
                 Long workerId,
                 Workplace workplace,
                 Set<Service> services,
                 String comment,
                 Long clientId) {
        this.id = id;
        this.date = date;
        this.timeStart = timeStart;
        this.timeFinish = timeFinish;
        this.workerId = workerId;
        this.workplace = workplace;
        this.services = services;
        this.comment = comment;
        this.clientId = clientId;
    }

    public boolean addService(Service service) {
        return services.add(service);
    }

    public boolean removeService(Service service) {
        return services.remove(service);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message)) return false;

        Order order = (Order) o;

        if (!Objects.equals(date, order.date)) return false;
        if (!Objects.equals(timeStart, order.timeStart)) return false;
        if (!Objects.equals(timeFinish, order.timeFinish)) return false;
        if (!Objects.equals(workerId, order.workerId)) return false;
        if (!Objects.equals(workplace, order.workplace)) return false;
        return Objects.equals(clientId, order.clientId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                date,
                timeStart,
                timeFinish,
                workerId,
                workplace,
                clientId);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", date=" + date +
                ", timeStart=" + timeStart +
                ", timeFinish=" + timeFinish +
                ", workerId=" + workerId +
                ", workplaceId=" + workplace.getId() +
                ", description='" + comment + '\'' +
                ", clientId=" + clientId +
                '}';
    }
}
