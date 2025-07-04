package com.example.kirana.controller;

import com.example.kirana.model.Customer;
import com.example.kirana.model.User;
import com.example.kirana.repository.CustomerRepository;
import com.example.kirana.repository.UserRepository;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/customers")
public class CustomerController {
    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<Map<String, String>>> getAllCustomers(@RequestParam(required = false) String userId) {
        String effectiveUserId = userId;
        if (effectiveUserId == null || effectiveUserId.isEmpty()) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) {
                effectiveUserId = auth.getName(); // userId is set as principal in JWT
            }
        }
        if (effectiveUserId == null || effectiveUserId.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        User user = userRepository.findByUserId(effectiveUserId);
        if (user == null) return ResponseEntity.notFound().build();
        List<Map<String, String>> customers = customerRepository.findByUser(user).stream()
                .map(c -> Map.of(
                    "customerId", c.getCustomerId(),
                    "name", c.getName(),
                    "contact", c.getContact(),
                    "userId", c.getUser().getUserId()
                ))
                .toList();
        return ResponseEntity.ok(customers);
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> addCustomer(@Valid @RequestBody Customer customer, @RequestParam(required = false) String userId) {
        try {
            String effectiveUserId = userId;
            if (effectiveUserId == null || effectiveUserId.isEmpty()) {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth != null) {
                    effectiveUserId = auth.getName();
                }
            }
            if (effectiveUserId == null || effectiveUserId.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            User user = userRepository.findByUserId(effectiveUserId);
            if (user == null) {
                return ResponseEntity.badRequest().build();
            }
            customer.setUser(user);
            // Generate UUID based on userId, name, and contact to ensure uniqueness per user
            String base = user.getUserId() + ":" + customer.getName() + ":" + customer.getContact();
            String uuid = UUID.nameUUIDFromBytes(base.getBytes()).toString().replace("-", "");
            customer.setCustomerId(uuid);
            Customer savedCustomer = customerRepository.save(customer);
            // Return customerId, name, contact, and userId in the response
            return ResponseEntity.ok(Map.of(
                "customerId", savedCustomer.getCustomerId(),
                "name", savedCustomer.getName(),
                "contact", savedCustomer.getContact(),
                "userId", user.getUserId()
            ));
        } catch (Exception e) {
            logger.error("Error adding customer", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{customerUuid}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable String customerUuid) {
        if (!customerRepository.existsById(customerUuid)) {
            return ResponseEntity.notFound().build();
        }
        customerRepository.deleteById(customerUuid);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<Map<String, String>> getCustomer(@PathVariable String customerId) {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null || customer.getUser() == null) {
            return ResponseEntity.notFound().build();
        }
        // Optionally, check if the authenticated user matches customer.getUser().getUserId()
        Map<String, String> response = Map.of(
            "customerId", customer.getCustomerId(),
            "name", customer.getName(),
            "contact", customer.getContact(),
            "userId", customer.getUser().getUserId()
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{customerId}")
    public ResponseEntity<Map<String, String>> updateCustomer(@PathVariable String customerId, @RequestBody Map<String, String> updates) {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null) {
            return ResponseEntity.notFound().build();
        }
        boolean changed = false;
        if (updates.containsKey("name")) {
            customer.setName(updates.get("name"));
            changed = true;
        }
        if (updates.containsKey("contact")) {
            customer.setContact(updates.get("contact"));
            changed = true;
        }
        if (!changed) {
            return ResponseEntity.badRequest().build();
        }
        Customer savedCustomer = customerRepository.save(customer);
        Map<String, String> response = Map.of(
            "customerId", savedCustomer.getCustomerId(),
            "name", savedCustomer.getName(),
            "contact", savedCustomer.getContact(),
            "userId", savedCustomer.getUser().getUserId()
        );
        return ResponseEntity.ok(response);
    }
}