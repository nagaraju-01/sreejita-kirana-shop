package com.example.kirana.repository;

import com.example.kirana.model.Debt;
import com.example.kirana.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import java.util.List;

public interface DebtRepository extends JpaRepository<Debt, String> {
    List<Debt> findByCustomer(Customer customer);
    Page<Debt> findByCustomer(Customer customer, Pageable pageable);
}