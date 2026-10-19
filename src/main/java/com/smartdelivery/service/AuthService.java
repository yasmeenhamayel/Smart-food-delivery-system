package com.smartdelivery.service;


import com.smartdelivery.domain.User;
import java.util.List;

public class AuthService {

    private final List<User> users;

    public AuthService(List<User> users) {
        this.users = users;
    }

    public User login(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username)
                    && user.checkPassword(password)) {
                return user;
            }
        }

        return null;
    }
}