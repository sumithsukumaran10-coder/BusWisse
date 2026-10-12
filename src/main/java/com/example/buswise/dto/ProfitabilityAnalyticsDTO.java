package com.example.buswise.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class ProfitabilityAnalyticsDTO {
    private BigDecimal totalRevenue;
    private BigDecimal totalFuelCost;
    private BigDecimal totalOtherExpenses;
    private BigDecimal totalOperatingCost;
    private BigDecimal netOperatingProfit;
    private Double profitMarginPercentage;

    private Map<String, BigDecimal> expensesByCategoryMap;
    private List<RouteProfitabilityItem> routeProfitabilityList;
    private List<TripFinancialSummary> topProfitableTrips;
    private List<TripFinancialSummary> lossMakingTrips;

    public static class RouteProfitabilityItem {
        private Long routeId;
        private String routeNumber;
        private String routeName;
        private long totalTrips;
        private BigDecimal totalRevenue;
        private BigDecimal totalOperatingCost;
        private BigDecimal netProfit;
        private Double profitMarginPercentage;

        public RouteProfitabilityItem() {}

        public Long getRouteId() { return routeId; }
        public void setRouteId(Long routeId) { this.routeId = routeId; }
        public String getRouteNumber() { return routeNumber; }
        public void setRouteNumber(String routeNumber) { this.routeNumber = routeNumber; }
        public String getRouteName() { return routeName; }
        public void setRouteName(String routeName) { this.routeName = routeName; }
        public long getTotalTrips() { return totalTrips; }
        public void setTotalTrips(long totalTrips) { this.totalTrips = totalTrips; }
        public BigDecimal getTotalRevenue() { return totalRevenue; }
        public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }
        public BigDecimal getTotalOperatingCost() { return totalOperatingCost; }
        public void setTotalOperatingCost(BigDecimal totalOperatingCost) { this.totalOperatingCost = totalOperatingCost; }
        public BigDecimal getNetProfit() { return netProfit; }
        public void setNetProfit(BigDecimal netProfit) { this.netProfit = netProfit; }
        public Double getProfitMarginPercentage() { return profitMarginPercentage; }
        public void setProfitMarginPercentage(Double profitMarginPercentage) { this.profitMarginPercentage = profitMarginPercentage; }
    }

    public static class TripFinancialSummary {
        private Long tripId;
        private String routeName;
        private String busRegistration;
        private BigDecimal revenue;
        private BigDecimal cost;
        private BigDecimal profit;

        public TripFinancialSummary() {}

        public TripFinancialSummary(Long tripId, String routeName, String busRegistration,
                                    BigDecimal revenue, BigDecimal cost, BigDecimal profit) {
            this.tripId = tripId;
            this.routeName = routeName;
            this.busRegistration = busRegistration;
            this.revenue = revenue;
            this.cost = cost;
            this.profit = profit;
        }

        public Long getTripId() { return tripId; }
        public void setTripId(Long tripId) { this.tripId = tripId; }
        public String getRouteName() { return routeName; }
        public void setRouteName(String routeName) { this.routeName = routeName; }
        public String getBusRegistration() { return busRegistration; }
        public void setBusRegistration(String busRegistration) { this.busRegistration = busRegistration; }
        public BigDecimal getRevenue() { return revenue; }
        public void setRevenue(BigDecimal revenue) { this.revenue = revenue; }
        public BigDecimal getCost() { return cost; }
        public void setCost(BigDecimal cost) { this.cost = cost; }
        public BigDecimal getProfit() { return profit; }
        public void setProfit(BigDecimal profit) { this.profit = profit; }
    }

    public ProfitabilityAnalyticsDTO() {}

    // Getters and Setters
    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }
    public BigDecimal getTotalFuelCost() { return totalFuelCost; }
    public void setTotalFuelCost(BigDecimal totalFuelCost) { this.totalFuelCost = totalFuelCost; }
    public BigDecimal getTotalOtherExpenses() { return totalOtherExpenses; }
    public void setTotalOtherExpenses(BigDecimal totalOtherExpenses) { this.totalOtherExpenses = totalOtherExpenses; }
    public BigDecimal getTotalOperatingCost() { return totalOperatingCost; }
    public void setTotalOperatingCost(BigDecimal totalOperatingCost) { this.totalOperatingCost = totalOperatingCost; }
    public BigDecimal getNetOperatingProfit() { return netOperatingProfit; }
    public void setNetOperatingProfit(BigDecimal netOperatingProfit) { this.netOperatingProfit = netOperatingProfit; }
    public Double getProfitMarginPercentage() { return profitMarginPercentage; }
    public void setProfitMarginPercentage(Double profitMarginPercentage) { this.profitMarginPercentage = profitMarginPercentage; }
    public Map<String, BigDecimal> getExpensesByCategoryMap() { return expensesByCategoryMap; }
    public void setExpensesByCategoryMap(Map<String, BigDecimal> expensesByCategoryMap) { this.expensesByCategoryMap = expensesByCategoryMap; }
    public List<RouteProfitabilityItem> getRouteProfitabilityList() { return routeProfitabilityList; }
    public void setRouteProfitabilityList(List<RouteProfitabilityItem> routeProfitabilityList) { this.routeProfitabilityList = routeProfitabilityList; }
    public List<TripFinancialSummary> getTopProfitableTrips() { return topProfitableTrips; }
    public void setTopProfitableTrips(List<TripFinancialSummary> topProfitableTrips) { this.topProfitableTrips = topProfitableTrips; }
    public List<TripFinancialSummary> getLossMakingTrips() { return lossMakingTrips; }
    public void setLossMakingTrips(List<TripFinancialSummary> lossMakingTrips) { this.lossMakingTrips = lossMakingTrips; }
}
