package com.alevya.authsber.controller;

import com.alevya.authsber.dto.MessageDtoRequest;
import com.alevya.authsber.dto.MessageDtoResponse;
import com.alevya.authsber.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Message controller"
        , description = "Give CRUD functional for message:" +
        "/api/v1/message/**")
//@SecurityRequirement(name = "JWT Authentication")
@RestController
@RequestMapping("/api/v1/message")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @Operation(summary = "Create message")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageDtoResponse> createMessage(
            @RequestBody MessageDtoRequest messageDtoRequest) {
        return ResponseEntity.ok(messageService.createMessage(messageDtoRequest));
    }

    @Operation(summary = "Get message by id")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageDtoResponse> getMessageById(@PathVariable Long id) {
        return ResponseEntity.ok(messageService.getMessageById(id));
    }
}
