package com.alevya.authsber.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@ToString
public class OrderDtoResponse {
    private Long id;
    private LocalDate date;
    private LocalTime timeStart;
    private LocalTime timeFinish;
    private Long workTimeId;
    private Long workplaceId;
    private Long serviceId;
    private String comment;
    private Long clientId;
}
