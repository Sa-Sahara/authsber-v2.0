package com.alevya.authsber.task;

import com.alevya.authsber.model.User;
import com.alevya.authsber.model.WorkTime;
import com.alevya.authsber.model.Workplace;
import com.alevya.authsber.repository.UserRepository;
import com.alevya.authsber.repository.WorkTimeRepository;
import com.alevya.authsber.repository.WorkplaceRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@Async
@EnableScheduling
public class MakeWorkTimeTask {

    private WorkplaceRepository workplaceRepository;
    private WorkTimeRepository workTimeRepository;
    private UserRepository userRepository;

    public MakeWorkTimeTask(WorkplaceRepository workplaceRepository,
            WorkTimeRepository workTimeRepository,
            UserRepository userRepository) {
        this.workplaceRepository = workplaceRepository;
        this.workTimeRepository = workTimeRepository;
        this.userRepository = userRepository;
    }

    @Scheduled(fixedDelay = 5000)
    public void reportCurrentTime() {
        List<Workplace> workplaceList =  workplaceRepository.findAll();
        for (Workplace wp : workplaceList) {
            List<WorkTime> workTimeList =  workTimeRepository
                    .findAllByDateBetweenAndWorkplaceId(LocalDate.now(),
                            LocalDate.now().plusDays(3), wp.getId());
            if(workTimeList.isEmpty()) {
                User user = userRepository.findById(wp.getId()).orElseThrow();
                for (int i = 0; i < 3; i++) {
                    workTimeRepository.save(new WorkTime(
                            null,
                            LocalDate.now().plusDays(i),
                            LocalTime.of(9, 0),
                            LocalTime.of(20, 0),
                            wp,
                            user
                    ));
                }
            } else if(workTimeList.size() < 3) {
                User user = userRepository.findById(wp.getId()).orElseThrow();
                    workTimeRepository.save(new WorkTime(
                            null,
                            LocalDate.now().plusDays(2),
                            LocalTime.of(9, 0),
                            LocalTime.of(20, 0),
                            wp,
                            user
                    ));
            }
        }
    }
}
