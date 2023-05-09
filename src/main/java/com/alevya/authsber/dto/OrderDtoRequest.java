package com.alevya.authsber.dto;

import com.alevya.authsber.model.Service;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@ToString
public class OrderDtoRequest {
    private LocalDate date;
    private LocalTime timeStart;
    private LocalTime timeFinish;
    private Long workerId;
    private Long workplaceId;
    @Singular("service")
    private Set<Service> services;
    private String comment;
    private Long clientId;
}
