package com.messenger.chatdemo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.messenger.chatdemo.model.Messages;

@Repository
public interface MessageRepository extends JpaRepository<Messages, Long> {

    List<Messages> findByChatIdAndIsReadFalse(Long chatId);

    Long countByReceiverIdAndIsReadFalse(Long receiverId);

    List<Messages> findByChatId(Long chatId);
}
