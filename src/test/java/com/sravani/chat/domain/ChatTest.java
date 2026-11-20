package com.sravani.chat.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;

import org.junit.jupiter.api.Test;

class ChatTest {

    @Test
    void participantsAreStoredInCanonicalOrder() {
        Chat ab = new Chat(1L, 2L);
        Chat ba = new Chat(2L, 1L);
        assertThat(ab.getParticipantLowId()).isEqualTo(ba.getParticipantLowId()).isEqualTo(1L);
        assertThat(ab.getParticipantHighId()).isEqualTo(ba.getParticipantHighId()).isEqualTo(2L);
    }

    @Test
    void otherParticipant() {
        Chat chat = new Chat(5L, 3L);
        assertThat(chat.otherParticipant(5L)).isEqualTo(3L);
        assertThat(chat.otherParticipant(3L)).isEqualTo(5L);
        assertThatThrownBy(() -> chat.otherParticipant(9L)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void onlyTheReceiverCanMarkAMessageRead() {
        Message message = new Message(1L, 10L, 20L, "hi");
        message.markReadBy(10L, Instant.EPOCH);
        assertThat(message.getReadAt()).isNull();
        message.markReadBy(20L, Instant.EPOCH);
        assertThat(message.getReadAt()).isEqualTo(Instant.EPOCH);
    }
}
