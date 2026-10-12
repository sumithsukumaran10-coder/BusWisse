package com.example.buswise.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class FuelAnalyticsDTO {
    private BigDecimal totalFuelConsumedLiters;
    private BigDecimal totalFuelCost;
    private Double fleetAverageEfficiency;
    private Double efficiencyThresholdBaseline; // configurable baseline (e.g. 4.0 km/L)
    private long inefficientBusesCount;

    private List<BusEfficiencyItem> busEfficiencyList;
    private Map<String, BigDecimal> fuelByFuelTypeMap;
    private Map<String, Double> monthlyEfficiencyTrend;

    public static class BusEfficiencyItem {
        private Long busId;
        private String registrationNumber;
        private String busModel;
        private String fuelType;
        private BigDecimal totalDistanceKm;
        private BigDecimal totalLitersConsumed;
        private BigDecimal totalCost;
        private Double averageEfficiencyKmPerLiter;
        private boolean flaggedInefficient;

        public BusEfficiencyItem() {}

        public Long getBusId() { return busId; }
        public void setBusId(Long busId) { this.busId = busId; }
        public String getRegistrationNumber() { return registrationNumber; }
        public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }
        public String getBusModel() { return busModel; }
        public void setBusModel(String busModel) { this.busModel = busModel; }
        public String getFuelType() { return fuelType; }
        public void setFuelType(String fuelType) { this.fuelType = fuelType; }
        public BigDecimal getTotalDistanceKm() { return totalDistanceKm; }
        public void setTotalDistanceKm(BigDecimal totalDistanceKm) { this.totalDistanceKm = totalDistanceKm; }
        public BigDecimal getTotalLitersConsumed() { return totalLitersConsumed; }
        public void setTotalLitersConsumed(BigDecimal totalLitersConsumed) { this.totalLitersConsumed = totalLitersConsumed; }
        public BigDecimal getTotalCost() { return totalCost; }
        public void setTotalCost(BigDecimal totalCost) { this.totalCost = totalCost; }
        public Double getAverageEfficiencyKmPerLiter() { return averageEfficiencyKmPerLiter; }
        public void setAverageEfficiencyKmPerLiter(Double averageEfficiencyKmPerLiter) { this.averageEfficiencyKmPerLiter = averageEfficiencyKmPerLiter; }
        public boolean isFlaggedInefficient() { return flaggedInefficient; }
        public void setFlaggedInefficient(boolean flaggedInefficient) { this.flaggedInefficient = flaggedInefficient; }
    }

    public FuelAnalyticsDTO() {}

    // Getters and Setters
    public BigDecimal getTotalFuelConsumedLiters() { return totalFuelConsumedLiters; }
    public void setTotalFuelConsumedLiters(BigDecimal totalFuelConsumedLiters) { this.totalFuelConsumedLiters = totalFuelConsumedLiters; }
    public BigDecimal getTotalFuelCost() { return totalFuelCost; }
    public void setTotalFuelCost(BigDecimal totalFuelCost) { this.totalFuelCost = totalFuelCost; }
    public Double getFleetAverageEfficiency() { return fleetAverageEfficiency; }
    public void setFleetAverageEfficiency(Double fleetAverageEfficiency) { this.fleetAverageEfficiency = fleetAverageEfficiency; }
    public Double getEfficiencyThresholdBaseline() { return efficiencyThresholdBaseline; }
    public void setEfficiencyThresholdBaseline(Double efficiencyThresholdBaseline) { this.efficiencyThresholdBaseline = efficiencyThresholdBaseline; }
    public long getInefficientBusesCount() { return inefficientBusesCount; }
    public void setInefficientBusesCount(long inefficientBusesCount) { this.inefficientBusesCount = inefficientBusesCount; }
    public List<BusEfficiencyItem> getBusEfficiencyList() { return busEfficiencyList; }
    public void setBusEfficiencyList(List<BusEfficiencyItem> busEfficiencyList) { this.busEfficiencyList = busEfficiencyList; }
    public Map<String, BigDecimal> getFuelByFuelTypeMap() { return fuelByFuelTypeMap; }
    public void setFuelByFuelTypeMap(Map<String, BigDecimal> fuelByFuelTypeMap) { this.fuelByFuelTypeMap = fuelByFuelTypeMap; }
    public Map<String, Double> getMonthlyEfficiencyTrend() { return monthlyEfficiencyTrend; }
    public void setMonthlyEfficiencyTrend(Map<String, Double> monthlyEfficiencyTrend) { this.monthlyEfficiencyTrend = monthlyEfficiencyTrend; }
}
