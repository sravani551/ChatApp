package com.sravani.chat.service;

import java.util.List;

import com.sravani.chat.domain.Chat;
import com.sravani.chat.error.ConflictException;
import com.sravani.chat.error.NotFoundException;
import com.sravani.chat.repository.ChatRepository;
import com.sravani.chat.repository.MessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChatService {

    private final ChatRepository chats;
    private final MessageRepository messages;
    private final UserService userService;

    public ChatService(ChatRepository chats, MessageRepository messages, UserService userService) {
        this.chats = chats;
        this.messages = messages;
        this.userService = userService;
    }

    @Transactional
    public Chat start(Long userId, Long otherUserId) {
        if (userId.equals(otherUserId)) {
            throw new ConflictException("Cannot start a chat with yourself");
        }
        userService.get(userId);
        userService.get(otherUserId);
        Chat chat = new Chat(userId, otherUserId);
        chats.findByParticipantLowIdAndParticipantHighId(chat.getParticipantLowId(), chat.getParticipantHighId())
                .ifPresent(existing -> {
                    throw new ConflictException("Chat already exists with id " + existing.getId());
                });
        return chats.save(chat);
    }

    @Transactional(readOnly = true)
    public Chat get(Long chatId) {
        return chats.findById(chatId).orElseThrow(() -> new NotFoundException("Chat " + chatId + " not found"));
    }

    @Transactional(readOnly = true)
    public List<Chat> forUser(Long userId) {
        userService.get(userId);
        return chats.findAllForUser(userId);
    }

    @Transactional
    public void delete(Long chatId) {
        Chat chat = get(chatId);
        messages.deleteByChatId(chat.getId());
        chats.delete(chat);
    }
}
