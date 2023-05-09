package com.alevya.authsber.model;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

@Getter
@Setter
public final class Slot {
    private LocalDate date;
    private LocalTime timeStart;
    private LocalTime timeFinish;
    private Long workerId;
    private Long workplaceId;
    private Long serviceId;
    private String comment;

    public Slot() {
    }

    @Builder
    public Slot(LocalDate date,
                LocalTime timeStart,
                LocalTime timeFinish,
                Long workerId,
                Long workplaceId,
                Long serviceId,
                String comment) {
        this.date = date;
        this.timeStart = timeStart;
        this.timeFinish = timeFinish;
        this.workerId = workerId;
        this.workplaceId = workplaceId;
        this.serviceId = serviceId;
        this.comment = comment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Workplace)) return false;

        Slot slot = (Slot) o;

        if (!Objects.equals(date, slot.date)) return false;
        if (!Objects.equals(timeStart, slot.timeStart)) return false;
        if (!Objects.equals(timeFinish, slot.timeFinish)) return false;
        if (!Objects.equals(workerId, slot.workerId)) return false;
        if (!Objects.equals(workplaceId, slot.workplaceId)) return false;
        return Objects.equals(serviceId, slot.serviceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                date,
                timeStart,
                timeFinish,
                workerId,
                workplaceId,
                serviceId
        );
    }

    @Override
    public String toString() {
        return "Slot{" +
                "date=" + date +
                ", timeStart=" + timeStart +
                ", timeFinish=" + timeFinish +
                ", workerId=" + workerId +
                ", workplaceId=" + workplaceId +
                ", serviceId=" + serviceId +
                ", description='" + comment + '\'' +
                '}';
    }
}
