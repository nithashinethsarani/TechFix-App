package com.example.techfix_app.models;

public class User {

    private String userId;
    private String name;
    private String email;
    private String phone;
    private String address;

    // Empty constructor required by Firebase Firestore
    public User() {
    }

    // Existing constructor
    // We keep this so your existing SignupActivity code
    // does not break.
    public User(String userId, String name, String email) {
        this.userId = userId;
        this.name = name;
        this.email = email;
    }

    // New constructor for the complete profile
    public User(String userId, String name, String email,
                String phone, String address) {

        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }

    // Getters

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    // Setters
    // Firestore can use these when converting
    // a Firestore document into a User object.

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}