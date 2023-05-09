package com.alevya.authsber.model;

import com.alevya.authsber.model.enums.MessageType;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Table(name = "message")
@Getter
@Setter
public final class Message {

    @Id
    @SequenceGenerator(name = "message_seq", sequenceName = "message_seq", allocationSize = 1)
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="message_seq")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    private MessageType type;

    @NotNull
    private String accessCode;

    @Column(name = "user_id", nullable = false)
    @NotNull
    private Long userId;

    @Column (name = "create_time_millis")
    @NotNull
    Long createTime;

    public Message() {
    }

    @Builder
    public Message(Long id,
                   MessageType type,
                   String accessCode,
                   Long userId,
                   Long createTime) {
        this.id = id;
        this.type = type;
        this.accessCode = accessCode;
        this.userId = userId;
        this.createTime = createTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message)) return false; //Liskov, Hibernate, and many mocking frameworks such as Mockito and EasyMock, use bytecode manipulation tricks to generate subclasses on the fly. That means that an object that you instantiate yourself could never be equal to an object that you fetch from the database, even if all the fields have the same values, simply because they’re not of the same type.

        Message message = (Message) o;

        if (!Objects.equals(type, message.type)) return false;
        if (!Objects.equals(accessCode, message.accessCode)) return false;
        if (!Objects.equals(userId, message.userId)) return false;
        return Objects.equals(createTime, message.createTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, accessCode, userId, createTime);
    }

    @Override
    public String toString() {
        return "Message{" +
                "id = " + id +
                ", type = '" + type + '\'' +
                ", accessCode = '" + accessCode + '\'' +
                ", userId = " + userId +
                ", createTime = " + createTime +
                '}';
    }
}
