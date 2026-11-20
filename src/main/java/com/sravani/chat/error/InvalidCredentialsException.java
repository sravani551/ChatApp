package com.sravani.chat.error;

public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        // Same message for unknown user and wrong password, so usernames can't be probed.
        super("Invalid username or password");
    }
}
