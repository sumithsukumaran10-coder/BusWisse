package com.example.buswise.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing a physical passenger bus in the transport fleet.
 */
@Entity
@Table(name = "buses", indexes = {
    @Index(name = "idx_bus_registration", columnList = "registration_number", unique = true),
    @Index(name = "idx_bus_status", columnList = "status")
})
public class Bus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Registration number is required")
    @Column(name = "registration_number", nullable = false, unique = true, length = 30)
    private String registrationNumber;

    @NotBlank(message = "Bus model name is required")
    @Column(name = "bus_model", nullable = false, length = 100)
    private String busModel;

    @NotNull(message = "Seating capacity is required")
    @Min(value = 1, message = "Seating capacity must be at least 1")
    @Column(name = "seating_capacity", nullable = false)
    private Integer seatingCapacity;

    @NotNull(message = "Fuel type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "fuel_type", nullable = false, length = 20)
    private FuelType fuelType;

    @NotNull(message = "Odometer reading is required")
    @Min(value = 0, message = "Odometer reading cannot be negative")
    @Column(name = "odometer_reading", nullable = false, precision = 10, scale = 2)
    private BigDecimal odometerReading = BigDecimal.ZERO;

    @NotNull(message = "Bus status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private BusStatus status = BusStatus.AVAILABLE;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Bus() {
    }

    public Bus(Long id, String registrationNumber, String busModel, Integer seatingCapacity,
               FuelType fuelType, BigDecimal odometerReading, BusStatus status, String notes) {
        this.id = id;
        this.registrationNumber = registrationNumber;
        this.busModel = busModel;
        this.seatingCapacity = seatingCapacity;
        this.fuelType = fuelType;
        this.odometerReading = odometerReading != null ? odometerReading : BigDecimal.ZERO;
        this.status = status != null ? status : BusStatus.AVAILABLE;
        this.notes = notes;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = BusStatus.AVAILABLE;
        }
        if (this.odometerReading == null) {
            this.odometerReading = BigDecimal.ZERO;
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

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getBusModel() {
        return busModel;
    }

    public void setBusModel(String busModel) {
        this.busModel = busModel;
    }

    public Integer getSeatingCapacity() {
        return seatingCapacity;
    }

    public void setSeatingCapacity(Integer seatingCapacity) {
        this.seatingCapacity = seatingCapacity;
    }

    public FuelType getFuelType() {
        return fuelType;
    }

    public void setFuelType(FuelType fuelType) {
        this.fuelType = fuelType;
    }

    public BigDecimal getOdometerReading() {
        return odometerReading;
    }

    public void setOdometerReading(BigDecimal odometerReading) {
        this.odometerReading = odometerReading;
    }

    public BusStatus getStatus() {
        return status;
    }

    public void setStatus(BusStatus status) {
        this.status = status;
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
