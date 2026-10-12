package com.example.buswise.dto;

import com.example.buswise.entity.BusStatus;
import com.example.buswise.entity.FuelType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class BusRequestDTO {

    @NotBlank(message = "Registration number is required")
    private String registrationNumber;

    @NotBlank(message = "Bus model name is required")
    private String busModel;

    @NotNull(message = "Seating capacity is required")
    @Min(value = 1, message = "Seating capacity must be at least 1")
    @Max(value = 120, message = "Seating capacity cannot exceed 120")
    private Integer seatingCapacity;

    @NotNull(message = "Fuel type is required")
    private FuelType fuelType;

    @NotNull(message = "Odometer reading is required")
    @Min(value = 0, message = "Odometer reading cannot be negative")
    private BigDecimal odometerReading;

    private BusStatus status = BusStatus.AVAILABLE;

    private String notes;

    public BusRequestDTO() {
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
}
