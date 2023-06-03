package com.alevya.authsber.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

@Entity
@Table(name = "t_order")
@Getter
@Setter
public final class Order {

    @Id
    @Setter(AccessLevel.NONE)
    @SequenceGenerator(name = "order_seq", sequenceName = "order_seq", allocationSize = 1)
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="order_seq")
    private Long id;

    @NotNull
    private LocalDate date;

    @NotNull
    @Column(name = "time_start")
    private LocalTime timeStart;

    @NotNull
    @Column(name = "time_finish")
    private LocalTime timeFinish;

    @Column(name = "worktime_id")
    private Long workTimeId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "workplace_id")
    private Workplace workplace;

    @Column(name = "service_id")
    private Long serviceId;

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
                 Long workTimeId,
                 Workplace workplace,
                 Long serviceId,
                 String comment,
                 Long clientId) {
        this.id = id;
        this.date = date;
        this.timeStart = timeStart;
        this.timeFinish = timeFinish;
        this.workTimeId = workTimeId;
        this.workplace = workplace;
        this.serviceId = serviceId;
        this.comment = comment;
        this.clientId = clientId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message)) return false;

        Order order = (Order) o;

        if (!Objects.equals(date, order.date)) return false;
        if (!Objects.equals(timeStart, order.timeStart)) return false;
        if (!Objects.equals(timeFinish, order.timeFinish)) return false;
        if (!Objects.equals(workTimeId, order.workTimeId)) return false;
        if (!Objects.equals(workplace, order.workplace)) return false;
        if (!Objects.equals(serviceId, order.getServiceId())) return false;
        return Objects.equals(clientId, order.clientId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                date,
                timeStart,
                timeFinish,
                workTimeId,
                workplace,
                serviceId,
                clientId);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", date=" + date +
                ", timeStart=" + timeStart +
                ", timeFinish=" + timeFinish +
                ", workerId=" + workTimeId +
                ", workplaceId=" + workplace.getId() +
                ", serviceId=" + serviceId +
                ", description='" + comment + '\'' +
                ", clientId=" + clientId +
                '}';
    }
}
