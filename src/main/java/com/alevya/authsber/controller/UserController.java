package com.alevya.authsber.controller;

import com.alevya.authsber.dto.UserDtoRequest;
import com.alevya.authsber.dto.UserDtoResponse;
import com.alevya.authsber.dto.UserRegistrationDto;
import com.alevya.authsber.security.JwtTokenProvider;
import com.alevya.authsber.security.UserDetailsServiceImpl;
import com.alevya.authsber.security.UserPrincipal;
import com.alevya.authsber.service.UserService;
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


@Tag(name="User controller"
        , description="Give CRUD functional for user:" +
        "/api/v1/user/**")
//@SecurityRequirement(name = "JWT Authentication")
@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtTokenProvider jwtTokenProvider;
    private static final Log log = LogFactory.getLog(UserController.class);

    public UserController(UserService userService
            , UserDetailsServiceImpl userDetailsService
            , JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.userDetailsService = userDetailsService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

//    @Secured("CREATE_USER")
    @Operation(summary = "Create User")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDtoResponse> createUser (@RequestBody UserDtoRequest userDtoRequest) {
        log.info("createUser userDtoRequest: " + userDtoRequest);
        return ResponseEntity.ok(userService.createUser(userDtoRequest));
    }

//    @Secured("GET_USER")
    @Operation(summary = "Get User By Id")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDtoResponse> getUserById(@PathVariable Long id) {
        log.info("getUserById id: " + id);
        return  ResponseEntity.ok(userService.getUserById(id));
    }

//    @Secured("GET_MYUSER")
    @Operation(summary = "Get My User for change profile")
    @GetMapping(value = "/my", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserRegistrationDto> getMyUser(@RequestHeader("Authorization") String jwtToken) {
        log.info("getMyUser jwtToken:" + jwtToken);
        UserPrincipal userPrincipal = (UserPrincipal) userDetailsService
                .loadUserByUsername(jwtTokenProvider.getPhoneEmail(jwtToken));
        return  ResponseEntity.ok(userService.mapToUserRegistrationDto(userPrincipal.getUser()));
    }

//    @Secured("GET_USERS")
    @Operation(summary = "Get All Users")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<UserDtoResponse> getAllUsers() {
        log.info("getAllUsers");
        return userService.getAllUsers();
    }

//    @Secured("GET_USERS")
    @Operation(summary = "Get All Users Page")
    @GetMapping(value = "/pages", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Page<UserDtoResponse> getAllUsersPage(@RequestParam(defaultValue = "1") int page,
                                                 @RequestParam(defaultValue = "10") int size,
                                                 @RequestParam(defaultValue = "DESC") Sort.Direction sortDirection,
                                                 @RequestParam(defaultValue = "id") String sort
                                                 ) {
        log.info("getAllUsersPage page: " + page + " size: " + size + " sort: " + sort);
        return userService.findAllUsersPageable(PageRequest.of(page, size, sortDirection, sort));
    }

//    @Secured("UPDATE_USER")
    @Operation(summary = "Update User")
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDtoResponse> updateUser(@PathVariable Long id
            , @RequestBody @Validated UserDtoRequest userDtoRequest) {
        log.info("updateUser id: " + id + " userDtoRequest: " + userDtoRequest);
        return ResponseEntity.ok(userService.updateUser(id, userDtoRequest));
    }

//    @Secured("DELETE_USER")
    @Operation(summary = "Delete User")
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        log.info("deleteUser id: " + id);
        userService.deleteUser(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
