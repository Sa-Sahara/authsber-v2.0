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

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompanyService {

    private static final int MAX_LENGTH = 255;

    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public CompanyDtoResponse createCompany(CompanyDtoRequest companyDtoRequest) {
        //checking fields
        if (companyDtoRequest == null) {
            throw new BadRequestException("Invalid company");
        }
        if (!StringUtils.isNotBlank(companyDtoRequest.getShortName())) {
            throw new BadRequestException("Invalid company's name");
        }
        if (companyDtoRequest.getShortName().length() > MAX_LENGTH) {
            throw new BadRequestException("Too long company's name");
        }
        if (companyRepository.existsByShortName(companyDtoRequest.getShortName())) {
            throw new BadRequestException("Exist company with this name");
        }
        if (!StringUtils.isNotBlank(companyDtoRequest.getFullName())) {
            throw new BadRequestException("Invalid company's full name");
        }
        if (companyRepository.existsByFullName(companyDtoRequest.getFullName())) {
            throw new BadRequestException("Exist company with this full name");
        }
        if (!StringUtils.isNotBlank(companyDtoRequest.getDescription())) {
            throw new BadRequestException("Invalid company's description");
        }
        //save in DB
        return mapToCompanyDto(companyRepository.save(mapToCompany(companyDtoRequest)));
    }

    public CompanyDtoResponse getCompanyById(Long id) {
        if (id == null) {
            throw new BadRequestException("Invalid ID");
        }
        return mapToCompanyDto(companyRepository.findById(id).orElseThrow(()
                -> new NotFoundException("Company not found!")));
    }

    public CompanyDtoResponse getCompanyByName(String name) {
        if (name == null) {
            throw new BadRequestException("Invalid name");
        }
        Company company = companyRepository.findByShortName(name);
        if (company == null) {
            throw new NotFoundException("Company not found!");
        }
        return mapToCompanyDto(company);
    }

    public CompanyDtoResponse getCompanyByFullName(String fullName) {
        if (fullName == null) {
            throw new BadRequestException("Invalid fullName");
        }
        Company company = companyRepository.findByFullName(fullName);
        if (company == null) {
            throw new NotFoundException("Company not found!");
        }
        return mapToCompanyDto(company);
    }

    public List<CompanyDtoResponse> getAllCompanies() {
        return companyRepository.findAll().stream()
                .map(this::mapToCompanyDto)
                .collect(Collectors.toList());
    }

    public Page<CompanyDtoResponse> findAllCompaniesPageable(Pageable pageable) {
        Page<Company> page = companyRepository.findAll(pageable);
        return new PageImpl<>(page.stream().map(this::mapToCompanyDto)
                .collect(Collectors.toList()), page.getPageable(), page.getTotalElements());
    }

    public CompanyDtoResponse updateCompany(Long id, CompanyDtoRequest companyDtoRequest) {
        if (companyDtoRequest == null) {
            throw new BadRequestException("Invalid company");
        }
        Company oldCompany = companyRepository.findById(id).orElseThrow(()
                -> new NotFoundException("Company not found!"));
        //full name
        if (companyDtoRequest.getFullName() != null &&
                companyRepository.existsByFullName(companyDtoRequest.getFullName())) {
            throw new BadRequestException("Exist company with this name");
        } else if(companyDtoRequest.getFullName() != null) {
            final String fullName = companyDtoRequest.getFullName();
            if (!StringUtils.isNotBlank(fullName)) {
                throw new BadRequestException("Invalid company's full name");
            } else {
                oldCompany.setFullName(fullName);
            }
        }

        //name
        if (companyDtoRequest.getShortName() != null &&
                companyRepository.existsByShortName(companyDtoRequest.getShortName())) {
            throw new BadRequestException("Exist company with this name");
        } else if(companyDtoRequest.getShortName() != null) {
            final String name = companyDtoRequest.getShortName();
            if (!StringUtils.isNotBlank(name)) {
                throw new BadRequestException("Invalid company's name");
            } else if (name.length() > MAX_LENGTH) {
                throw new BadRequestException("Too long company's name");
            } else {
                oldCompany.setShortName(name);
            }
        }

        //description
        final String description = companyDtoRequest.getDescription();
        if (companyDtoRequest.getDescription() != null && !StringUtils.isNotBlank(description)) {
            throw new BadRequestException("Invalid company's description");
        } else if(companyDtoRequest.getDescription() != null) {
            oldCompany.setDescription(description);
        }
        //address
        final String address = companyDtoRequest.getAddress();
        if (companyDtoRequest.getAddress() != null && !StringUtils.isNotBlank(address)) {
            throw new BadRequestException("Invalid company's address");
        } else if(companyDtoRequest.getAddress() != null){
            oldCompany.setAddress(address);
        }
        //phone
        final String phone = companyDtoRequest.getPhone();
        if (phone != null) {
            oldCompany.setPhone(phone);
        }
        return mapToCompanyDto(companyRepository.saveAndFlush(oldCompany));
    }

    public void deleteCompany(Long id) {
        if (id == null) {
            throw new BadRequestException("Invalid ID");
        }
        companyRepository.deleteById(id);
    }

    public CompanyDtoResponse mapToCompanyDto(Company company) {
        CompanyDtoResponse companyDto = CompanyDtoResponse.builder()
                .id(company.getId())
                .shortName(company.getShortName())
                .fullName(company.getFullName())
                .description(company.getDescription())
                .address(company.getAddress())
                .phone(company.getPhone())
                .build();
        return companyDto;
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
