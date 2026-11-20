package com.sravani.chat.web;

import java.util.List;
import java.util.Map;

import com.sravani.chat.dto.ChatResponse;
import com.sravani.chat.dto.LoginRequest;
import com.sravani.chat.dto.SignupRequest;
import com.sravani.chat.dto.UserResponse;
import com.sravani.chat.service.ChatService;
import com.sravani.chat.service.MessageService;
import com.sravani.chat.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    private final ChatService chatService;
    private final MessageService messageService;

    public UserController(UserService userService, ChatService chatService, MessageService messageService) {
        this.userService = userService;
        this.chatService = chatService;
        this.messageService = messageService;
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse signup(@Valid @RequestBody SignupRequest request) {
        return UserResponse.from(userService.signup(request));
    }

    @PostMapping("/auth/login")
    public UserResponse login(@Valid @RequestBody LoginRequest request) {
        return UserResponse.from(userService.login(request));
    }

    @GetMapping("/users/{userId}")
    public UserResponse get(@PathVariable Long userId) {
        return UserResponse.from(userService.get(userId));
    }

    @DeleteMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long userId) {
        userService.delete(userId);
    }

    @GetMapping("/users/{userId}/chats")
    public List<ChatResponse> chats(@PathVariable Long userId) {
        return chatService.forUser(userId).stream().map(ChatResponse::from).toList();
    }

    @GetMapping("/users/{userId}/unread-count")
    public Map<String, Long> unreadCount(@PathVariable Long userId) {
        return Map.of("unread", messageService.unreadCount(userId));
    }
}
