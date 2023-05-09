package com.alevya.authsber.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@ToString
public class WorkTimeDtoResponse {
    private Long id;
    private LocalDate date;
    private LocalTime start;
    private LocalTime finish;
    private Long workplaceId;
    private Long workerId;
}
