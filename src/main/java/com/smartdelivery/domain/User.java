package com.smartdelivery.domain;

public class User {

    private String name;
    private String username;
    private String password;
    private Role role;

    public User(String name, String username, String password, Role role) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public Role getRole() {
        return role;
    }

    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }
}