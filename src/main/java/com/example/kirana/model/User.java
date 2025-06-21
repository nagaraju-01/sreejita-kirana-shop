package com.example.kirana.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @Column(name = "user_id", unique = true, nullable = false, updatable = false)
    private String userId;
    @Column(unique = true)
    private String username;
    private String password; // Store hashed password in production

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}