package com.example.buswise.dto;

import com.example.buswise.entity.Trip;
import com.example.buswise.entity.TripStatus;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

public class TripResponseDTO {
    private Long id;
    private Long busId;
    private String busRegistrationNumber;
    private String busModel;
    private Integer busCapacity;
    private Long routeId;
    private String routeNumber;
    private String routeName;
    private String sourceLocation;
    private String destinationLocation;
    private BigDecimal distanceKm;
    private LocalDateTime departureTime;
    private LocalDateTime expectedArrivalTime;
    private LocalDateTime actualArrivalTime;
    private TripStatus status;
    private Integer passengerCount;
    private BigDecimal ticketRevenue;
    private BigDecimal additionalExpenses;
    private Boolean overloadException;
    private String overloadReason;
    private String notes;
    private Double occupancyRate;
    private BigDecimal estimatedProfit;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TripResponseDTO() {
    }

    public static TripResponseDTO fromEntity(Trip trip) {
        if (trip == null) return null;
        TripResponseDTO dto = new TripResponseDTO();
        dto.setId(trip.getId());
        if (trip.getBus() != null) {
            dto.setBusId(trip.getBus().getId());
            dto.setBusRegistrationNumber(trip.getBus().getRegistrationNumber());
            dto.setBusModel(trip.getBus().getBusModel());
            dto.setBusCapacity(trip.getBus().getSeatingCapacity());
        }
        if (trip.getRoute() != null) {
            dto.setRouteId(trip.getRoute().getId());
            dto.setRouteNumber(trip.getRoute().getRouteNumber());
            dto.setRouteName(trip.getRoute().getRouteName());
            dto.setSourceLocation(trip.getRoute().getSourceLocation());
            dto.setDestinationLocation(trip.getRoute().getDestinationLocation());
            dto.setDistanceKm(trip.getRoute().getDistanceKm());
        }
        dto.setDepartureTime(trip.getDepartureTime());
        dto.setExpectedArrivalTime(trip.getExpectedArrivalTime());
        dto.setActualArrivalTime(trip.getActualArrivalTime());
        dto.setStatus(trip.getStatus());
        dto.setPassengerCount(trip.getPassengerCount());
        dto.setTicketRevenue(trip.getTicketRevenue());
        dto.setAdditionalExpenses(trip.getAdditionalExpenses());
        dto.setOverloadException(trip.getOverloadException());
        dto.setOverloadReason(trip.getOverloadReason());
        dto.setNotes(trip.getNotes());
        dto.setCreatedAt(trip.getCreatedAt());
        dto.setUpdatedAt(trip.getUpdatedAt());

        // Occupancy calculation
        if (trip.getBus() != null && trip.getBus().getSeatingCapacity() != null && trip.getBus().getSeatingCapacity() > 0) {
            double occ = ((double) trip.getPassengerCount() / trip.getBus().getSeatingCapacity()) * 100.0;
            dto.setOccupancyRate(BigDecimal.valueOf(occ).setScale(1, RoundingMode.HALF_UP).doubleValue());
        } else {
            dto.setOccupancyRate(0.0);
        }

        // Basic trip-level direct margin (ticket revenue - additional expenses)
        BigDecimal rev = trip.getTicketRevenue() != null ? trip.getTicketRevenue() : BigDecimal.ZERO;
        BigDecimal exp = trip.getAdditionalExpenses() != null ? trip.getAdditionalExpenses() : BigDecimal.ZERO;
        dto.setEstimatedProfit(rev.subtract(exp));

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

    public Integer getBusCapacity() {
        return busCapacity;
    }

    public void setBusCapacity(Integer busCapacity) {
        this.busCapacity = busCapacity;
    }

    public Long getRouteId() {
        return routeId;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }

    public String getRouteNumber() {
        return routeNumber;
    }

    public void setRouteNumber(String routeNumber) {
        this.routeNumber = routeNumber;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getSourceLocation() {
        return sourceLocation;
    }

    public void setSourceLocation(String sourceLocation) {
        this.sourceLocation = sourceLocation;
    }

    public String getDestinationLocation() {
        return destinationLocation;
    }

    public void setDestinationLocation(String destinationLocation) {
        this.destinationLocation = destinationLocation;
    }

    public BigDecimal getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(BigDecimal distanceKm) {
        this.distanceKm = distanceKm;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
    }

    public LocalDateTime getExpectedArrivalTime() {
        return expectedArrivalTime;
    }

    public void setExpectedArrivalTime(LocalDateTime expectedArrivalTime) {
        this.expectedArrivalTime = expectedArrivalTime;
    }

    public LocalDateTime getActualArrivalTime() {
        return actualArrivalTime;
    }

    public void setActualArrivalTime(LocalDateTime actualArrivalTime) {
        this.actualArrivalTime = actualArrivalTime;
    }

    public TripStatus getStatus() {
        return status;
    }

    public void setStatus(TripStatus status) {
        this.status = status;
    }

    public Integer getPassengerCount() {
        return passengerCount;
    }

    public void setPassengerCount(Integer passengerCount) {
        this.passengerCount = passengerCount;
    }

    public BigDecimal getTicketRevenue() {
        return ticketRevenue;
    }

    public void setTicketRevenue(BigDecimal ticketRevenue) {
        this.ticketRevenue = ticketRevenue;
    }

    public BigDecimal getAdditionalExpenses() {
        return additionalExpenses;
    }

    public void setAdditionalExpenses(BigDecimal additionalExpenses) {
        this.additionalExpenses = additionalExpenses;
    }

    public Boolean getOverloadException() {
        return overloadException;
    }

    public void setOverloadException(Boolean overloadException) {
        this.overloadException = overloadException;
    }

    public String getOverloadReason() {
        return overloadReason;
    }

    public void setOverloadReason(String overloadReason) {
        this.overloadReason = overloadReason;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Double getOccupancyRate() {
        return occupancyRate;
    }

    public void setOccupancyRate(Double occupancyRate) {
        this.occupancyRate = occupancyRate;
    }

    public BigDecimal getEstimatedProfit() {
        return estimatedProfit;
    }

    public void setEstimatedProfit(BigDecimal estimatedProfit) {
        this.estimatedProfit = estimatedProfit;
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
