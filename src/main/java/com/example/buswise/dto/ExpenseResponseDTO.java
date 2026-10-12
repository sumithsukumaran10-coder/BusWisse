package com.example.buswise.dto;

import com.example.buswise.entity.Expense;
import com.example.buswise.entity.ExpenseCategory;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ExpenseResponseDTO {
    private Long id;
    private Long busId;
    private String busRegistrationNumber;
    private Long tripId;
    private String tripRouteName;
    private LocalDate expenseDate;
    private ExpenseCategory category;
    private BigDecimal amount;
    private String description;
    private LocalDateTime createdAt;

    public ExpenseResponseDTO() {
    }

    public static ExpenseResponseDTO fromEntity(Expense expense) {
        if (expense == null) return null;
        ExpenseResponseDTO dto = new ExpenseResponseDTO();
        dto.setId(expense.getId());
        if (expense.getBus() != null) {
            dto.setBusId(expense.getBus().getId());
            dto.setBusRegistrationNumber(expense.getBus().getRegistrationNumber());
        }
        if (expense.getTrip() != null) {
            dto.setTripId(expense.getTrip().getId());
            if (expense.getTrip().getRoute() != null) {
                dto.setTripRouteName(expense.getTrip().getRoute().getRouteName());
            }
        }
        dto.setExpenseDate(expense.getExpenseDate());
        dto.setCategory(expense.getCategory());
        dto.setAmount(expense.getAmount());
        dto.setDescription(expense.getDescription());
        dto.setCreatedAt(expense.getCreatedAt());
        return dto;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBusId() {
        return busId;
    }

    public void setBusId(Long busId) {
        this.busId = busId;
    }

    public String getBusRegistrationNumber() {
        return busRegistrationNumber;
    }

    public void setBusRegistrationNumber(String busRegistrationNumber) {
        this.busRegistrationNumber = busRegistrationNumber;
    }

    public Long getTripId() {
        return tripId;
    }

    public void setTripId(Long tripId) {
        this.tripId = tripId;
    }

    public String getTripRouteName() {
        return tripRouteName;
    }

    public void setTripRouteName(String tripRouteName) {
        this.tripRouteName = tripRouteName;
    }

    public LocalDate getExpenseDate() {
        return expenseDate;
    }

    public void setExpenseDate(LocalDate expenseDate) {
        this.expenseDate = expenseDate;
    }

    public ExpenseCategory getCategory() {
        return category;
    }

    public void setCategory(ExpenseCategory category) {
        this.category = category;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
