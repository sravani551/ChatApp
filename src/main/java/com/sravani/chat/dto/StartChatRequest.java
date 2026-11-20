package com.sravani.chat.dto;

import jakarta.validation.constraints.NotNull;

public record StartChatRequest(@NotNull Long userId, @NotNull Long otherUserId) {
}
