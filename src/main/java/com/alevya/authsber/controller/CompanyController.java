package com.alevya.authsber.controller;

import com.alevya.authsber.dto.CompanyDtoRequest;
import com.alevya.authsber.dto.CompanyDtoResponse;
import com.alevya.authsber.service.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@CrossOrigin(value = "http://localhost:3000")
@Tag(name = "Company controller",
        description = "Give CRUD functions for company: " +
        "/api/v1/company/**")
//@SecurityRequirement(name = "JWT Authentication")
@RestController
@RequestMapping("/api/v1/company")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

        @Secured("CREATE_COMPANY")
    @Operation(summary = "Create company")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CompanyDtoResponse> createCompany(@RequestBody CompanyDtoRequest dto) {
        return ResponseEntity.ok(companyService.createCompany(dto));
    }

    @Secured("GET_COMPANY")
    @Operation(summary = "Get company by id")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CompanyDtoResponse> getCompanyById(@PathVariable Long id) {
        return ResponseEntity.ok(companyService.getCompanyById(id));
    }

    @Secured("GET_COMPANY")
    @Operation(summary = "Get company by full name")
    @GetMapping(value = "/fullName/{fullName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CompanyDtoResponse> getCompanyByFullName(@PathVariable String fullName) {
        return ResponseEntity.ok(companyService.getCompanyByFullName(fullName));
    }

    @Secured("GET_COMPANY")
    @Operation(summary = "Get company by short name")
    @GetMapping(value = "/shortName/{shortName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CompanyDtoResponse> getCompanyByShortName(@PathVariable String shortName) {
        return ResponseEntity.ok(companyService.getCompanyByShortName(shortName));
    }

    @Secured("GET_COMPANIES")
    @Operation(summary = "Get all companies")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Set<CompanyDtoResponse> getAllCompanies() {
        return companyService.getAllCompanies();
    }

    @Operation(summary = "Get companies addresses")
    @GetMapping(value = "/addresses", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Set<String> getAllCompaniesAddresses() {
        return companyService.getAllCompaniesAddresses();
    }

    @Secured("GET_COMPANIES")
    @Operation(summary = "Get All Companies Page")
    @GetMapping(value = "/pages", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Page<CompanyDtoResponse> getAllCompaniesPage(@RequestParam(defaultValue = "1") int page,
                                                        @RequestParam(defaultValue = "10") int size,
                                                        @RequestParam(defaultValue = "DESC") Sort.Direction sortDirection,
                                                        @RequestParam(defaultValue = "id") String sort
    ) {
        return companyService.findAllCompaniesPageable(PageRequest.of(page, size, sortDirection, sort));
    }

    @Secured("UPDATE_COMPANY")
    @Operation(summary = "Update company")
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CompanyDtoResponse> updateCompany(@PathVariable Long id,
            @RequestBody @Validated CompanyDtoRequest dto) {
        return ResponseEntity.ok(companyService.updateCompany(id, dto));
    }

    @Secured("DELETE_COMPANY")
    @Operation(summary = "Delete company")
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteCompany(@PathVariable Long id) {
        companyService.deleteCompany(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
