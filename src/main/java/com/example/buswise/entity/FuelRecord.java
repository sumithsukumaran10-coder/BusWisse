package com.example.buswise.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity representing vehicle fuel replenishment, distance travelled, and fuel efficiency.
 */
@Entity
@Table(name = "fuel_records", indexes = {
    @Index(name = "idx_fuel_bus", columnList = "bus_id"),
    @Index(name = "idx_fuel_date", columnList = "record_date")
})
public class FuelRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Bus assignment is required")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "bus_id", nullable = false, foreignKey = @ForeignKey(name = "fk_fuel_bus"))
    private Bus bus;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "trip_id", foreignKey = @ForeignKey(name = "fk_fuel_trip"))
    private Trip trip;

    @NotNull(message = "Record date is required")
    @Column(name = "record_date", nullable = false)
    private LocalDate recordDate;

    @NotNull(message = "Distance travelled is required")
    @DecimalMin(value = "0.0", message = "Distance cannot be negative")
    @Column(name = "distance_travelled_km", nullable = false, precision = 8, scale = 2)
    private BigDecimal distanceTravelledKm;

    @NotNull(message = "Fuel consumed is required")
    @DecimalMin(value = "0.01", message = "Fuel consumed must be greater than zero")
    @Column(name = "fuel_consumed_liters", nullable = false, precision = 8, scale = 2)
    private BigDecimal fuelConsumedLiters;

    @NotNull(message = "Fuel price per liter is required")
    @DecimalMin(value = "0.00", message = "Fuel price cannot be negative")
    @Column(name = "fuel_price_per_liter", nullable = false, precision = 8, scale = 2)
    private BigDecimal fuelPricePerLiter;

    @Column(name = "total_fuel_cost", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalFuelCost = BigDecimal.ZERO;

    @Column(name = "fuel_efficiency_km_per_liter", nullable = false, precision = 8, scale = 2)
    private BigDecimal fuelEfficiencyKmPerLiter = BigDecimal.ZERO;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public FuelRecord() {
    }

    public FuelRecord(Long id, Bus bus, Trip trip, LocalDate recordDate,
                      BigDecimal distanceTravelledKm, BigDecimal fuelConsumedLiters,
                      BigDecimal fuelPricePerLiter, String notes) {
        this.id = id;
        this.bus = bus;
        this.trip = trip;
        this.recordDate = recordDate;
        this.distanceTravelledKm = distanceTravelledKm;
        this.fuelConsumedLiters = fuelConsumedLiters;
        this.fuelPricePerLiter = fuelPricePerLiter;
        this.notes = notes;
        calculateDerivedFields();
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        calculateDerivedFields();
    }

    @PreUpdate
    protected void onUpdate() {
        calculateDerivedFields();
    }

    public void calculateDerivedFields() {
        if (fuelConsumedLiters != null && fuelPricePerLiter != null) {
            this.totalFuelCost = fuelConsumedLiters.multiply(fuelPricePerLiter).setScale(2, RoundingMode.HALF_UP);
        } else {
            this.totalFuelCost = BigDecimal.ZERO;
        }

        if (distanceTravelledKm != null && fuelConsumedLiters != null && fuelConsumedLiters.compareTo(BigDecimal.ZERO) > 0) {
            this.fuelEfficiencyKmPerLiter = distanceTravelledKm.divide(fuelConsumedLiters, 2, RoundingMode.HALF_UP);
        } else {
            this.fuelEfficiencyKmPerLiter = BigDecimal.ZERO;
        }
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

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public LocalDate getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(LocalDate recordDate) {
        this.recordDate = recordDate;
    }

    public BigDecimal getDistanceTravelledKm() {
        return distanceTravelledKm;
    }

    public void setDistanceTravelledKm(BigDecimal distanceTravelledKm) {
        this.distanceTravelledKm = distanceTravelledKm;
        calculateDerivedFields();
    }

    public BigDecimal getFuelConsumedLiters() {
        return fuelConsumedLiters;
    }

    public void setFuelConsumedLiters(BigDecimal fuelConsumedLiters) {
        this.fuelConsumedLiters = fuelConsumedLiters;
        calculateDerivedFields();
    }

    public BigDecimal getFuelPricePerLiter() {
        return fuelPricePerLiter;
    }

    public void setFuelPricePerLiter(BigDecimal fuelPricePerLiter) {
        this.fuelPricePerLiter = fuelPricePerLiter;
        calculateDerivedFields();
    }

    public BigDecimal getTotalFuelCost() {
        return totalFuelCost;
    }

    public void setTotalFuelCost(BigDecimal totalFuelCost) {
        this.totalFuelCost = totalFuelCost;
    }

    public BigDecimal getFuelEfficiencyKmPerLiter() {
        return fuelEfficiencyKmPerLiter;
    }

    public void setFuelEfficiencyKmPerLiter(BigDecimal fuelEfficiencyKmPerLiter) {
        this.fuelEfficiencyKmPerLiter = fuelEfficiencyKmPerLiter;
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
}
