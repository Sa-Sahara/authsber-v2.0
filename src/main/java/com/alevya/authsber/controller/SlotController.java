package com.alevya.authsber.controller;

import com.alevya.authsber.dto.OrderDtoResponse;
import com.alevya.authsber.model.Slot;
import com.alevya.authsber.security.JwtTokenProvider;
import com.alevya.authsber.service.OrderService;
import com.alevya.authsber.service.SlotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Slot controller",
        description = "Give slots for company:" +
                "/api/v1/slot/**")
//@SecurityRequirement(name = "JWT Authentication")
@RestController
@RequestMapping("/api/v1/slot")
public class SlotController {
    private static final Log log = LogFactory.getLog(SlotController.class);

    private final SlotService slotService;
    private final JwtTokenProvider jwtTokenProvider;
    private final OrderService orderService;

    public SlotController(SlotService slotService,
            JwtTokenProvider jwtTokenProvider,
            OrderService orderService) {
        this.slotService = slotService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.orderService = orderService;
    }

    @Operation(summary = "Get all slots for company")
    @GetMapping(value = "/company/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getSlots(@PathVariable Long id) {
        log.info("getSlots for company id: " + id);
        return ResponseEntity.ok(slotService.getSlots(id));
    }

    @Operation(summary = "Create order by slot")
    @PostMapping(value = "/order",produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrderDtoResponse> createOrder(@RequestHeader("Authorization") String jwtToken,
             @RequestBody Slot slot) {
        log.info("createOrder by slot: " + slot);
        Long idJwt = jwtTokenProvider.getUserId(jwtToken);
        return ResponseEntity.ok(orderService.createOrderBySlot(slot, idJwt));
    }
}
