package com.messenger.chatdemo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.messenger.chatdemo.model.Messages;
import com.messenger.chatdemo.service.MessageService;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    // Send a Message
    @PostMapping("/send")
    public ResponseEntity<String> sendMessage(@RequestBody Messages message) {
        messageService.sendMessage(message);
        return ResponseEntity.ok("Message sent successfully!");
    }

    // Read Messages in a Chat
    @GetMapping("/read/{chatId}")
    public ResponseEntity<List<Messages>> readMessages(@PathVariable Long chatId) {
        List<Messages> messages = messageService.readMessages(chatId);
        return ResponseEntity.ok(messages);
    }

    // Get Unread Message Count for a Receiver
    @GetMapping("/unread-count/{receiverId}")
    public ResponseEntity<Long> getUnreadMessageCount(@PathVariable Long receiverId) {
        Long count = messageService.getUnreadMessageCount(receiverId);
        return ResponseEntity.ok(count);
    }
}