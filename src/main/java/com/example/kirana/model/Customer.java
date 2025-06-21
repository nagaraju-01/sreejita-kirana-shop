package com.example.kirana.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
public class Customer {
    @Id
    @Column(name = "customer_id", unique = true, nullable = false, updatable = false)
    private String customerId;

    @NotBlank(message = "Customer name is required")
    @Size(min = 2, max = 50, message = "Customer name must be 2-50 characters")
    private String name;

    @NotBlank(message = "Contact is required")
    @Size(min = 10, max = 15, message = "Contact must be 10-15 digits")
    private String contact;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<Debt> debts = new java.util.ArrayList<>();

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public java.util.List<Debt> getDebts() { return debts; }
    public void setDebts(java.util.List<Debt> debts) { this.debts = debts; }
}