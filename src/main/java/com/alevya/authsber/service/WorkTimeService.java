package com.alevya.authsber.service;

import com.alevya.authsber.dto.WorkTimeDtoRequest;
import com.alevya.authsber.dto.WorkTimeDtoResponse;
import com.alevya.authsber.exception.BadRequestException;
import com.alevya.authsber.exception.NotFoundException;
import com.alevya.authsber.model.User;
import com.alevya.authsber.model.WorkTime;
import com.alevya.authsber.model.Workplace;
import com.alevya.authsber.repository.UserRepository;
import com.alevya.authsber.repository.WorkTimeRepository;
import com.alevya.authsber.repository.WorkplaceRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorkTimeService {
    private static final Log log = LogFactory.getLog(WorkTimeService.class);
    private static final int MAX_WORK_HOURS = 24;
    private final WorkTimeRepository workTimeRepository;
    private final WorkplaceRepository workplaceRepository;
    private final UserRepository userRepository;


    public WorkTimeService(WorkTimeRepository workTimeRepository,
                           WorkplaceRepository workplaceRepository,
                           UserRepository userRepository) {
        this.workTimeRepository = workTimeRepository;
        this.workplaceRepository = workplaceRepository;
        this.userRepository = userRepository;
    }

    public WorkTimeDtoResponse createWorkTime(
            WorkTimeDtoRequest workTimeDtoRequest) {
        log.info("createWorkTime workTimeDtoRequest: " + workTimeDtoRequest);
        //fields check
        if (workTimeDtoRequest == null) {
            throw new BadRequestException("Invalid workTime");
        }
        if (workTimeDtoRequest.getDate() == null
                || workTimeDtoRequest.getDate().isBefore(LocalDate.now())) {
            throw new BadRequestException("Invalid Date");
        }
        if (workTimeDtoRequest.getStart() == null) {
            throw new BadRequestException("Invalid start of working period");
        }
        if (workTimeDtoRequest.getFinish() == null) {
            throw new BadRequestException("Invalid end of working period");
        }
        if (Duration.between(
                workTimeDtoRequest.getFinish(),
                workTimeDtoRequest.getStart()).toHours() > MAX_WORK_HOURS) {
            throw new BadRequestException("Too long working day.");
        }
        if (workTimeDtoRequest.getWorkplaceId() == null) {
            throw new BadRequestException("Invalid Workplace");
        }
        if (workTimeDtoRequest.getUserId() == null) {
            throw new BadRequestException("Invalid Worker");
        }
        // check that this worker has not booked this time
        checkIfWorkerBusy(workTimeDtoRequest);
        // check that this workplace is not booked
        checkIfWorkplaceBooked(workTimeDtoRequest);

        return mapToWorkTimeDto(workTimeRepository.save(
                mapToWorkTime(workTimeDtoRequest)));
    }

    public WorkTimeDtoResponse getWorkTimeById(Long id) {
        log.info("getWorkTimeById id: " + id);
        if (id == null) {
            throw new BadRequestException("Invalid ID");
        }
        return mapToWorkTimeDto(workTimeRepository.findById(id).orElseThrow(()
                -> new NotFoundException("WorkTime not found!")));
    }

    public List<WorkTimeDtoResponse> getAllWorkTimes() {
        log.info("getAllWorkTimes");
        return workTimeRepository.findAll().stream()
                .map(this::mapToWorkTimeDto)
                .collect(Collectors.toList());
    }

    public List<WorkTimeDtoResponse> getBookedWorkTimesByUserAndDate(
            User user, LocalDate localDate) {
        log.info("getBookedWorkTimesByUserAfterDate by User id "
                + user.getId() + " by date " + localDate);
        return workTimeRepository.findAllByWorkerAndDate(user.getId(), localDate)
                .stream()
                .map(this::mapToWorkTimeDto)
                .collect(Collectors.toList());
    }

    public List<WorkTimeDtoResponse> getBookedWorkTimesByWorkplaceAndDate(
            Workplace workplace, LocalDate localDate) {
        log.info("getBookedWorkTimesByWorkplaceForDate by Workplace id "
                + workplace.getId() + " by date " + localDate);
        return workTimeRepository.findAllByWorkplaceAndDate(workplace.getId(), localDate)
                .stream()
                .map(this::mapToWorkTimeDto)
                .collect(Collectors.toList());
    }

    public WorkTimeDtoResponse updateWorkTime(
            Long id,
            WorkTimeDtoRequest workTimeDtoRequest) {
        log.info("updateWorkTime id: " + id + " workTimeDtoRequest: " + workTimeDtoRequest);

        if (workTimeDtoRequest == null
                || workTimeDtoRequest.getDate() == null
                || workTimeDtoRequest.getStart() == null
                || workTimeDtoRequest.getFinish() == null
                || workTimeDtoRequest.getUserId() == null
                || workTimeDtoRequest.getWorkplaceId() == null
        ) {
            throw new BadRequestException("Invalid workTimeDtoRequest");
        }
        WorkTime oldWorkTime = workTimeRepository.findById(id).orElseThrow(()
                -> new NotFoundException("WorkTime not found!"));
        //date
        if (workTimeDtoRequest.getDate().isBefore(LocalDate.now())) {
            throw new BadRequestException("Invalid date");
        } else {
            oldWorkTime.setDate(workTimeDtoRequest.getDate());
        }
        //start
        oldWorkTime.setStart(workTimeDtoRequest.getStart());
        //finish
        if (Duration.between(
                workTimeDtoRequest.getFinish(),
                workTimeDtoRequest.getStart()).toHours() > MAX_WORK_HOURS) {
            throw new BadRequestException("Too long working day.");
        } else {
            oldWorkTime.setFinish(workTimeDtoRequest.getFinish());
        }
        //workplace
        checkIfWorkplaceBooked(workTimeDtoRequest);
        Workplace workplace = workplaceRepository.findById(workTimeDtoRequest.getWorkplaceId()).orElseThrow(()
                -> new NotFoundException("Workplace not found!"));
        oldWorkTime.setWorkplace(workplace);
        //worker
        checkIfWorkerBusy(workTimeDtoRequest);
        User user = userRepository.findById(workTimeDtoRequest.getUserId()).orElseThrow(()
                -> new NotFoundException("User not found!"));
        oldWorkTime.setWorker(user);

        return mapToWorkTimeDto(workTimeRepository.saveAndFlush(oldWorkTime));
    }

    public void deleteWorkTime(Long id) {
        log.info("deleteWorkTime id: " + id);
        if (id == null) {
            throw new BadRequestException("Invalid ID");
        }
        workTimeRepository.deleteById(id);
    }

    public WorkTimeDtoResponse mapToWorkTimeDto(WorkTime workTime) {
        WorkTimeDtoResponse dto = WorkTimeDtoResponse.builder()
                .id(workTime.getId())
                .date(workTime.getDate())
                .start(workTime.getStart())
                .finish(workTime.getFinish())
                .workplaceId(workTime.getWorkplace().getId())
                .workerId(workTime.getWorker().getId())
                .build();
        return dto;
    }

    public WorkTime mapToWorkTime(WorkTimeDtoRequest workTimeDtoRequest) {
        WorkTime workTime = new WorkTime();
        workTime.setDate(workTimeDtoRequest.getDate());
        workTime.setStart(workTimeDtoRequest.getStart());
        workTime.setFinish(workTimeDtoRequest.getFinish());
        Workplace workplace = workplaceRepository.findById(workTimeDtoRequest.getWorkplaceId()).orElseThrow(()
                -> new NotFoundException("Workplace not found!"));
        workTime.setWorkplace(workplace);
        User user = userRepository.findById(workTimeDtoRequest.getUserId()).orElseThrow(()
                -> new NotFoundException("User not found!"));
        workTime.setWorker(user);
        return workTime;
    }

    public Page<WorkTimeDtoResponse> findAllWorkTimesPageable(Pageable pageable) {
        log.info("findAllWorkTimesPageable pageable:" + pageable);
        Page<WorkTime> page = workTimeRepository.findAll(pageable);
        return new PageImpl<>(page.stream().map(this::mapToWorkTimeDto)
                .collect(Collectors.toList()), page.getPageable(), page.getTotalElements());
    }

    public void checkIfWorkplaceBooked(WorkTimeDtoRequest workTimeDtoRequest) {
        List<WorkTime> workTimes2 =
                workTimeRepository.findAllByWorkplaceAndDate(
                        workTimeDtoRequest.getWorkplaceId(),
                        workTimeDtoRequest.getDate());
        for (WorkTime w : workTimes2) {
            if (!(w.getFinish().isBefore(workTimeDtoRequest.getStart())
                    || w.getStart().isAfter(workTimeDtoRequest.getFinish()))) {
                throw new BadRequestException(
                        "Another worker already booked this Workplace for this time.");
            }
        }
    }

    public void checkIfWorkerBusy(WorkTimeDtoRequest workTimeDtoRequest) {
        List<WorkTime> workTimes =
                workTimeRepository.findAllByWorkerAndDate(
                        workTimeDtoRequest.getUserId(),
                        workTimeDtoRequest.getDate());
        for (WorkTime w : workTimes) {
            if (!(w.getFinish().isBefore(workTimeDtoRequest.getStart())
                    || w.getStart().isAfter(workTimeDtoRequest.getFinish()))) {
                throw new BadRequestException("This worker already booked this time.");
            }
        }
    }
}