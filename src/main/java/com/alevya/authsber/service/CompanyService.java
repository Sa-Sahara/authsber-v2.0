package com.alevya.authsber.service;

import com.alevya.authsber.dto.CompanyDtoRequest;
import com.alevya.authsber.dto.CompanyDtoResponse;
import com.alevya.authsber.exception.BadRequestException;
import com.alevya.authsber.exception.NotFoundException;
import com.alevya.authsber.model.Company;
import com.alevya.authsber.repository.CompanyRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CompanyService {

    private static final int MAX_LENGTH = 255;

    private CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public CompanyDtoResponse createCompany(CompanyDtoRequest dto) {
        //checking fields
        if (dto == null) {
            throw new BadRequestException("Invalid company");
        }
        if (StringUtils.isBlank(dto.getShortName())) {
            throw new BadRequestException("Invalid company short name");
        }
        if (dto.getShortName().length() > MAX_LENGTH) {
            throw new BadRequestException("Too long company name");
        }
        if (companyRepository.existsByShortName(dto.getShortName())) {
            throw new BadRequestException("Exist company with this name");
        }
        if (StringUtils.isBlank(dto.getFullName())) {
            throw new BadRequestException("Invalid company full name");
        }
        if (companyRepository.existsByFullName(dto.getFullName())) {
            throw new BadRequestException("Exist company with this full name");
        }
        if (StringUtils.isBlank(dto.getDescription())) {
            throw new BadRequestException("Invalid company description");
        }
        if (StringUtils.isBlank(dto.getAddress())) {
            throw new BadRequestException("Invalid company Address");
        }
        //save in DB
        return mapToDto(companyRepository.save(mapToCompany(dto)));
    }

    public CompanyDtoResponse getCompanyById(Long id) {
        if (id == null) {
            throw new BadRequestException("Invalid ID");
        }
        return mapToDto(companyRepository.findById(id).orElseThrow(()
                -> new NotFoundException("Company not found!")));
    }

    public CompanyDtoResponse getCompanyByFullName(String fullName) {
        if (StringUtils.isBlank(fullName)) {
            throw new BadRequestException("Invalid fullName");
        }
        Company company = companyRepository.findByFullName(fullName);
        if (company == null) { //todo - is it possible
            throw new NotFoundException("Company not found!");
        }
        return mapToDto(company);
    }

    public CompanyDtoResponse getCompanyByShortName(String shortName) {
        if (StringUtils.isBlank(shortName)) {
            throw new BadRequestException("Invalid name");
        }
        Company company = companyRepository.findByShortName(shortName);
        if (company == null) { //todo - is it possible
            throw new NotFoundException("Company not found!");
        }
        return mapToDto(company);
    }

    public Set<CompanyDtoResponse> getAllCompanies() {
        return companyRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toSet());
    }

    public Page<CompanyDtoResponse> findAllCompaniesPageable(Pageable pageable) {
        Page<Company> page = companyRepository.findAll(pageable);
        return new PageImpl<>(page.stream().map(this::mapToDto)
                .collect(Collectors.toList()), page.getPageable(), page.getTotalElements());
    }

    public CompanyDtoResponse updateCompany(CompanyDtoRequest dto) {
        if (dto == null || !companyRepository.existsByFullName(dto.getFullName())) {
            throw new BadRequestException("Invalid company");
        }
        Company oldCompany = companyRepository.findByFullName(dto.getFullName());

        //short name
        if (StringUtils.isBlank(dto.getShortName())) {
            throw new BadRequestException("Invalid company short name");
        } else if (companyRepository.existsByShortName(dto.getShortName())) {
            throw new BadRequestException("Not unique company short name");
        }
        oldCompany.setShortName(dto.getShortName());

        //description
        oldCompany.setDescription(dto.getDescription());

        //address
        final String address = dto.getAddress();
        if (StringUtils.isBlank(address)) {
            throw new BadRequestException("Invalid company address");
        } else {
            oldCompany.setAddress(dto.getAddress());
        }

        //phone
        oldCompany.setPhone(dto.getPhone());

        return mapToDto(companyRepository.saveAndFlush(oldCompany));
    }

    public void deleteCompany(Long id) {
        if (id == null) {
            throw new BadRequestException("Invalid ID");
        }
        companyRepository.deleteById(id);
    }

    public CompanyDtoResponse mapToDto(Company company) {
        CompanyDtoResponse dto = CompanyDtoResponse.builder()
                .id(company.getId())
                .shortName(company.getShortName())
                .fullName(company.getFullName())
                .description(company.getDescription())
                .address(company.getAddress())
                .phone(company.getPhone())
                .build();
        return dto;
    }

    public Company mapToCompany(CompanyDtoRequest companyDtoRequest) {
        Company company = new Company();
        company.setShortName(companyDtoRequest.getShortName());
        company.setFullName(companyDtoRequest.getFullName());
        company.setDescription(companyDtoRequest.getDescription());
        company.setAddress(companyDtoRequest.getAddress());
        company.setPhone(companyDtoRequest.getPhone());
        return company;
    }
}