package com.alevya.authsber.controller;

import com.alevya.authsber.dto.OrderDtoRequest;
import com.alevya.authsber.dto.OrderDtoResponse;
import com.alevya.authsber.security.JwtTokenProvider;
import com.alevya.authsber.service.OrderService;
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

@Tag(name = "Order controller",
        description = "Give CRUD functional for Order:" +
                "/api/v1/order/**")
//@SecurityRequirement(name = "JWT Authentication")
@RestController
@RequestMapping("/api/v1/order")
public class OrderController {
    private final OrderService orderService;
    private final JwtTokenProvider jwtTokenProvider;

    public OrderController(OrderService orderService,
             JwtTokenProvider jwtTokenProvider) {
        this.orderService = orderService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

//    @Secured("CREATE_ORDER")
    @Operation(summary = "Create order")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrderDtoResponse> createOrder(
            @RequestBody OrderDtoRequest orderDtoRequest) {
        return ResponseEntity.ok(orderService.createOrder(orderDtoRequest));
    }

//    @Secured("GET_ORDER")
    @Operation(summary = "Get order by id")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrderDtoResponse> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

//    @Secured("GET_ORDER")
    @Operation(summary = "Get order by user id")
    @GetMapping(value = "/user/id", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<OrderDtoResponse> getOrderByUserId(@RequestHeader("Authorization") String jwtToken) {
        Long idJwt = jwtTokenProvider.getUserId(jwtToken);
        return orderService.getOrderByUserId(idJwt);
    }

//    @Secured("GET_ORDER")
    @Operation(summary = "Get order by worker id")
    @GetMapping(value = "/worker/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<OrderDtoResponse> getOrderByWorkerId(@PathVariable Long id) {
        return orderService.getOrderByWorkerId(id);
    }

//    @Secured("GET_ORDER")
    @Operation(summary = "Get order by workplace id")
    @GetMapping(value = "/workplace/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<OrderDtoResponse> getOrderByWorkplaceId(@PathVariable Long id) {
        return orderService.getOrderByWorkplaceId(id);
    }

//    @Secured("GET_ORDERS")
    @Operation(summary = "Get all orders")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<OrderDtoResponse> getAllOrders() {
        return orderService.getAllOrders();
    }

//    @Secured("UPDATE_ORDER")
    @Operation(summary = "Update order")
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrderDtoResponse> updateOrder(
            @PathVariable Long id,
            @RequestBody @Validated OrderDtoRequest orderDtoRequest) {
        return ResponseEntity.ok(orderService.updateOrder(id, orderDtoRequest));
    }

//    @Secured("DELETE_ORDER")
    @Operation(summary = "Delete order")
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

//    @Secured("GET_ORDERS")
    @Operation(summary = "Get All orders Page")
    @GetMapping(value = "/pages", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Page<OrderDtoResponse> getAllOrdersPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "DESC") Sort.Direction sortDirection,
            @RequestParam(defaultValue = "id") String sort
    ) {
        return orderService.findAllOrdersPageable(PageRequest.of(page, size, sortDirection, sort));
    }
}
