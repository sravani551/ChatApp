package com.sravani.chat.service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

import com.sravani.chat.domain.Chat;
import com.sravani.chat.domain.Message;
import com.sravani.chat.error.ForbiddenException;
import com.sravani.chat.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MessageService {

    private final MessageRepository messages;
    private final ChatService chatService;
    private final UserService userService;
    private final Clock clock;

    @Autowired
    public MessageService(MessageRepository messages, ChatService chatService, UserService userService) {
        this(messages, chatService, userService, Clock.systemUTC());
    }

    MessageService(MessageRepository messages, ChatService chatService, UserService userService, Clock clock) {
        this.messages = messages;
        this.chatService = chatService;
        this.userService = userService;
        this.clock = clock;
    }

    @Transactional
    public Message send(Long chatId, Long senderId, String content) {
        Chat chat = participantChat(chatId, senderId);
        return messages.save(new Message(chat.getId(), senderId, chat.otherParticipant(senderId), content.strip()));
    }

    /**
     * Returns a page of the conversation and marks as read only the messages addressed to
     * the reader. Opening a chat must not mark the reader's own sent messages as read.
     */
    @Transactional
    public List<Message> read(Long chatId, Long readerId, int page, int size) {
        participantChat(chatId, readerId);
        List<Message> conversation = messages.findByChatIdOrderBySentAtAscIdAsc(chatId, PageRequest.of(page, size));
        Instant now = clock.instant();
        conversation.forEach(m -> m.markReadBy(readerId, now));
        return conversation;
    }

    @Transactional(readOnly = true)
    public long unreadCount(Long userId) {
        userService.get(userId);
        return messages.countByReceiverIdAndReadAtIsNull(userId);
    }

    private Chat participantChat(Long chatId, Long userId) {
        Chat chat = chatService.get(chatId);
        if (!chat.hasParticipant(userId)) {
            throw new ForbiddenException("User " + userId + " is not a participant of chat " + chatId);
        }
        return chat;
    }
}
