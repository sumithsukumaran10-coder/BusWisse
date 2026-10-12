package com.example.buswise.dto;

import com.example.buswise.entity.TripStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TripRequestDTO {

    @NotNull(message = "Bus ID is required")
    private Long busId;

    @NotNull(message = "Route ID is required")
    private Long routeId;

    @NotNull(message = "Departure time is required")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm[:ss]")
    private LocalDateTime departureTime;

    @NotNull(message = "Expected arrival time is required")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm[:ss]")
    private LocalDateTime expectedArrivalTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm[:ss]")
    private LocalDateTime actualArrivalTime;

    private TripStatus status = TripStatus.SCHEDULED;

    @Min(value = 0, message = "Passenger count cannot be negative")
    private Integer passengerCount = 0;

    @DecimalMin(value = "0.00", message = "Ticket revenue cannot be negative")
    private BigDecimal ticketRevenue = BigDecimal.ZERO;

    @DecimalMin(value = "0.00", message = "Additional expenses cannot be negative")
    private BigDecimal additionalExpenses = BigDecimal.ZERO;

    private Boolean overloadException = false;
    private String overloadReason;
    private String notes;

    public TripRequestDTO() {
    }

    public Long getBusId() {
        return busId;
    }

    public void setBusId(Long busId) {
        this.busId = busId;
    }

    public Long getRouteId() {
        return routeId;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
    }

    public LocalDateTime getExpectedArrivalTime() {
        return expectedArrivalTime;
    }

    public void setExpectedArrivalTime(LocalDateTime expectedArrivalTime) {
        this.expectedArrivalTime = expectedArrivalTime;
    }

    public LocalDateTime getActualArrivalTime() {
        return actualArrivalTime;
    }

    public void setActualArrivalTime(LocalDateTime actualArrivalTime) {
        this.actualArrivalTime = actualArrivalTime;
    }

    public TripStatus getStatus() {
        return status;
    }

    public void setStatus(TripStatus status) {
        this.status = status;
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
