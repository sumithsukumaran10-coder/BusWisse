package com.example.buswise.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class DashboardSummaryDTO {
    // Fleet Status Metrics
    private long totalBuses;
    private long availableBuses;
    private long onTripBuses;
    private long maintenanceBuses;

    // Route & Trip Metrics
    private long totalRoutes;
    private long totalTrips;
    private long scheduledTrips;
    private long inProgressTrips;
    private long completedTrips;
    private long cancelledTrips;

    // Operational Volume Metrics
    private long totalPassengersCarried;
    private BigDecimal totalFuelConsumedLiters;
    private Double averageFuelEfficiency;
    private Double averageOccupancyRate;

    // Financial Performance Metrics
    private BigDecimal totalRevenue;
    private BigDecimal totalFuelCost;
    private BigDecimal totalOtherExpenses;
    private BigDecimal totalOperatingCost;
    private BigDecimal estimatedOperatingProfit;
    private Double profitMarginPercentage;

    // Alerts
    private long activeAlertsCount;

    // Visual Charts Payload
    private Map<String, Long> busStatusDistribution;
    private Map<String, Long> routePassengerDemand;
    private Map<String, BigDecimal> monthlyRevenue;
    private Map<String, BigDecimal> monthlyExpenses;
    private List<TripResponseDTO> recentTrips;
    private List<OperationalAlertDTO> recentAlerts;

    public DashboardSummaryDTO() {
    }

    // Getters and Setters
    public long getTotalBuses() {
        return totalBuses;
    }

    public void setTotalBuses(long totalBuses) {
        this.totalBuses = totalBuses;
    }

    public long getAvailableBuses() {
        return availableBuses;
    }

    public void setAvailableBuses(long availableBuses) {
        this.availableBuses = availableBuses;
    }

    public long getOnTripBuses() {
        return onTripBuses;
    }

    public void setOnTripBuses(long onTripBuses) {
        this.onTripBuses = onTripBuses;
    }

    public long getMaintenanceBuses() {
        return maintenanceBuses;
    }

    public void setMaintenanceBuses(long maintenanceBuses) {
        this.maintenanceBuses = maintenanceBuses;
    }

    public long getTotalRoutes() {
        return totalRoutes;
    }

    public void setTotalRoutes(long totalRoutes) {
        this.totalRoutes = totalRoutes;
    }

    public long getTotalTrips() {
        return totalTrips;
    }

    public void setTotalTrips(long totalTrips) {
        this.totalTrips = totalTrips;
    }

    public long getScheduledTrips() {
        return scheduledTrips;
    }

    public void setScheduledTrips(long scheduledTrips) {
        this.scheduledTrips = scheduledTrips;
    }

    public long getInProgressTrips() {
        return inProgressTrips;
    }

    public void setInProgressTrips(long inProgressTrips) {
        this.inProgressTrips = inProgressTrips;
    }

    public long getCompletedTrips() {
        return completedTrips;
    }

    public void setCompletedTrips(long completedTrips) {
        this.completedTrips = completedTrips;
    }

    public long getCancelledTrips() {
        return cancelledTrips;
    }

    public void setCancelledTrips(long cancelledTrips) {
        this.cancelledTrips = cancelledTrips;
    }

    public long getTotalPassengersCarried() {
        return totalPassengersCarried;
    }

    public void setTotalPassengersCarried(long totalPassengersCarried) {
        this.totalPassengersCarried = totalPassengersCarried;
    }

    public BigDecimal getTotalFuelConsumedLiters() {
        return totalFuelConsumedLiters;
    }

    public void setTotalFuelConsumedLiters(BigDecimal totalFuelConsumedLiters) {
        this.totalFuelConsumedLiters = totalFuelConsumedLiters;
    }

    public Double getAverageFuelEfficiency() {
        return averageFuelEfficiency;
    }

    public void setAverageFuelEfficiency(Double averageFuelEfficiency) {
        this.averageFuelEfficiency = averageFuelEfficiency;
    }

    public Double getAverageOccupancyRate() {
        return averageOccupancyRate;
    }

    public void setAverageOccupancyRate(Double averageOccupancyRate) {
        this.averageOccupancyRate = averageOccupancyRate;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public BigDecimal getTotalFuelCost() {
        return totalFuelCost;
    }

    public void setTotalFuelCost(BigDecimal totalFuelCost) {
        this.totalFuelCost = totalFuelCost;
    }

    public BigDecimal getTotalOtherExpenses() {
        return totalOtherExpenses;
    }

    public void setTotalOtherExpenses(BigDecimal totalOtherExpenses) {
        this.totalOtherExpenses = totalOtherExpenses;
    }

    public BigDecimal getTotalOperatingCost() {
        return totalOperatingCost;
    }

    public void setTotalOperatingCost(BigDecimal totalOperatingCost) {
        this.totalOperatingCost = totalOperatingCost;
    }

    public BigDecimal getEstimatedOperatingProfit() {
        return estimatedOperatingProfit;
    }

    public void setEstimatedOperatingProfit(BigDecimal estimatedOperatingProfit) {
        this.estimatedOperatingProfit = estimatedOperatingProfit;
    }

    public Double getProfitMarginPercentage() {
        return profitMarginPercentage;
    }

    public void setProfitMarginPercentage(Double profitMarginPercentage) {
        this.profitMarginPercentage = profitMarginPercentage;
    }

    public long getActiveAlertsCount() {
        return activeAlertsCount;
    }

    public void setActiveAlertsCount(long activeAlertsCount) {
        this.activeAlertsCount = activeAlertsCount;
    }

    public Map<String, Long> getBusStatusDistribution() {
        return busStatusDistribution;
    }

    public void setBusStatusDistribution(Map<String, Long> busStatusDistribution) {
        this.busStatusDistribution = busStatusDistribution;
    }

    public Map<String, Long> getRoutePassengerDemand() {
        return routePassengerDemand;
    }

    public void setRoutePassengerDemand(Map<String, Long> routePassengerDemand) {
        this.routePassengerDemand = routePassengerDemand;
    }

    public Map<String, BigDecimal> getMonthlyRevenue() {
        return monthlyRevenue;
    }

    public void setMonthlyRevenue(Map<String, BigDecimal> monthlyRevenue) {
        this.monthlyRevenue = monthlyRevenue;
    }

    public Map<String, BigDecimal> getMonthlyExpenses() {
        return monthlyExpenses;
    }

    public void setMonthlyExpenses(Map<String, BigDecimal> monthlyExpenses) {
        this.monthlyExpenses = monthlyExpenses;
    }

    public List<TripResponseDTO> getRecentTrips() {
        return recentTrips;
    }

    public void setRecentTrips(List<TripResponseDTO> recentTrips) {
        this.recentTrips = recentTrips;
    }

    public List<OperationalAlertDTO> getRecentAlerts() {
        return recentAlerts;
    }

    public void setRecentAlerts(List<OperationalAlertDTO> recentAlerts) {
        this.recentAlerts = recentAlerts;
    }
}
