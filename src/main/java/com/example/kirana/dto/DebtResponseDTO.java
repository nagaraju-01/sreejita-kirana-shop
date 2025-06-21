package com.example.kirana.dto;

public class DebtResponseDTO {
    private String debtId;
    private double amount;
    private String description;
    private String date;
    private int serialNumber;
    private CustomerDTO customer;

    // Getters and setters
    public String getDebtId() { return debtId; }
    public void setDebtId(String debtId) { this.debtId = debtId; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public int getSerialNumber() { return serialNumber; }
    public void setSerialNumber(int serialNumber) { this.serialNumber = serialNumber; }
    public CustomerDTO getCustomer() { return customer; }
    public void setCustomer(CustomerDTO customer) { this.customer = customer; }

    public static class CustomerDTO {
        private String customerId;
        private String name;
        private String contact;
        // Getters and setters
        public String getCustomerId() { return customerId; }
        public void setCustomerId(String customerId) { this.customerId = customerId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getContact() { return contact; }
        public void setContact(String contact) { this.contact = contact; }
    }
}
