package com.example.kirana.controller;

import com.example.kirana.model.SignupRequest;
import com.example.kirana.model.User;
import com.example.kirana.repository.UserRepository;
import com.example.kirana.security.JwtUtil;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> signup(@Valid @RequestBody SignupRequest signupRequest) {
        logger.info("Received signup request for username: {}", signupRequest.getUsername());

        if (signupRequest.getUsername() == null || signupRequest.getUsername().trim().isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("state", "failed", "error", "Username cannot be empty"));
        }
        if (!signupRequest.getPassword().equals(signupRequest.getConfirmPassword())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("state", "failed", "error", "Passwords do not match"));
        }
        if (userRepository.findByUsername(signupRequest.getUsername()) != null) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of("state", "failed", "error", "Username already exists"));
        }
        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        // Generate a random UUID for userId
        user.setUserId(UUID.randomUUID().toString().replace("-",""));
        // Or, for deterministic UUID based on username:
        // user.setUserId(UUID.nameUUIDFromBytes(signupRequest.getUsername().getBytes()).toString());
        userRepository.save(user);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of("state", "success", "message", "User registered", "userId", user.getUserId()));
    }

    @PostMapping("/signin")
    public ResponseEntity<Map<String, String>> signin(@RequestBody Map<String, String> loginRequest) {
        logger.info("Received signin request for username: {}", loginRequest.get("username"));
        
        String userId = loginRequest.get("userId");
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");
        User dbUser = null;
        if (userId != null && !userId.isEmpty()) {
            dbUser = userRepository.findByUserId(userId);
        } else if (username != null && !username.isEmpty()) {
            dbUser = userRepository.findByUsername(username);
        }
        if (dbUser != null && passwordEncoder.matches(password, dbUser.getPassword())) {
            return ResponseEntity.ok(Map.of(
                    "state", "success",
                    "token", jwtUtil.generateToken(dbUser.getUserId()),
                    "userId", dbUser.getUserId(),
                    "message", "Signin successful"
            ));
        }
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Map.of(
                        "state", "failed",
                        "error", "Invalid credentials"
                ));
    }
}