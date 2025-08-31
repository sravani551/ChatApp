package com.messenger.chatdemo.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.messenger.chatdemo.model.Chats;
import com.messenger.chatdemo.repository.ChatsRepository;
import com.messenger.chatdemo.service.ChatService;

import jakarta.transaction.Transactional;

@Service
public class ChatServiceImpl implements ChatService {

    private final ChatsRepository chatRepository;

    @Autowired
    public ChatServiceImpl(ChatsRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    @Override
    public Chats startChat(Long user1Id, Long user2Id) {
        // Check if chat already exists
        if (chatRepository.existsByUser1IdAndUser2Id(user1Id, user2Id)) {
            throw new RuntimeException("Chat already exists between these users.");
        }
        Chats chat = new Chats(user1Id, user2Id);
        return chatRepository.save(chat);
    }

    @Override
    @Transactional
    public void deleteChat(Long user1Id, Long user2Id) {
        System.out.println("Deleting chat between user1Id: " + user1Id + " and user2Id: " + user2Id);

        if (!chatRepository.existsByUser1IdAndUser2Id(user1Id, user2Id)) {
            throw new RuntimeException("Chat does not exist.");
        }

        chatRepository.deleteByUser1IdAndUser2Id(user1Id, user2Id);
        System.out.println("Chat deleted successfully.");
    }
}
