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
@Table(name = "order")
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
    @Singular
    private Set<Service> services = new HashSet<>();

    private String comment;

    @Column(name = "client_id")
    private Long clientId;

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
        int result = date != null ? date.hashCode() : 0;
        result = 31 * result + (timeStart != null ? timeStart.hashCode() : 0);
        result = 31 * result + (timeFinish != null ? timeFinish.hashCode() : 0);
        result = 31 * result + (workerId != null ? workerId.hashCode() : 0);
        result = 31 * result + (workplace != null ? workplace.hashCode() : 0);
        result = 31 * result + (clientId != null ? clientId.hashCode() : 0);
        return result;
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
