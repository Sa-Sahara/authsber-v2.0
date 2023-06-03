package com.alevya.authsber.controller;

import com.alevya.authsber.dto.PermissionDtoRequest;
import com.alevya.authsber.dto.PermissionDtoResponse;
import com.alevya.authsber.service.PermissionService;
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
@Tag(name = "Permission's controller"
        , description = "Give CRUD functional for permission:" +
        "/api/v1/permission/**")
//@SecurityRequirement(name = "JWT Authentication")
@RestController
@RequestMapping("/api/v1/permission")
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    //    @Secured("CREATE_PERMISSION")
    @Operation(summary = "Create permission")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PermissionDtoResponse> createPermission(@RequestBody PermissionDtoRequest permissionDtoRequest) {
        return ResponseEntity.ok(permissionService.createPermission(permissionDtoRequest));
    }

    //    @Secured("GET_PERMISSION")
    @Operation(summary = "Get permission by id")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PermissionDtoResponse> getPermissionById(@PathVariable Long id) {
        return ResponseEntity.ok(permissionService.getPermissionById(id));
    }

    //    @Secured("GET_PERMISSIONS")
    @Operation(summary = "Get all permissions")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<PermissionDtoResponse> getAllPermissions() {
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
        return permissionService.findAllPermissionsPageable(PageRequest.of(page, size, sortDirection, sort));
    }

    //    @Secured("UPDATE_PERMISSION")
    @Operation(summary = "Update permission")
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PermissionDtoResponse> updatePermission(@PathVariable Long id,
             @RequestBody @Validated PermissionDtoRequest permissionDtoRequest) {
        return ResponseEntity.ok(permissionService.updatePermission(id, permissionDtoRequest));
    }

    //    @Secured("DELETE_PERMISSION")
    @Operation(summary = "Delete permission")
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deletePermission(@PathVariable Long id) {
        permissionService.deletePermission(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
