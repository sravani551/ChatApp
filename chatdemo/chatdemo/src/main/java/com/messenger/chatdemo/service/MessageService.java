package com.messenger.chatdemo.service;

import java.util.List;

import com.messenger.chatdemo.model.Messages;

public interface MessageService {
    void sendMessage(Messages message);

    List<Messages> readMessages(Long chatId);

    Long getUnreadMessageCount(Long receiverId);

}
