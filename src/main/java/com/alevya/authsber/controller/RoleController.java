package com.alevya.authsber.controller;

import com.alevya.authsber.dto.RoleDtoRequest;
import com.alevya.authsber.dto.RoleDtoResponse;
import com.alevya.authsber.service.RoleService;
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
@Tag(name = "Role controller"
        , description = "Give CRUD functional for role:" +
        "/api/v1/role/**")
//@SecurityRequirement(name = "JWT Authentication")
@RestController
@RequestMapping("/api/v1/role")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    //    @Secured("CREATE_ROLE")
    @Operation(summary = "Create role")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RoleDtoResponse> createRole(@RequestBody RoleDtoRequest roleDtoRequest) {
        return ResponseEntity.ok(roleService.createRole(roleDtoRequest));
    }

    //    @Secured("GET_ROLE")
    @Operation(summary = "Get role by id")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RoleDtoResponse> getRoleById(@PathVariable Long id) {
        return ResponseEntity.ok(roleService.getRoleById(id));
    }

    //    @Secured("GET_ROLE")
    @Operation(summary = "Get role by name")
    @GetMapping(value = "/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RoleDtoResponse> getRoleByName(@PathVariable String name) {
        return ResponseEntity.ok(roleService.getRoleByName(name));
    }

//    @Secured("GET_ROLE")
//    @Operation(summary = "Get role for not verified user")
//    @GetMapping(value = "/guest", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<RoleDtoResponse> getRoleGuest() {
//        return getRoleByName("GUEST");
//    }

    //    @Secured("GET_ROLES")
    @Operation(summary = "Get all roles")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<RoleDtoResponse> getAllRoles() {
        return roleService.getAllRoles();
    }

    //    @Secured("GET_ROLES")
    @Operation(summary = "Get All Roles Page")
    @GetMapping(value = "/pages", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Page<RoleDtoResponse> getAllRolesPage(@RequestParam(defaultValue = "1") int page,
                                                 @RequestParam(defaultValue = "10") int size,
                                                 @RequestParam(defaultValue = "DESC") Sort.Direction sortDirection,
                                                 @RequestParam(defaultValue = "id") String sort
    ) {
        return roleService.findAllRolesPageable(PageRequest.of(page, size, sortDirection, sort));
    }

    //    @Secured("UPDATE_ROLE")
    @Operation(summary = "Update role")
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RoleDtoResponse> updateRole(
            @PathVariable Long id,
            @RequestBody @Validated RoleDtoRequest roleDtoRequest) {
        return ResponseEntity.ok(roleService.updateRole(id, roleDtoRequest));
    }

    //    @Secured("DELETE_ROLE")
    @Operation(summary = "Delete role")
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
