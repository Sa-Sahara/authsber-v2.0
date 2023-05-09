package com.alevya.authsber.service;

import com.alevya.authsber.dto.MessageDtoRequest;
import com.alevya.authsber.dto.MessageDtoResponse;
import com.alevya.authsber.exception.BadRequestException;
import com.alevya.authsber.exception.NotFoundException;
import com.alevya.authsber.model.Message;
import com.alevya.authsber.model.enums.MessageType;
import com.alevya.authsber.repository.MessageRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
public class MessageService {
    private static final Log log = LogFactory.getLog(MessageService.class);
    private MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public MessageDtoResponse createMessage(MessageDtoRequest messageDtoRequest) {
        if (messageDtoRequest == null) {
            throw new BadRequestException("Invalid message");
        }
        log.info("createMessage: " + messageDtoRequest);
        return mapToMessageDto(messageRepository.save(mapToMessage(messageDtoRequest)));
    }

    @Transactional
    public MessageDtoResponse createCheckMessage(MessageDtoRequest messageDtoRequest) {
        if (messageDtoRequest == null) {
            throw new BadRequestException("Invalid message");
        }
        if (messageDtoRequest.getUserId() == null) {
            throw new BadRequestException("Invalid user id");
        }
        log.info("createCheckMessage: " + messageDtoRequest);
        messageRepository.deleteAllByUserIdAndType(messageDtoRequest.getUserId(), messageDtoRequest.getType());
        return mapToMessageDto(messageRepository.save(mapToMessage(messageDtoRequest)));
    }

    public MessageDtoResponse getMessageById(Long id) {
        if (id == null) {
            throw new BadRequestException("Invalid ID");
        }
        log.info("getMessageById: " + id);
        return mapToMessageDto(messageRepository.findById(id).orElseThrow(()
                -> new NotFoundException("Message not found!")));
    }

    public MessageDtoResponse getLastMessageByUserIdAndMessageType(Long id, MessageType type) {
        if (id == null) {
            throw new BadRequestException("Invalid ID");
        }
        log.info("getLastMessageByTypeAndUserId: " + id);
        return mapToMessageDto(messageRepository.findLastByUserIdAndType(id, type));
    }

    public MessageDtoResponse mapToMessageDto(Message message) {
        if (message == null) {
            throw new BadRequestException("Invalid message");
        }
        MessageDtoResponse messageDto = MessageDtoResponse.builder()
                .id(message.getId())
                .type(message.getType())
                .accessCode(message.getAccessCode())
                .userId(message.getUserId())
                .createTime(message.getCreateTime())
                .build();
        log.info("mapToMessageDto: " + message);
        return messageDto;
    }

    public Message mapToMessage(MessageDtoRequest messageDtoRequest) {
        if (messageDtoRequest == null) {
            throw new BadRequestException("Invalid messageDtoRequest");
        }
        Message message = new Message();
        message.setType(messageDtoRequest.getType());
        message.setAccessCode(messageDtoRequest.getAccessCode());
        message.setUserId(messageDtoRequest.getUserId());
        message.setCreateTime(messageDtoRequest.getCreateTime());
        log.info("mapToMessage: " + messageDtoRequest);
        return message;
    }

    public Page<MessageDtoResponse> findAllMessagesPageable(Pageable pageable) {
        Page<Message> page = messageRepository.findAll(pageable);
        return new PageImpl<>(page.stream().map(this::mapToMessageDto)
                .collect(Collectors.toList()), page.getPageable(), page.getTotalElements());
    }
}
