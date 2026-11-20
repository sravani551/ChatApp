package com.sravani.chat.dto;

import java.time.Instant;

import com.sravani.chat.domain.Chat;

public record ChatResponse(Long id, Long participantA, Long participantB, Instant createdAt) {

    public static ChatResponse from(Chat chat) {
        return new ChatResponse(chat.getId(), chat.getParticipantLowId(), chat.getParticipantHighId(), chat.getCreatedAt());
    }
}
