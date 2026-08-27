package com.example.techfix_app.data.model;

public class User {
    private String userId;
    private String name;
    private String email;

    // empty constructor for Firebase
    public User() {}

    public User(String userId, String name, String email) {
        this.userId = userId;
        this.name = name;
        this.email = email;
    }

    public String getUserId() { return userId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
}