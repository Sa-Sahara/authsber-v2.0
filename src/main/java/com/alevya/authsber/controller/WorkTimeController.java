package com.alevya.authsber.controller;

import com.alevya.authsber.dto.WorkTimeDtoRequest;
import com.alevya.authsber.dto.WorkTimeDtoResponse;
import com.alevya.authsber.service.WorkTimeService;
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

@Tag(name = "WorkTime controller"
        , description = "Give CRUD functional for s WorkTime:" +
        "/api/v1/workTime/**")
//@SecurityRequirement(name = "JWT Authentication")
@RestController
@RequestMapping("/api/v1/worktime")
public class WorkTimeController {
    private final WorkTimeService workTimeService;

    public WorkTimeController(WorkTimeService workTimeService) {
        this.workTimeService = workTimeService;
    }

    //    @Secured("CREATE_WORKTIME")
    @Operation(summary = "Create workTime")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<WorkTimeDtoResponse> createWorkTime(
            @RequestBody WorkTimeDtoRequest workTimeDtoRequest) {
        return ResponseEntity.ok(workTimeService.createWorkTime(workTimeDtoRequest));
    }

    //    @Secured("GET_WORKTIME")
    @Operation(summary = "Get workTime by id")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<WorkTimeDtoResponse> getWorkTimeById(@PathVariable Long id) {
        return ResponseEntity.ok(workTimeService.getWorkTimeById(id));
    }

    //    @Secured("GET_WORKTIMES")
    @Operation(summary = "Get all WorkTimes")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<WorkTimeDtoResponse> getAllWorkTimes() {
        return workTimeService.getAllWorkTimes();
    }

    //    @Secured("UPDATE_WORKTIME")
    @Operation(summary = "Update WorkTime")
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<WorkTimeDtoResponse> updateWorkTime(
            @PathVariable Long id,
            @RequestBody @Validated WorkTimeDtoRequest workTimeDtoRequest) {
        return ResponseEntity.ok(workTimeService.updateWorkTime(id, workTimeDtoRequest));
    }

    //    @Secured("DELETE_WORKTIME")
    @Operation(summary = "Delete WorkTime")
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteWorkTime(@PathVariable Long id) {
        workTimeService.deleteWorkTime(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    //    @Secured("GET_WORKTIMES")
    @Operation(summary = "Get All WorkTimes Page")
    @GetMapping(value = "/pages", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Page<WorkTimeDtoResponse> getAllWorkTimesPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "DESC") Sort.Direction sortDirection,
            @RequestParam(defaultValue = "id") String sort
    ) {
        return workTimeService.findAllWorkTimesPageable(PageRequest.of(page, size, sortDirection, sort));
    }
}
