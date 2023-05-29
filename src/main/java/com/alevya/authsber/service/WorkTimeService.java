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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorkTimeService {
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

    public WorkTimeDtoResponse createWorkTime(WorkTimeDtoRequest dto) {
        //fields check
        checkDto(dto);
        // check that this worker has not booked this time
        checkIfWorkerBusy(dto);
        // check that this workplace is not booked
        checkIfWorkplaceBooked(dto);

        return mapToWorkTimeDto(workTimeRepository.save(
                mapToWorkTime(dto)));
    }

    public WorkTimeDtoResponse getWorkTimeById(Long id) {
        if (id == null) {
            throw new BadRequestException("Invalid ID");
        }
        return mapToWorkTimeDto(workTimeRepository.findById(id).orElseThrow(()
                -> new NotFoundException("WorkTime not found!")));
    }

    public List<WorkTimeDtoResponse> getAllWorkTimes() {
        return workTimeRepository.findAll().stream()
                .map(this::mapToWorkTimeDto)
                .collect(Collectors.toList());
    }

    public List<WorkTimeDtoResponse> getBookedWorkTimesByUserAndDate(
            User user, LocalDate localDate) {
        return workTimeRepository.findAllByWorkerAndDate(user.getId(), localDate)
                .stream()
                .map(this::mapToWorkTimeDto)
                .collect(Collectors.toList());
    }

    public List<WorkTimeDtoResponse> getBookedWorkTimesByWorkplaceAndDate(
            Workplace workplace, LocalDate localDate) {
        return workTimeRepository.findAllByWorkplaceAndDate(workplace.getId(), localDate)
                .stream()
                .map(this::mapToWorkTimeDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public WorkTimeDtoResponse updateWorkTime(
            Long id,
            WorkTimeDtoRequest dto) {

        checkDto(dto);
        WorkTime oldWorkTime = workTimeRepository.findById(id).orElseThrow(()
                -> new NotFoundException("WorkTime not found!"));
        //date
            oldWorkTime.setDate(dto.getDate());
        //start
        oldWorkTime.setStart(dto.getStart());
        //finish
            oldWorkTime.setFinish(dto.getFinish());
        //workplace
        checkIfWorkplaceBooked(dto);
        Workplace workplace = workplaceRepository.findById(dto.getWorkplaceId()).orElseThrow(()
                -> new NotFoundException("Workplace not found!"));
        oldWorkTime.setWorkplace(workplace);
        //worker
        checkIfWorkerBusy(dto);
        User user = userRepository.findById(dto.getUserId()).orElseThrow(()
                -> new NotFoundException("User not found!"));
        oldWorkTime.setWorker(user);

        return mapToWorkTimeDto(workTimeRepository.saveAndFlush(oldWorkTime));
    }

    public void deleteWorkTime(Long id) {
        if (id == null) {
            throw new BadRequestException("Invalid ID");
        }
        workTimeRepository.deleteById(id);
    }

    private void checkDto(WorkTimeDtoRequest dto) {
        if (dto == null) {
            throw new BadRequestException("Invalid workTime");
        }
        if (dto.getDate() == null
                || dto.getDate().isBefore(LocalDate.now())) {
            throw new BadRequestException("Invalid Date");
        }
        if (dto.getStart() == null) {
            throw new BadRequestException("Invalid start of working period");
        }
        if (dto.getFinish() == null) {
            throw new BadRequestException("Invalid end of working period");
        }
        if (Duration.between(
                dto.getFinish(),
                dto.getStart()).toHours() > MAX_WORK_HOURS) {
            throw new BadRequestException("Too long working day.");
        }
        if (dto.getWorkplaceId() == null) {
            throw new BadRequestException("Invalid Workplace");
        }
        if (dto.getUserId() == null) {
            throw new BadRequestException("Invalid Worker");
        }
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
        Page<WorkTime> page = workTimeRepository.findAll(pageable);
        return new PageImpl<>(page.stream().map(this::mapToWorkTimeDto)
                .collect(Collectors.toList()), page.getPageable(), page.getTotalElements());
    }

    public void checkIfWorkplaceBooked(WorkTimeDtoRequest dto) {
        List<WorkTime> workTimes2 =
                workTimeRepository.findAllByWorkplaceAndDate(
                        dto.getWorkplaceId(),
                        dto.getDate());
        for (WorkTime w : workTimes2) {
            if (!(w.getFinish().isBefore(dto.getStart())
                    || w.getStart().isAfter(dto.getFinish()))) {
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
