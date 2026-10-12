package com.example.buswise.dto;

import com.example.buswise.entity.Route;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RouteResponseDTO {
    private Long id;
    private String routeNumber;
    private String routeName;
    private String sourceLocation;
    private String destinationLocation;
    private BigDecimal distanceKm;
    private Integer estimatedDurationMinutes;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long totalTripsCount;

    public RouteResponseDTO() {
    }

    public static RouteResponseDTO fromEntity(Route route) {
        if (route == null) return null;
        RouteResponseDTO dto = new RouteResponseDTO();
        dto.setId(route.getId());
        dto.setRouteNumber(route.getRouteNumber());
        dto.setRouteName(route.getRouteName());
        dto.setSourceLocation(route.getSourceLocation());
        dto.setDestinationLocation(route.getDestinationLocation());
        dto.setDistanceKm(route.getDistanceKm());
        dto.setEstimatedDurationMinutes(route.getEstimatedDurationMinutes());
        dto.setDescription(route.getDescription());
        dto.setCreatedAt(route.getCreatedAt());
        dto.setUpdatedAt(route.getUpdatedAt());
        return dto;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Integer getEstimatedDurationMinutes() {
        return estimatedDurationMinutes;
    }

    public void setEstimatedDurationMinutes(Integer estimatedDurationMinutes) {
        this.estimatedDurationMinutes = estimatedDurationMinutes;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Long getTotalTripsCount() {
        return totalTripsCount;
    }

    public void setTotalTripsCount(Long totalTripsCount) {
        this.totalTripsCount = totalTripsCount;
    }
}
