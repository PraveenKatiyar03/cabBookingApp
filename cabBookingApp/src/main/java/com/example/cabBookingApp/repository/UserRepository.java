package com.example.cabBookingApp.repository;

import com.example.cabBookingApp.exceptionHandler.UserNotFoundException;
import com.example.cabBookingApp.model.UserDetails;

import java.util.ArrayList;
import java.util.List;

public class UserRepository {

    private final List<UserDetails> users = new ArrayList<>();

    public void addUser(UserDetails user) {
        users.add(user);
    }

    public UserDetails getUserByUsername(String username) {
        return users.stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException(username));
    }
}
