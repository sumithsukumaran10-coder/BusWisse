package com.example.buswise.dto;

import java.util.List;
import java.util.Map;

public class PassengerAnalyticsDTO {
    private long totalPassengersCarried;
    private Double averageOccupancyRate;
    private long fullyBookedOrOverloadedTrips;
    private long underutilizedTrips; // < 40% occupancy

    private List<RouteDemandItem> routeDemandList;
    private List<String> operationalRecommendations;
    private Map<String, Double> occupancyByRouteMap;

    public static class RouteDemandItem {
        private Long routeId;
        private String routeNumber;
        private String routeName;
        private long totalPassengers;
        private Double averageOccupancyRate;
        private String demandCategory; // HIGH, OPTIMAL, LOW

        public RouteDemandItem() {}

        public RouteDemandItem(Long routeId, String routeNumber, String routeName,
                               long totalPassengers, Double averageOccupancyRate, String demandCategory) {
            this.routeId = routeId;
            this.routeNumber = routeNumber;
            this.routeName = routeName;
            this.totalPassengers = totalPassengers;
            this.averageOccupancyRate = averageOccupancyRate;
            this.demandCategory = demandCategory;
        }

        public Long getRouteId() { return routeId; }
        public void setRouteId(Long routeId) { this.routeId = routeId; }
        public String getRouteNumber() { return routeNumber; }
        public void setRouteNumber(String routeNumber) { this.routeNumber = routeNumber; }
        public String getRouteName() { return routeName; }
        public void setRouteName(String routeName) { this.routeName = routeName; }
        public long getTotalPassengers() { return totalPassengers; }
        public void setTotalPassengers(long totalPassengers) { this.totalPassengers = totalPassengers; }
        public Double getAverageOccupancyRate() { return averageOccupancyRate; }
        public void setAverageOccupancyRate(Double averageOccupancyRate) { this.averageOccupancyRate = averageOccupancyRate; }
        public String getDemandCategory() { return demandCategory; }
        public void setDemandCategory(String demandCategory) { this.demandCategory = demandCategory; }
    }

    public PassengerAnalyticsDTO() {}

    // Getters and Setters
    public long getTotalPassengersCarried() { return totalPassengersCarried; }
    public void setTotalPassengersCarried(long totalPassengersCarried) { this.totalPassengersCarried = totalPassengersCarried; }
    public Double getAverageOccupancyRate() { return averageOccupancyRate; }
    public void setAverageOccupancyRate(Double averageOccupancyRate) { this.averageOccupancyRate = averageOccupancyRate; }
    public long getFullyBookedOrOverloadedTrips() { return fullyBookedOrOverloadedTrips; }
    public void setFullyBookedOrOverloadedTrips(long fullyBookedOrOverloadedTrips) { this.fullyBookedOrOverloadedTrips = fullyBookedOrOverloadedTrips; }
    public long getUnderutilizedTrips() { return underutilizedTrips; }
    public void setUnderutilizedTrips(long underutilizedTrips) { this.underutilizedTrips = underutilizedTrips; }
    public List<RouteDemandItem> getRouteDemandList() { return routeDemandList; }
    public void setRouteDemandList(List<RouteDemandItem> routeDemandList) { this.routeDemandList = routeDemandList; }
    public List<String> getOperationalRecommendations() { return operationalRecommendations; }
    public void setOperationalRecommendations(List<String> operationalRecommendations) { this.operationalRecommendations = operationalRecommendations; }
    public Map<String, Double> getOccupancyByRouteMap() { return occupancyByRouteMap; }
    public void setOccupancyByRouteMap(Map<String, Double> occupancyByRouteMap) { this.occupancyByRouteMap = occupancyByRouteMap; }
}
