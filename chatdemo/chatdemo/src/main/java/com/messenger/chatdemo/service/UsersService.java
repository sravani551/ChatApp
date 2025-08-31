package com.messenger.chatdemo.service;

import com.messenger.chatdemo.model.Users;

public interface UsersService {

    Users createUser(Users user);

    Users login(String username, String password);

    void deleteUser(Long userId);
}
