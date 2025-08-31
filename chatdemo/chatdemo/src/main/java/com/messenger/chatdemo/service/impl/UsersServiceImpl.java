package com.messenger.chatdemo.service.impl;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.messenger.chatdemo.model.Users;
import com.messenger.chatdemo.repository.UsersRepository;
import com.messenger.chatdemo.service.UsersService;

@Service
public class UsersServiceImpl implements UsersService {

    private final UsersRepository userRepository;

    @Autowired
    public UsersServiceImpl(UsersRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Users createUser(Users user) {
        return userRepository.save(user);
    }

    @Override
    public Users login(String username, String password) {
        Optional<Users> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found with username: " + username);
        }

        Users user = userOpt.get();
        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("Invalid password");
        }

        return user;

    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

}
