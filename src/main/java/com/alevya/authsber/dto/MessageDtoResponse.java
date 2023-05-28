package com.alevya.authsber.dto;


import com.alevya.authsber.model.enums.MessageType;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@ToString
public class MessageDtoResponse {
    private Long id;
    private MessageType type;
    private Long accessCode;
    private Long userId;
    private Long createTime;
}
