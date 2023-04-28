package com.alevya.authsber.model;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public final class Slot {
    private LocalDate date;
    private LocalTime timeStart;
    private LocalTime timeFinish;
    private Long workerId;
    private Long workplaceId;
    private Long serviceId;
    private String description;

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
        int result = date != null ? date.hashCode() : 0;
        result = 31 * result + (timeStart != null ? timeStart.hashCode() : 0);
        result = 31 * result + (timeFinish != null ? timeFinish.hashCode() : 0);
        result = 31 * result + (workerId != null ? workerId.hashCode() : 0);
        result = 31 * result + (workplaceId != null ? workplaceId.hashCode() : 0);
        result = 31 * result + (serviceId != null ? serviceId.hashCode() : 0);
        return result;
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
                ", description='" + description + '\'' +
                '}';
    }
}
