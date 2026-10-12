package com.example.buswise.dto;

import com.example.buswise.entity.Bus;
import com.example.buswise.entity.BusStatus;
import com.example.buswise.entity.FuelType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BusResponseDTO {
    private Long id;
    private String registrationNumber;
    private String busModel;
    private Integer seatingCapacity;
    private FuelType fuelType;
    private BigDecimal odometerReading;
    private BusStatus status;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public BusResponseDTO() {
    }

    public static BusResponseDTO fromEntity(Bus bus) {
        if (bus == null) return null;
        BusResponseDTO dto = new BusResponseDTO();
        dto.setId(bus.getId());
        dto.setRegistrationNumber(bus.getRegistrationNumber());
        dto.setBusModel(bus.getBusModel());
        dto.setSeatingCapacity(bus.getSeatingCapacity());
        dto.setFuelType(bus.getFuelType());
        dto.setOdometerReading(bus.getOdometerReading());
        dto.setStatus(bus.getStatus());
        dto.setNotes(bus.getNotes());
        dto.setCreatedAt(bus.getCreatedAt());
        dto.setUpdatedAt(bus.getUpdatedAt());
        return dto;
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
