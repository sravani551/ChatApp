package com.messenger.chatdemo.service;

import com.messenger.chatdemo.model.Chats;

public interface ChatService {
    Chats startChat(Long user1Id, Long user2Id);

    void deleteChat(Long user1Id, Long user2Id);
}
