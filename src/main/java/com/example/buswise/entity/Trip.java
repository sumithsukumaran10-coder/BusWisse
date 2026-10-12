package com.example.buswise.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing an operational trip scheduled or run by a bus along a specific route.
 */
@Entity
@Table(name = "trips", indexes = {
    @Index(name = "idx_trip_bus_time", columnList = "bus_id, departure_time, expected_arrival_time"),
    @Index(name = "idx_trip_route", columnList = "route_id"),
    @Index(name = "idx_trip_status", columnList = "status"),
    @Index(name = "idx_trip_departure", columnList = "departure_time")
})
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Bus assignment is required")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "bus_id", nullable = false, foreignKey = @ForeignKey(name = "fk_trips_bus"))
    private Bus bus;

    @NotNull(message = "Route assignment is required")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "route_id", nullable = false, foreignKey = @ForeignKey(name = "fk_trips_route"))
    private Route route;

    @NotNull(message = "Departure time is required")
    @Column(name = "departure_time", nullable = false)
    private LocalDateTime departureTime;

    @NotNull(message = "Expected arrival time is required")
    @Column(name = "expected_arrival_time", nullable = false)
    private LocalDateTime expectedArrivalTime;

    @Column(name = "actual_arrival_time")
    private LocalDateTime actualArrivalTime;

    @NotNull(message = "Trip status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private TripStatus status = TripStatus.SCHEDULED;

    @NotNull(message = "Passenger count cannot be null")
    @Min(value = 0, message = "Passenger count cannot be negative")
    @Column(name = "passenger_count", nullable = false)
    private Integer passengerCount = 0;

    @NotNull(message = "Ticket revenue cannot be null")
    @DecimalMin(value = "0.00", message = "Ticket revenue cannot be negative")
    @Column(name = "ticket_revenue", nullable = false, precision = 10, scale = 2)
    private BigDecimal ticketRevenue = BigDecimal.ZERO;

    @NotNull(message = "Additional expenses cannot be null")
    @DecimalMin(value = "0.00", message = "Additional expenses cannot be negative")
    @Column(name = "additional_expenses", nullable = false, precision = 10, scale = 2)
    private BigDecimal additionalExpenses = BigDecimal.ZERO;

    @Column(name = "overload_exception", nullable = false)
    private Boolean overloadException = false;

    @Column(name = "overload_reason", length = 255)
    private String overloadReason;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Trip() {
    }

    public Trip(Long id, Bus bus, Route route, LocalDateTime departureTime,
                LocalDateTime expectedArrivalTime, LocalDateTime actualArrivalTime,
                TripStatus status, Integer passengerCount, BigDecimal ticketRevenue,
                BigDecimal additionalExpenses, Boolean overloadException, String overloadReason, String notes) {
        this.id = id;
        this.bus = bus;
        this.route = route;
        this.departureTime = departureTime;
        this.expectedArrivalTime = expectedArrivalTime;
        this.actualArrivalTime = actualArrivalTime;
        this.status = status != null ? status : TripStatus.SCHEDULED;
        this.passengerCount = passengerCount != null ? passengerCount : 0;
        this.ticketRevenue = ticketRevenue != null ? ticketRevenue : BigDecimal.ZERO;
        this.additionalExpenses = additionalExpenses != null ? additionalExpenses : BigDecimal.ZERO;
        this.overloadException = overloadException != null ? overloadException : false;
        this.overloadReason = overloadReason;
        this.notes = notes;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = TripStatus.SCHEDULED;
        }
        if (this.ticketRevenue == null) {
            this.ticketRevenue = BigDecimal.ZERO;
        }
        if (this.additionalExpenses == null) {
            this.additionalExpenses = BigDecimal.ZERO;
        }
        if (this.passengerCount == null) {
            this.passengerCount = 0;
        }
        if (this.overloadException == null) {
            this.overloadException = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Bus getBus() {
        return bus;
    }

    public void setBus(Bus bus) {
        this.bus = bus;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
