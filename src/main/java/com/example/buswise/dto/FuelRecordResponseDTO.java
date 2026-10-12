package com.example.buswise.dto;

import com.example.buswise.entity.FuelRecord;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class FuelRecordResponseDTO {
    private Long id;
    private Long busId;
    private String busRegistrationNumber;
    private String busModel;
    private Long tripId;
    private String tripRouteName;
    private LocalDate recordDate;
    private BigDecimal distanceTravelledKm;
    private BigDecimal fuelConsumedLiters;
    private BigDecimal fuelPricePerLiter;
    private BigDecimal totalFuelCost;
    private BigDecimal fuelEfficiencyKmPerLiter;
    private String notes;
    private LocalDateTime createdAt;

    public FuelRecordResponseDTO() {
    }

    public static FuelRecordResponseDTO fromEntity(FuelRecord record) {
        if (record == null) return null;
        FuelRecordResponseDTO dto = new FuelRecordResponseDTO();
        dto.setId(record.getId());
        if (record.getBus() != null) {
            dto.setBusId(record.getBus().getId());
            dto.setBusRegistrationNumber(record.getBus().getRegistrationNumber());
            dto.setBusModel(record.getBus().getBusModel());
        }
        if (record.getTrip() != null) {
            dto.setTripId(record.getTrip().getId());
            if (record.getTrip().getRoute() != null) {
                dto.setTripRouteName(record.getTrip().getRoute().getRouteName());
            }
        }
        dto.setRecordDate(record.getRecordDate());
        dto.setDistanceTravelledKm(record.getDistanceTravelledKm());
        dto.setFuelConsumedLiters(record.getFuelConsumedLiters());
        dto.setFuelPricePerLiter(record.getFuelPricePerLiter());
        dto.setTotalFuelCost(record.getTotalFuelCost());
        dto.setFuelEfficiencyKmPerLiter(record.getFuelEfficiencyKmPerLiter());
        dto.setNotes(record.getNotes());
        dto.setCreatedAt(record.getCreatedAt());
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

    public String getBusModel() {
        return busModel;
    }

    public void setBusModel(String busModel) {
        this.busModel = busModel;
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
