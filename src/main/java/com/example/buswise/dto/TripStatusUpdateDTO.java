package com.example.buswise.dto;

import com.example.buswise.entity.TripStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TripStatusUpdateDTO {

    @NotNull(message = "New status is required")
    private TripStatus status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm[:ss]")
    private LocalDateTime actualArrivalTime;

    @Min(value = 0, message = "Passenger count cannot be negative")
    private Integer passengerCount;

    @DecimalMin(value = "0.00", message = "Ticket revenue cannot be negative")
    private BigDecimal ticketRevenue;

    @DecimalMin(value = "0.00", message = "Additional expenses cannot be negative")
    private BigDecimal additionalExpenses;

    private Boolean overloadException;
    private String overloadReason;
    private String notes;

    public TripStatusUpdateDTO() {
    }

    public TripStatus getStatus() {
        return status;
    }

    public void setStatus(TripStatus status) {
        this.status = status;
    }

    public LocalDateTime getActualArrivalTime() {
        return actualArrivalTime;
    }

    public void setActualArrivalTime(LocalDateTime actualArrivalTime) {
        this.actualArrivalTime = actualArrivalTime;
    }

    public Integer getPassengerCount() {
        return passengerCount;
    }

    public void setPassengerCount(Integer passengerCount) {
        this.passengerCount = passengerCount;
    }

    public BigDecimal getTicketRevenue() {
        return ticketRevenue;
    }

    public void setTicketRevenue(BigDecimal ticketRevenue) {
        this.ticketRevenue = ticketRevenue;
    }

    public BigDecimal getAdditionalExpenses() {
        return additionalExpenses;
    }

    public void setAdditionalExpenses(BigDecimal additionalExpenses) {
        this.additionalExpenses = additionalExpenses;
    }

    public Boolean getOverloadException() {
        return overloadException;
    }

    public void setOverloadException(Boolean overloadException) {
        this.overloadException = overloadException;
    }

    public String getOverloadReason() {
        return overloadReason;
    }

    public void setOverloadReason(String overloadReason) {
        this.overloadReason = overloadReason;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
