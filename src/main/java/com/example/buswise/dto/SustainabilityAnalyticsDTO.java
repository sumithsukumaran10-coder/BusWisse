package com.example.buswise.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class SustainabilityAnalyticsDTO {
    private BigDecimal estimatedTotalCo2EmissionsKg;
    private BigDecimal estimatedTotalCo2EmissionsTonnes;
    private Double emissionFactorUsedKgPerLiter;
    private BigDecimal estimatedCo2AvoidedKgByEv;
    private Double averageCo2GramsPerPassengerKm;
    private String methodologyNote;

    private List<BusEmissionItem> busEmissionsList;
    private Map<String, BigDecimal> emissionsByRouteMap;
    private List<String> sustainabilityRecommendations;

    public static class BusEmissionItem {
        private Long busId;
        private String registrationNumber;
        private String busModel;
        private String fuelType;
        private BigDecimal fuelConsumedLiters;
        private BigDecimal estimatedCo2Kg;

        public BusEmissionItem() {}

        public Long getBusId() { return busId; }
        public void setBusId(Long busId) { this.busId = busId; }
        public String getRegistrationNumber() { return registrationNumber; }
        public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }
        public String getBusModel() { return busModel; }
        public void setBusModel(String busModel) { this.busModel = busModel; }
        public String getFuelType() { return fuelType; }
        public void setFuelType(String fuelType) { this.fuelType = fuelType; }
        public BigDecimal getFuelConsumedLiters() { return fuelConsumedLiters; }
        public void setFuelConsumedLiters(BigDecimal fuelConsumedLiters) { this.fuelConsumedLiters = fuelConsumedLiters; }
        public BigDecimal getEstimatedCo2Kg() { return estimatedCo2Kg; }
        public void setEstimatedCo2Kg(BigDecimal estimatedCo2Kg) { this.estimatedCo2Kg = estimatedCo2Kg; }
    }

    public SustainabilityAnalyticsDTO() {}

    // Getters and Setters
    public BigDecimal getEstimatedTotalCo2EmissionsKg() { return estimatedTotalCo2EmissionsKg; }
    public void setEstimatedTotalCo2EmissionsKg(BigDecimal estimatedTotalCo2EmissionsKg) { this.estimatedTotalCo2EmissionsKg = estimatedTotalCo2EmissionsKg; }
    public BigDecimal getEstimatedTotalCo2EmissionsTonnes() { return estimatedTotalCo2EmissionsTonnes; }
    public void setEstimatedTotalCo2EmissionsTonnes(BigDecimal estimatedTotalCo2EmissionsTonnes) { this.estimatedTotalCo2EmissionsTonnes = estimatedTotalCo2EmissionsTonnes; }
    public Double getEmissionFactorUsedKgPerLiter() { return emissionFactorUsedKgPerLiter; }
    public void setEmissionFactorUsedKgPerLiter(Double emissionFactorUsedKgPerLiter) { this.emissionFactorUsedKgPerLiter = emissionFactorUsedKgPerLiter; }
    public BigDecimal getEstimatedCo2AvoidedKgByEv() { return estimatedCo2AvoidedKgByEv; }
    public void setEstimatedCo2AvoidedKgByEv(BigDecimal estimatedCo2AvoidedKgByEv) { this.estimatedCo2AvoidedKgByEv = estimatedCo2AvoidedKgByEv; }
    public Double getAverageCo2GramsPerPassengerKm() { return averageCo2GramsPerPassengerKm; }
    public void setAverageCo2GramsPerPassengerKm(Double averageCo2GramsPerPassengerKm) { this.averageCo2GramsPerPassengerKm = averageCo2GramsPerPassengerKm; }
    public String getMethodologyNote() { return methodologyNote; }
    public void setMethodologyNote(String methodologyNote) { this.methodologyNote = methodologyNote; }
    public List<BusEmissionItem> getBusEmissionsList() { return busEmissionsList; }
    public void setBusEmissionsList(List<BusEmissionItem> busEmissionsList) { this.busEmissionsList = busEmissionsList; }
    public Map<String, BigDecimal> getEmissionsByRouteMap() { return emissionsByRouteMap; }
    public void setEmissionsByRouteMap(Map<String, BigDecimal> emissionsByRouteMap) { this.emissionsByRouteMap = emissionsByRouteMap; }
    public List<String> getSustainabilityRecommendations() { return sustainabilityRecommendations; }
    public void setSustainabilityRecommendations(List<String> sustainabilityRecommendations) { this.sustainabilityRecommendations = sustainabilityRecommendations; }
}
