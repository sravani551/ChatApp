package com.sravani.chat.repository;

import java.util.List;

import com.sravani.chat.domain.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByChatIdOrderBySentAtAscIdAsc(Long chatId, Pageable pageable);

    long countByReceiverIdAndReadAtIsNull(Long receiverId);

    @Modifying
    @Query("delete from Message m where m.chatId = :chatId")
    void deleteByChatId(Long chatId);
}
