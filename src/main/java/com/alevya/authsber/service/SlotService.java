package com.alevya.authsber.service;

import com.alevya.authsber.exception.BadRequestException;
import com.alevya.authsber.exception.NotFoundException;
import com.alevya.authsber.model.Order;
import com.alevya.authsber.model.Slot;
import com.alevya.authsber.model.WorkTime;
import com.alevya.authsber.model.Workplace;
import com.alevya.authsber.repository.OrderRepository;
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

    public SlotService(WorkplaceRepository workplaceRepository,
                       WorkTimeRepository workTimeRepository,
             OrderRepository orderRepository) {
        this.workplaceRepository = workplaceRepository;
        this.workTimeRepository = workTimeRepository;
        this.orderRepository = orderRepository;
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
        int slotsPerHour = (int)(MINUTES_PER_HOUR/slotDuration);
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
}
