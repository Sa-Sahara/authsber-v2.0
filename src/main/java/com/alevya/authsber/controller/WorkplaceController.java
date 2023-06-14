package com.alevya.authsber.controller;

import com.alevya.authsber.dto.WorkplaceDtoRequest;
import com.alevya.authsber.dto.WorkplaceDtoResponse;
import com.alevya.authsber.service.WorkplaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(value = "http://localhost:3000")
@Tag(name = "Workplace controller"
        , description = "Give CRUD functional for Workplace:" +
        "/api/v1/workplace/**")
//@SecurityRequirement(name = "JWT Authentication")
@RestController
@RequestMapping("/api/v1/workplace")
public class WorkplaceController {

    private final WorkplaceService workplaceService;

    public WorkplaceController(WorkplaceService workplaceService) {
        this.workplaceService = workplaceService;
    }


    //    @Secured("CREATE_WORKPLACE")
    @Operation(summary = "Create workplace")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<WorkplaceDtoResponse> createWorkplace(
            @RequestBody WorkplaceDtoRequest dto) {
        return ResponseEntity.ok(workplaceService.createWorkplace(dto));
    }

    //    @Secured("GET_WORKPLACE")
    @Operation(summary = "Get workplace by id")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<WorkplaceDtoResponse> getWorkplaceById(@PathVariable Long id) {
        return ResponseEntity.ok(workplaceService.getWorkplaceById(id));
    }

    //    @Secured("GET_WORKPLACES")
    @Operation(summary = "Get all Workplaces")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<WorkplaceDtoResponse> getAllWorkplaces() {
        return workplaceService.getAllWorkplaces();
    }

    //    @Secured("UPDATE_WORKPLACE")
    @Operation(summary = "Update Workplace")
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<WorkplaceDtoResponse> updateWorkplace(
            @PathVariable Long id,
            @RequestBody @Validated WorkplaceDtoRequest dto) {
        return ResponseEntity.ok(workplaceService.updateWorkplace(id, dto));
    }

    //    @Secured("DELETE_WORKPLACE")
    @Operation(summary = "Delete Workplace")
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteWorkplace(@PathVariable Long id) {
        workplaceService.deleteWorkplace(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    //    @Secured("GET_WORKPLACES")
    @Operation(summary = "Get All workplaces Page")
    @GetMapping(value = "/pages", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Page<WorkplaceDtoResponse> getAllWorkplacesPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "DESC") Sort.Direction sortDirection,
            @RequestParam(defaultValue = "id") String sort
    ) {
        return workplaceService.findAllWorkplacesPageable(PageRequest.of(page, size, sortDirection, sort));
    }
}
