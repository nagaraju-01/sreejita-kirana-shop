package com.example.kirana.controller;

import com.example.kirana.dto.DebtResponseDTO;
import com.example.kirana.model.Customer;
import com.example.kirana.model.Debt;
import com.example.kirana.repository.CustomerRepository;
import com.example.kirana.repository.DebtRepository;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/debts")
public class DebtController {
    private static final Logger logger = LoggerFactory.getLogger(DebtController.class);

    @Autowired
    private DebtRepository debtRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @PostMapping
    public ResponseEntity<DebtResponseDTO> addDebt(@Valid @RequestBody Debt debt) {
        logger.info("Received request to add debt for customer: {}", debt.getCustomer() != null ? debt.getCustomer().getCustomerId() : "null");
        if (debt.getDate() == null) debt.setDate(LocalDate.now());
        // Fetch the customer from DB to avoid JPA issues
        if (debt.getCustomer() != null && debt.getCustomer().getCustomerId() != null) {
            Customer customer = customerRepository.findById(debt.getCustomer().getCustomerId()).orElse(null);
            if (customer == null) {
                logger.warn("Customer not found for customerId: {}", debt.getCustomer().getCustomerId());
                return ResponseEntity.badRequest().build();
            }
            debt.setCustomer(customer);
            List<Debt> existingDebts = debtRepository.findByCustomer(customer);
            int maxSerial = existingDebts.stream()
                .mapToInt(Debt::getSerialNumber)
                .max()
                .orElse(0);
            debt.setSerialNumber(maxSerial + 1);
        } else {
            return ResponseEntity.badRequest().build();
        }
        // Generate and set a unique debtId using UUID
        debt.setDebtId(java.util.UUID.randomUUID().toString().replace("-", ""));
        Debt savedDebt = debtRepository.save(debt);
        logger.info("Debt saved successfully: {}", savedDebt.getDebtId());
        // Map to DTO
        DebtResponseDTO dto = new DebtResponseDTO();
        dto.setDebtId(savedDebt.getDebtId());
        dto.setAmount(savedDebt.getAmount());
        dto.setDescription(savedDebt.getDescription());
        dto.setDate(savedDebt.getDate() != null ? savedDebt.getDate().toString() : null);
        dto.setSerialNumber(savedDebt.getSerialNumber());
        DebtResponseDTO.CustomerDTO customerDTO = new DebtResponseDTO.CustomerDTO();
        customerDTO.setCustomerId(savedDebt.getCustomer().getCustomerId());
        customerDTO.setName(savedDebt.getCustomer().getName());
        customerDTO.setContact(savedDebt.getCustomer().getContact());
        // userId removed from response
        dto.setCustomer(customerDTO);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<DebtResponseDTO>> getDebtsForCustomer(
            @PathVariable("customerId") String customerId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null) return ResponseEntity.notFound().build();
        List<Debt> debts;
        if (page != null && size != null) {
            Pageable pageable = PageRequest.of(page, size, org.springframework.data.domain.Sort.by("serialNumber"));
            debts = debtRepository.findByCustomer(customer, pageable).getContent();
        } else {
            debts = debtRepository.findByCustomer(customer);
            // Sort debts by serialNumber before mapping to DTOs for consistent ordering
            debts.sort(Comparator.comparingInt(Debt::getSerialNumber));
        }
        // If no records found, return empty list
        if (debts == null || debts.isEmpty()) {
            return ResponseEntity.ok(List.of());
        }
        List<DebtResponseDTO> dtos = debts.stream().map(debt -> {
            DebtResponseDTO dto = new DebtResponseDTO();
            dto.setDebtId(debt.getDebtId());
            dto.setAmount(debt.getAmount());
            dto.setDescription(debt.getDescription());
            dto.setDate(debt.getDate() != null ? debt.getDate().toString() : null);
            dto.setSerialNumber(debt.getSerialNumber());
            DebtResponseDTO.CustomerDTO customerDTO = new DebtResponseDTO.CustomerDTO();
            customerDTO.setCustomerId(debt.getCustomer().getCustomerId());
            customerDTO.setName(debt.getCustomer().getName());
            customerDTO.setContact(debt.getCustomer().getContact());
            dto.setCustomer(customerDTO);
            return dto;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @DeleteMapping("/{debtId}")
    public ResponseEntity<Void> deleteDebt(@PathVariable String debtId) {
        logger.info("Received request to delete debt: {}", debtId);
        Debt debt = debtRepository.findById(debtId).orElse(null);
        if (debt == null) {
            logger.warn("Debt not found for debtId: {}", debtId);
            return ResponseEntity.notFound().build();
        }
        Customer customer = debt.getCustomer();
        debtRepository.deleteById(debtId);
        // Reassign serial numbers for remaining debts
        List<Debt> debts = debtRepository.findByCustomer(customer);
        debts.sort(Comparator.comparing(Debt::getDate).thenComparing(Debt::getDebtId)); // stable order: by date, then debtId
        for (int i = 0; i < debts.size(); i++) {
            debts.get(i).setSerialNumber(i + 1);
        }
        debtRepository.saveAll(debts);
        logger.info("Debt deleted and serial numbers reassigned for customer: {}", customer.getCustomerId());
        return ResponseEntity.noContent().build();
    }
}