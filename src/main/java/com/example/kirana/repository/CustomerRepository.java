package com.example.kirana.repository;

import com.example.kirana.model.Customer;
import com.example.kirana.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, String> {
    List<Customer> findByUser(User user);
}