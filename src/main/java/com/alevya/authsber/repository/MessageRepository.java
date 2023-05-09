package com.alevya.authsber.repository;

import com.alevya.authsber.model.Message;
import com.alevya.authsber.model.enums.MessageType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    Message findLastByUserIdAndType(Long userId, MessageType type);
    void deleteAllByUserIdAndType(Long userId, MessageType type);
}
