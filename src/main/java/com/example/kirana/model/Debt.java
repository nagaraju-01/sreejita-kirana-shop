package com.example.kirana.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@Entity
public class Debt {

    @Id
    @Column(name = "debt_id", unique = true, nullable = false, updatable = false)
    private String debtId;

    @Min(value = 1, message = "Amount must be greater than 0")
    private double amount;

    @NotBlank(message = "Description is required")
    @Size(max = 255, message = "Description must be at most 255 characters")
    private String description;

    @NotNull(message = "Date is required")
    private LocalDate date;

    @Column(name = "serial_number")
    private int serialNumber;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
    
    public String getDebtId() { return debtId; }
    public void setDebtId(String debtId) { this.debtId = debtId; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public int getSerialNumber() { return serialNumber; }
    public void setSerialNumber(int serialNumber) { this.serialNumber = serialNumber; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
}