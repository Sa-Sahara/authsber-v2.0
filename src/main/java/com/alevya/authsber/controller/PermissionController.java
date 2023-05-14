package com.alevya.authsber.controller;

import com.alevya.authsber.dto.PermissionDtoRequest;
import com.alevya.authsber.dto.PermissionDtoResponse;
import com.alevya.authsber.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Permission's controller"
        , description = "Give CRUD functional for permission:" +
        "/api/v1/permission/**")
//@SecurityRequirement(name = "JWT Authentication")
@RestController
@RequestMapping("/api/v1/permission")
public class PermissionController {

    private static final Log log = LogFactory.getLog(PermissionController.class);
    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    //    @Secured("CREATE_PERMISSION")
    @Operation(summary = "Create permission")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PermissionDtoResponse> createPermission(@RequestBody PermissionDtoRequest permissionDtoRequest) {
        log.info("createPermission permissionDtoRequest: " + permissionDtoRequest);
        return ResponseEntity.ok(permissionService.createPermission(permissionDtoRequest));
    }

    //    @Secured("GET_PERMISSION")
    @Operation(summary = "Get permission by id")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PermissionDtoResponse> getPermissionById(@PathVariable Long id) {
        log.info("getPermissionById id: " + id);
        return ResponseEntity.ok(permissionService.getPermissionById(id));
    }

    //    @Secured("GET_PERMISSIONS")
    @Operation(summary = "Get all permissions")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<PermissionDtoResponse> getAllPermissions() {
        log.info("getAllPermissions");
        return permissionService.getAllPermissions();
    }

    //    @Secured("GET_PERMISSIONS")
    @Operation(summary = "Get All Permissions Page")
    @GetMapping(value = "/pages", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Page<PermissionDtoResponse> getAllPermissionsPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "DESC") Sort.Direction sortDirection,
            @RequestParam(defaultValue = "id") String sort
    ) {
        log.info("getAllPermissionsPage page: " + page + " size: " + size + " sort: " + sort);
        return permissionService.findAllPermissionsPageable(PageRequest.of(page, size, sortDirection, sort));
    }

    //    @Secured("UPDATE_PERMISSION")
    @Operation(summary = "Update permission")
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PermissionDtoResponse> updatePermission(@PathVariable Long id,
             @RequestBody @Validated PermissionDtoRequest permissionDtoRequest) {
        log.info("updatePermission id: " + id + " permissionDtoRequest: " + permissionDtoRequest);
        return ResponseEntity.ok(permissionService.updatePermission(id, permissionDtoRequest));
    }

    //    @Secured("DELETE_PERMISSION")
    @Operation(summary = "Delete permission")
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deletePermission(@PathVariable Long id) {
        log.info("updatePermission id: " + id);
        permissionService.deletePermission(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
