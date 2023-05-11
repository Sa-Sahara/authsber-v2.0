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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Service
public class SlotService {
    private static final Log log = LogFactory.getLog(SlotService.class);

    private final WorkplaceRepository workplaceRepository;
    private final WorkTimeRepository workTimeRepository;
    private final OrderRepository orderRepository;

    public SlotService(WorkplaceRepository workplaceRepository,
                       WorkTimeRepository workTimeRepository
            , OrderRepository orderRepository) {
        this.workplaceRepository = workplaceRepository;
        this.workTimeRepository = workTimeRepository;
        this.orderRepository = orderRepository;
    }


    public List<Slot> getSlots(Long companyId) {
        if (companyId == null) {
            throw new BadRequestException("Invalid ID");
        }
        log.info("getSlots by company id: " + companyId);
        List<Workplace> workplaceByCompanyId = workplaceRepository.findAllByCompanyId(companyId);
        if (workplaceByCompanyId.size() == 0) {
            throw new NotFoundException("Workplace not found");
        }
        List<Slot> slots = new ArrayList<>();
        for (Workplace workplace : workplaceByCompanyId) {
            List<WorkTime> workTimes = workTimeRepository
                    .findAllByDateBetweenAndWorkplaceId(
                            LocalDate.now()
                            , LocalDate.now().plusDays(7)
                            , workplace.getId()
                    );
            for (WorkTime workTime : workTimes) {
                int slotNumber = (workTime.getFinish().getHour() - workTime.getStart().getHour()) * 4;
                slotNumber += workTime.getFinish().getMinute() / 15;
                slotNumber -= workTime.getStart().getMinute() / 15;
                for (int i = 0; i < slotNumber; i++) {
                    slots.add(new Slot(workTime.getDate()
                            , workTime.getStart().plusMinutes(i * 15L)
                            , workTime.getStart().plusMinutes((i + 1) * 15L)
                            , workTime.getWorker().getId()
                            , workTime.getWorkplace().getId()
                            , workplace.getId()
                            , workplace.getDescription()));
                }
                List<Order> orders = orderRepository.findAllByWorkplaceIdAndDate(
                        workplace.getId()
                        , workTime.getDate());
                for (Order order : orders) {
                    slots.remove(new Slot(workTime.getDate()
                            , order.getTimeStart()
                            , order.getTimeFinish()
                            , workTime.getWorker().getId()
                            , workTime.getWorkplace().getId()
                            , workplace.getId()
                            , workplace.getDescription()));
                }
            }
        }
        return slots;
    }
}
