package com.alevya.authsber.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

/**
 * WorkTime
 * represents the working period booked by employee
 * within a certain Workplace.
 */

@Entity
@Table(name = "worktime")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public final class WorkTime {

    @Id
    @SequenceGenerator(name = "worktime_seq", sequenceName = "worktime_seq", allocationSize = 1)
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="worktime_seq")
    private Long id;

    @NotNull
    private LocalDate date;

    @NotNull
    private LocalTime start;

    @NotNull
    private LocalTime finish;

    @ManyToOne(fetch = FetchType.EAGER)
    @NotNull
    @JoinColumn(name = "workplace_id")
    private Workplace workplace;
    @ManyToOne(fetch = FetchType.EAGER)
    @NotNull
    @JoinColumn(name = "user_id")
    private User worker;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WorkTime)) return false;

        WorkTime workTime = (WorkTime) o;

        if (!Objects.equals(date, workTime.date)) return false;
        if (!Objects.equals(start, workTime.start)) return false;
        if (!Objects.equals(finish, workTime.finish)) return false;
        if (!Objects.equals(workplace, workTime.workplace)) return false;
        return Objects.equals(worker, workTime.worker);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, start, finish, workplace, worker);
    }

    @Override
    public String toString() {
        return "WorkTime{" +
                "id=" + id +
                ", date=" + date +
                ", start=" + start +
                ", finish=" + finish +
                ", workplaceId=" + workplace.getId() +
                ", workerId=" + worker.getId() +
                '}';
    }
}
