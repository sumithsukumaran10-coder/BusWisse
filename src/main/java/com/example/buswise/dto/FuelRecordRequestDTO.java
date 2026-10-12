package com.example.buswise.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public class FuelRecordRequestDTO {

    @NotNull(message = "Bus ID is required")
    private Long busId;

    private Long tripId;

    @NotNull(message = "Record date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate recordDate;

    @NotNull(message = "Distance travelled is required")
    @DecimalMin(value = "0.0", message = "Distance cannot be negative")
    private BigDecimal distanceTravelledKm;

    @NotNull(message = "Fuel consumed in liters is required")
    @DecimalMin(value = "0.01", message = "Fuel consumed must be greater than zero")
    private BigDecimal fuelConsumedLiters;

    @NotNull(message = "Fuel price per liter is required")
    @DecimalMin(value = "0.00", message = "Fuel price cannot be negative")
    private BigDecimal fuelPricePerLiter;

    private String notes;

    public FuelRecordRequestDTO() {
    }

    public Long getBusId() {
        return busId;
    }

    public void setBusId(Long busId) {
        this.busId = busId;
    }

    public Long getTripId() {
        return tripId;
    }

    public void setTripId(Long tripId) {
        this.tripId = tripId;
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
    }

    public BigDecimal getFuelConsumedLiters() {
        return fuelConsumedLiters;
    }

    public void setFuelConsumedLiters(BigDecimal fuelConsumedLiters) {
        this.fuelConsumedLiters = fuelConsumedLiters;
    }

    public BigDecimal getFuelPricePerLiter() {
        return fuelPricePerLiter;
    }

    public void setFuelPricePerLiter(BigDecimal fuelPricePerLiter) {
        this.fuelPricePerLiter = fuelPricePerLiter;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
