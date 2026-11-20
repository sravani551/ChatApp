package com.sravani.chat.dto;

import java.time.Instant;

import com.sravani.chat.domain.Message;

public record MessageResponse(Long id, Long chatId, Long senderId, Long receiverId, String content,
                              Instant sentAt, Instant readAt) {

    public static MessageResponse from(Message m) {
        return new MessageResponse(m.getId(), m.getChatId(), m.getSenderId(), m.getReceiverId(),
                m.getContent(), m.getSentAt(), m.getReadAt());
    }
}
