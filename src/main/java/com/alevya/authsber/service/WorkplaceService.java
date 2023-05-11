package com.alevya.authsber.service;

import com.alevya.authsber.dto.WorkplaceDtoRequest;
import com.alevya.authsber.dto.WorkplaceDtoResponse;
import com.alevya.authsber.exception.BadRequestException;
import com.alevya.authsber.exception.NotFoundException;
import com.alevya.authsber.model.Workplace;
import com.alevya.authsber.repository.WorkplaceRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorkplaceService {

    private static final Log log = LogFactory.getLog(WorkplaceService.class);

    private final WorkplaceRepository workplaceRepository;

    public WorkplaceService(WorkplaceRepository workplaceRepository) {
        this.workplaceRepository = workplaceRepository;
    }

    public WorkplaceDtoResponse createWorkplace(
            WorkplaceDtoRequest workplaceDtoRequest) {
        if (workplaceDtoRequest == null) {
            throw new BadRequestException("Invalid workplace");
        }
        log.info("createWorkplace: " + workplaceDtoRequest);
        return mapToWorkplaceDto(workplaceRepository.save(
                mapToWorkplace(workplaceDtoRequest)));
    }

    public WorkplaceDtoResponse getWorkplaceById(Long id) {
        if (id == null) {
            throw new BadRequestException("Invalid ID");
        }
        log.info("getWorkplaceById: " + id);
        return mapToWorkplaceDto(workplaceRepository.findById(id).orElseThrow(()
                -> new NotFoundException("Workplace not found!")));
    }

    public List<WorkplaceDtoResponse> getAllWorkplaces() {
        log.info("getAllWorkplaces");
        return workplaceRepository.findAll().stream()
                .map(this::mapToWorkplaceDto)
                .collect(Collectors.toList());
    }

    public WorkplaceDtoResponse updateWorkplace(Long id, WorkplaceDtoRequest workplaceDtoRequest) {
        log.info("updateWorkplace id: " + id + " workplaceDtoRequest: " + workplaceDtoRequest);
        if (workplaceDtoRequest == null) {
            throw new BadRequestException("Invalid workplace");
        }
        Workplace oldWorkplace = workplaceRepository.findById(id).orElseThrow(()
                -> new NotFoundException("Workplace not found!"));

        if (workplaceDtoRequest.getName() != null) {
            oldWorkplace.setName(workplaceDtoRequest.getName());
        }
        if (workplaceDtoRequest.getDescription() != null) {
            oldWorkplace.setDescription(workplaceDtoRequest.getDescription());
        }
        return mapToWorkplaceDto(workplaceRepository.saveAndFlush(oldWorkplace));
    }

    public void deleteWorkplace(Long id) {
        log.info("deleteWorkplace id: " + id);
        if (id == null) {
            throw new BadRequestException("Invalid ID");
        }
        workplaceRepository.deleteById(id);
    }

    public WorkplaceDtoResponse mapToWorkplaceDto(Workplace workplace) {
        WorkplaceDtoResponse dto = WorkplaceDtoResponse.builder()
                .id(workplace.getId())
                .name(workplace.getName())
                .description(workplace.getDescription())
                .build();
        return dto;
    }

    public Workplace mapToWorkplace(WorkplaceDtoRequest workplaceDtoRequest) {
        Workplace workplace = new Workplace();
        workplace.setName(workplaceDtoRequest.getName());
        workplace.setDescription(workplaceDtoRequest.getDescription());
        return workplace;
    }

    public Page<WorkplaceDtoResponse> findAllWorkplacesPageable(Pageable pageable) {
        Page<Workplace> page = workplaceRepository.findAll(pageable);
        return new PageImpl<>(page.stream().map(this::mapToWorkplaceDto)
                .collect(Collectors.toList()), page.getPageable(), page.getTotalElements());
    }
}
