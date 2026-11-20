package com.sravani.chat.repository;

import java.util.List;
import java.util.Optional;

import com.sravani.chat.domain.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    Optional<Chat> findByParticipantLowIdAndParticipantHighId(Long lowId, Long highId);

    @Query("select c from Chat c where c.participantLowId = :userId or c.participantHighId = :userId order by c.createdAt desc")
    List<Chat> findAllForUser(Long userId);
}
