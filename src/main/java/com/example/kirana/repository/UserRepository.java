package com.example.kirana.repository;

import com.example.kirana.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
    User findByUsername(String username);
    User findByUserId(String userId);
}