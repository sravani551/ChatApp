package com.sravani.chat.service;

import com.sravani.chat.domain.User;
import com.sravani.chat.dto.LoginRequest;
import com.sravani.chat.dto.SignupRequest;
import com.sravani.chat.error.ConflictException;
import com.sravani.chat.error.InvalidCredentialsException;
import com.sravani.chat.error.NotFoundException;
import com.sravani.chat.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository users, PasswordEncoder passwordEncoder) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User signup(SignupRequest request) {
        if (users.existsByUsername(request.username())) {
            throw new ConflictException("Username already taken");
        }
        return users.save(new User(request.username(), passwordEncoder.encode(request.password())));
    }

    @Transactional(readOnly = true)
    public User login(LoginRequest request) {
        return users.findByUsername(request.username())
                .filter(u -> passwordEncoder.matches(request.password(), u.getPasswordHash()))
                .orElseThrow(InvalidCredentialsException::new);
    }

    @Transactional(readOnly = true)
    public User get(Long userId) {
        return users.findById(userId).orElseThrow(() -> new NotFoundException("User " + userId + " not found"));
    }

    @Transactional
    public void delete(Long userId) {
        users.delete(get(userId));
    }
}
