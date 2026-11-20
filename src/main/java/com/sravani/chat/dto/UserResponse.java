package com.sravani.chat.dto;

import java.time.Instant;

import com.sravani.chat.domain.User;

/** Public view of a user: the password hash never leaves the service. */
public record UserResponse(Long id, String username, Instant createdAt) {

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getCreatedAt());
    }
}
