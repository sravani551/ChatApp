package com.messenger.chatdemo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.messenger.chatdemo.model.Chats;
import com.messenger.chatdemo.service.ChatService;

@RestController
@RequestMapping("/api/chats")
public class ChatController {

    private final ChatService chatService;

    @Autowired
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    // Start a new chat
    @PostMapping("/start")
    public ResponseEntity<Chats> startChat(@RequestParam Long user1Id, @RequestParam Long user2Id) {
        Chats chat = chatService.startChat(user1Id, user2Id);
        return ResponseEntity.ok(chat);
    }

    // Delete an existing chat
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteChat(@RequestParam Long user1Id, @RequestParam Long user2Id) {
        chatService.deleteChat(user1Id, user2Id);
        return ResponseEntity.ok("Chat deleted successfully.");
    }
}
