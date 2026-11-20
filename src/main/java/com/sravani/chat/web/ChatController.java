package com.sravani.chat.web;

import java.util.List;

import com.sravani.chat.dto.ChatResponse;
import com.sravani.chat.dto.MessageResponse;
import com.sravani.chat.dto.SendMessageRequest;
import com.sravani.chat.dto.StartChatRequest;
import com.sravani.chat.service.ChatService;
import com.sravani.chat.service.MessageService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chats")
public class ChatController {

    private final ChatService chatService;
    private final MessageService messageService;

    public ChatController(ChatService chatService, MessageService messageService) {
        this.chatService = chatService;
        this.messageService = messageService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ChatResponse start(@Valid @RequestBody StartChatRequest request) {
        return ChatResponse.from(chatService.start(request.userId(), request.otherUserId()));
    }

    @DeleteMapping("/{chatId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long chatId) {
        chatService.delete(chatId);
    }

    @PostMapping("/{chatId}/messages")
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponse send(@PathVariable Long chatId, @Valid @RequestBody SendMessageRequest request) {
        return MessageResponse.from(messageService.send(chatId, request.senderId(), request.content()));
    }

    @GetMapping("/{chatId}/messages")
    public List<MessageResponse> read(@PathVariable Long chatId,
                                      @RequestParam Long readerId,
                                      @RequestParam(defaultValue = "0") @Min(0) int page,
                                      @RequestParam(defaultValue = "50") @Min(1) @Max(200) int size) {
        return messageService.read(chatId, readerId, page, size).stream().map(MessageResponse::from).toList();
    }
}
