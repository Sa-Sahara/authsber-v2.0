package com.alevya.authsber.service;

import com.alevya.authsber.dto.WorkplaceDtoRequest;
import com.alevya.authsber.dto.WorkplaceDtoResponse;
import com.alevya.authsber.exception.BadRequestException;
import com.alevya.authsber.exception.NotFoundException;
import com.alevya.authsber.model.Workplace;
import com.alevya.authsber.repository.CompanyRepository;
import com.alevya.authsber.repository.WorkplaceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorkplaceService {

    private final WorkplaceRepository workplaceRepository;
    private final CompanyRepository companyRepository;

    public WorkplaceService(WorkplaceRepository workplaceRepository,
                            CompanyRepository companyRepository) {
        this.workplaceRepository = workplaceRepository;
        this.companyRepository = companyRepository;
    }

    public WorkplaceDtoResponse createWorkplace(WorkplaceDtoRequest dto) {
        if (dto == null) {
            throw new BadRequestException("Invalid workplace");
        }
        return mapToWorkplaceDto(workplaceRepository.save(
                mapToWorkplace(dto)));
    }

    public WorkplaceDtoResponse getWorkplaceById(Long id) {
        if (id == null) {
            throw new BadRequestException("Invalid ID");
        }
        return mapToWorkplaceDto(workplaceRepository.findById(id).orElseThrow(()
                -> new NotFoundException("Workplace not found!")));
    }

    public List<WorkplaceDtoResponse> getAllWorkplaces() {
        return workplaceRepository.findAll().stream()
                .map(this::mapToWorkplaceDto)
                .collect(Collectors.toList());
    }

    public WorkplaceDtoResponse updateWorkplace(Long id, WorkplaceDtoRequest dto) {
        if (dto == null) {
            throw new BadRequestException("Invalid workplace");
        }
        Workplace oldWorkplace = workplaceRepository.findById(id).orElseThrow(()
                -> new NotFoundException("Workplace not found!"));

        if (dto.getName() != null) {
            oldWorkplace.setName(dto.getName());
        }
        oldWorkplace.setDescription(dto.getDescription());
        oldWorkplace.setCompany(companyRepository.findById(dto.getCompanyId()).orElseThrow(()
                -> new NotFoundException("Company not found!")));
        return mapToWorkplaceDto(workplaceRepository.saveAndFlush(oldWorkplace));
    }

    public void deleteWorkplace(Long id) {
        if (id == null) {
            throw new BadRequestException("Invalid ID");
        }
        workplaceRepository.deleteById(id);
    }

    public Page<WorkplaceDtoResponse> findAllWorkplacesPageable(Pageable pageable) {
        Page<Workplace> page = workplaceRepository.findAll(pageable);
        return new PageImpl<>(page.stream().map(this::mapToWorkplaceDto)
                .collect(Collectors.toList()), page.getPageable(), page.getTotalElements());
    }

    public WorkplaceDtoResponse mapToWorkplaceDto(Workplace workplace) {
        WorkplaceDtoResponse dto = WorkplaceDtoResponse.builder()
                .id(workplace.getId())
                .name(workplace.getName())
                .description(workplace.getDescription())
                .companyId(workplace.getId())
                .build();
        return dto;
    }

    public Workplace mapToWorkplace(WorkplaceDtoRequest dto) {
        Workplace workplace = new Workplace();
        workplace.setName(dto.getName());
        workplace.setDescription(dto.getDescription());
        workplace.setCompany(companyRepository.findById(dto.getCompanyId()).orElseThrow(()
                -> new NotFoundException("Company not found!")));
        return workplace;
    }
}
