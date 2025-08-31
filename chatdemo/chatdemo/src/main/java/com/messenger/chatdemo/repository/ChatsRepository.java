package com.messenger.chatdemo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.messenger.chatdemo.model.Chats;

@Repository
public interface ChatsRepository extends JpaRepository<Chats, Long> {
    // Add method to delete a chat by users
    void deleteByUser1IdAndUser2Id(Long user1Id, Long user2Id);

    // Check if a chat already exists
    boolean existsByUser1IdAndUser2Id(Long user1Id, Long user2Id);
}
