package com.sravani.chat.domain;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * A one-to-one conversation. Participants are stored in a canonical order (lower id first)
 * with a unique constraint, so a chat between A and B is the same chat whoever started it.
 */
@Entity
@Table(name = "chats", uniqueConstraints = @UniqueConstraint(columnNames = {"participant_low_id", "participant_high_id"}))
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "participant_low_id", nullable = false)
    private Long participantLowId;

    @Column(name = "participant_high_id", nullable = false)
    private Long participantHighId;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected Chat() {
        // for JPA
    }

    public Chat(Long userA, Long userB) {
        this.participantLowId = Math.min(userA, userB);
        this.participantHighId = Math.max(userA, userB);
    }

    public boolean hasParticipant(Long userId) {
        return participantLowId.equals(userId) || participantHighId.equals(userId);
    }

    public Long otherParticipant(Long userId) {
        if (!hasParticipant(userId)) {
            throw new IllegalArgumentException("User " + userId + " is not in chat " + id);
        }
        return participantLowId.equals(userId) ? participantHighId : participantLowId;
    }

    public Long getId() {
        return id;
    }

    public Long getParticipantLowId() {
        return participantLowId;
    }

    public Long getParticipantHighId() {
        return participantHighId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
