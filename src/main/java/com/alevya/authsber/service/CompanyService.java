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

    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public CompanyDtoResponse createCompany(CompanyDtoRequest dto) {
        //checking fields
        checkFields(dto);

        if (companyRepository.existsByFullName(dto.getFullName())) {
            throw new BadRequestException("Exist company with this full name");
        }
        if (companyRepository.existsByShortName(dto.getShortName())) {
            throw new BadRequestException("Exist company with this short name");
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

    public CompanyDtoResponse getCompanyByAddress(String address) {
        if (StringUtils.isBlank(address)) {
            throw new BadRequestException("Invalid name");
        }
        Company company = companyRepository.findByAddress(address);
        if (company == null) {
            throw new NotFoundException("Company not found!");
        }
        return mapToDto(company);
    }

    public CompanyDtoResponse getCompanyByShortName(String shortName) {
        if (StringUtils.isBlank(shortName)) {
            throw new BadRequestException("Invalid name");
        }
        Company company = companyRepository.findByShortName(shortName);
        if (company == null) {
            throw new NotFoundException("Company not found!");
        }
        return mapToDto(company);
    }

    public Set<CompanyDtoResponse> getAllCompanies() {
        return companyRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toSet());
    }

    public Set<String> getAllCompaniesAddresses() {
        return companyRepository.findAll().stream()
                .map(Company::getAddress)
                .collect(Collectors.toSet());
    }

    public Set<CompanyDtoResponse> getAllParentCompanies(Long id) {
        return companyRepository.getThisAndAllParentCompanies(id).stream()
                .map(this::mapToDto)
                .collect(Collectors.toSet());
    }

    public Set<CompanyDtoResponse> getAllChildCompanies(Long id) {
        return companyRepository.getThisAndAllChildCompanies(id).stream()
                .map(this::mapToDto)
                .collect(Collectors.toSet());
    }

    public Page<CompanyDtoResponse> findAllCompaniesPageable(Pageable pageable) {
        Page<Company> page = companyRepository.findAll(pageable);
        return new PageImpl<>(page.stream().map(this::mapToDto)
                .collect(Collectors.toList()), page.getPageable(), page.getTotalElements());
    }

    public CompanyDtoResponse updateCompany(Long id, CompanyDtoRequest dto) {
        checkFields(dto);

        if (!companyRepository.existsByFullName(dto.getFullName())) {
            throw new BadRequestException("Invalid company");
        }

        Company oldCompany = companyRepository.findById(id)
                .orElseThrow(()
                        -> new NotFoundException("Company not found!"));

        //full name should not be changed

        //short name
        if (!dto.getShortName().equals(oldCompany.getShortName()) &&
                companyRepository.existsByShortName(dto.getShortName())) {
            throw new BadRequestException("Not unique short name");
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

        //parentCompany
        Company oldParent = oldCompany.getParentCompany();
        Company newParent = new Company();
        if (dto.getParentCompanyId() != null) {
            newParent = companyRepository.findById(dto.getParentCompanyId()).orElseThrow(()
                    -> new NotFoundException("Company not found!"));
        }

        if (oldCompany.getChildCompanies().size() != 1
                && getAllChildCompanies(oldCompany.getId()).contains(mapToDto(newParent))) {
            throw new BadRequestException("Company inheritance cycle, invalid parent");
        }

        if (newParent.getFullName() == null && oldParent != null) {
            throw new BadRequestException("Child company`s parent could not be null");
        } else {
            oldCompany.setParentCompany(newParent);
        }

        return mapToDto(companyRepository.saveAndFlush(oldCompany));
    }

    public void deleteCompany(Long id) {
        if (id == null) {
            throw new BadRequestException("Invalid ID");
        }
        companyRepository.deleteById(id);
    }

    private void checkFields(CompanyDtoRequest dto) {
        if (dto == null) {
            throw new BadRequestException("Invalid company");
        }
        if (StringUtils.isBlank(dto.getShortName())) {
            throw new BadRequestException("Invalid company short name");
        }
        if (dto.getShortName().length() > MAX_LENGTH) {
            throw new BadRequestException("Too long company short name");
        }
        if (StringUtils.isBlank(dto.getFullName())) {
            throw new BadRequestException("Invalid company full name");
        }
        if (dto.getFullName().length() > MAX_LENGTH) {
            throw new BadRequestException("Too long company full name");
        }
        if (StringUtils.isBlank(dto.getAddress())) {
            throw new BadRequestException("Invalid company Address");
        }
    }

    public CompanyDtoResponse mapToDto(Company company) {
        CompanyDtoResponse dto = CompanyDtoResponse.builder()
                .id(company.getId())
                .shortName(company.getShortName())
                .fullName(company.getFullName())
                .description(company.getDescription())
                .address(company.getAddress())
                .phone(company.getPhone())
                .parentCompanyId(company.getParentCompany() == null ? null : company.getParentCompany().getId())
                .build();
        return dto;
    }

    public Company mapToCompany(CompanyDtoRequest dto) {
        Company company = new Company();
        company.setShortName(dto.getShortName());
        company.setFullName(dto.getFullName());
        company.setDescription(dto.getDescription());
        company.setAddress(dto.getAddress());
        company.setPhone(dto.getPhone());
        company.setParentCompany(dto.getParentCompanyId() == null
                ? null
                : companyRepository.getReferenceById(dto.getParentCompanyId()));
        return company;
    }
}