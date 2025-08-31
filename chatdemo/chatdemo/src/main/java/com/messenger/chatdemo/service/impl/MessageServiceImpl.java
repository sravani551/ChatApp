
package com.messenger.chatdemo.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.messenger.chatdemo.model.Messages;
import com.messenger.chatdemo.repository.MessageRepository;
import com.messenger.chatdemo.service.MessageService;

@Service
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;

    @Autowired
    public MessageServiceImpl(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public void sendMessage(Messages message) {
        messageRepository.save(message);
    }

    @Override
    public List<Messages> readMessages(Long chatId) {
        List<Messages> messages = messageRepository.findByChatId(chatId);
        for (Messages message : messages) {
            message.setRead(true); // Mark as read
        }
        messageRepository.saveAll(messages);
        return messages;
    }

    @Override
    public Long getUnreadMessageCount(Long receiverId) {
        return messageRepository.countByReceiverIdAndIsReadFalse(receiverId);
    }

}