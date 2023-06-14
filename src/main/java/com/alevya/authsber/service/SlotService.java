package com.alevya.authsber.service;

import com.alevya.authsber.dto.CompanyDtoResponse;
import com.alevya.authsber.exception.BadRequestException;
import com.alevya.authsber.exception.NotFoundException;
import com.alevya.authsber.model.*;
import com.alevya.authsber.repository.OrderRepository;
import com.alevya.authsber.repository.ServiceRepository;
import com.alevya.authsber.repository.WorkTimeRepository;
import com.alevya.authsber.repository.WorkplaceRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class SlotService {

    private final WorkplaceRepository workplaceRepository;
    private final WorkTimeRepository workTimeRepository;
    private final OrderRepository orderRepository;
    private final long MINUTES_PER_HOUR = 60;
    @Value("${slot.horizon}")
    private long slotHorizon;
    @Value("${slot.duration}")
    private long slotDuration;
    private final ServiceRepository serviceRepository;
    private final CompanyService companyService;

    public SlotService(WorkplaceRepository workplaceRepository,
                       WorkTimeRepository workTimeRepository,
                       OrderRepository orderRepository,
                       ServiceRepository serviceRepository,
                       CompanyService companyService) {
        this.workplaceRepository = workplaceRepository;
        this.workTimeRepository = workTimeRepository;
        this.orderRepository = orderRepository;
        this.serviceRepository = serviceRepository;
        this.companyService = companyService;
    }

    public List<Slot> getSlots(Long companyId) {
        if (companyId == null) {
            throw new BadRequestException("Invalid ID");
        }
        List<Workplace> workplaceByCompanyId = workplaceRepository.findAllByCompanyId(companyId);
        if (workplaceByCompanyId.size() == 0) {
            throw new NotFoundException("Workplace not found");
        }
        List<Slot> slots = new ArrayList<>();
        int slotsPerHour = (int) (MINUTES_PER_HOUR / slotDuration);
        for (Workplace workplace : workplaceByCompanyId) {
            List<WorkTime> workTimes = workTimeRepository
                    .findAllByDateBetweenAndWorkplaceId(
                            LocalDate.now(),
                            LocalDate.now().plusDays(slotHorizon),
                            workplace.getId()
                    );
            for (WorkTime workTime : workTimes) {
                int slotNumber = (workTime.getFinish().getHour() - workTime.getStart().getHour())
                        * slotsPerHour;
                slotNumber += workTime.getFinish().getMinute() / slotDuration;
                slotNumber -= workTime.getStart().getMinute() / slotDuration;
                for (int i = 0; i < slotNumber; i++) {
                    slots.add(new Slot(workTime.getDate(),
                            workTime.getStart().plusMinutes(i * slotDuration),
                            workTime.getStart().plusMinutes((i + 1) * slotDuration),
                            workTime.getWorker().getId(),
                            workTime.getWorkplace().getId(),
                            workplace.getId(),
                            workplace.getDescription()));
                }
                List<Order> orders = orderRepository.findAllByWorkplaceIdAndDate(
                        workplace.getId(),
                        workTime.getDate());
                for (Order order : orders) {
                    slots.remove(new Slot(workTime.getDate(),
                            order.getTimeStart(),
                            order.getTimeFinish(),
                            workTime.getWorker().getId(),
                            workTime.getWorkplace().getId(),
                            workplace.getId(),
                            workplace.getDescription()));
                }
            }
        }
        return slots;
    }

    public List<Slot> getSlotsByServiceCompanyDay(String serviceName, String address, LocalDate date) {
        if (serviceName.isBlank() || address.isBlank() || date == null) {
            throw new BadRequestException("service, address, date should not be empty");
        }

        CompanyDtoResponse company = companyService.getCompanyByAddress(address);
        long companyId = company.getId();

        List<Workplace> workplaceByCompanyId = workplaceRepository.findAllByCompanyId(companyId);
        if (workplaceByCompanyId.size() == 0) {
            throw new NotFoundException("Workplace not found");
        }

        com.alevya.authsber.model.Service service = serviceRepository.findByName(serviceName);
        long serviceId = service.getId();

        List<Slot> slots = new ArrayList<>();
        int slotsPerHour = (int) (MINUTES_PER_HOUR / slotDuration);
        for (Workplace workplace : workplaceByCompanyId) {
            List<WorkTime> workTimes = workTimeRepository
                    .findAllByDateBetweenAndWorkplaceId(
                            date,
                            date,
                            workplace.getId()
                    );
            for (WorkTime workTime : workTimes) {
                int slotNumber = (workTime.getFinish().getHour() - workTime.getStart().getHour())
                        * slotsPerHour;
                slotNumber += workTime.getFinish().getMinute() / slotDuration;
                slotNumber -= workTime.getStart().getMinute() / slotDuration;
                for (int i = 0; i < slotNumber; i++) {
                    slots.add(
                            new Slot(workTime.getDate(),
                                    workTime.getStart().plusMinutes(i * slotDuration),
                                    workTime.getStart().plusMinutes((i + 1) * slotDuration),
                                    workTime.getId(),
                                    workTime.getWorkplace().getId(),
                                    serviceId,
                                    ""
                            ));
                }
                List<Order> orders = orderRepository.findAllByWorkplaceIdAndDate(
                        workplace.getId(),
                        workTime.getDate());
                for (Order order : orders) {
                    slots.remove(new Slot(workTime.getDate(),
                            order.getTimeStart(),
                            order.getTimeFinish(),
                            workTime.getWorker().getId(),
                            workTime.getWorkplace().getId(),
                            workplace.getId(),
                            workplace.getDescription()));
                }
            }
        }
        return slots;
    }
}
