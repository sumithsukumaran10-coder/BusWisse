package com.example.buswise.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing a transit route corridor between two terminals.
 */
@Entity
@Table(name = "routes", indexes = {
    @Index(name = "idx_route_number", columnList = "route_number", unique = true),
    @Index(name = "idx_route_locations", columnList = "source_location, destination_location")
})
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Route number/code is required")
    @Column(name = "route_number", nullable = false, unique = true, length = 50)
    private String routeNumber;

    @NotBlank(message = "Route display name is required")
    @Column(name = "route_name", nullable = false, length = 150)
    private String routeName;

    @NotBlank(message = "Source location is required")
    @Column(name = "source_location", nullable = false, length = 120)
    private String sourceLocation;

    @NotBlank(message = "Destination location is required")
    @Column(name = "destination_location", nullable = false, length = 120)
    private String destinationLocation;

    @NotNull(message = "Distance in km is required")
    @DecimalMin(value = "0.1", message = "Distance must be greater than zero")
    @Column(name = "distance_km", nullable = false, precision = 8, scale = 2)
    private BigDecimal distanceKm;

    @NotNull(message = "Estimated travel duration is required")
    @Min(value = 1, message = "Duration must be at least 1 minute")
    @Column(name = "estimated_duration_minutes", nullable = false)
    private Integer estimatedDurationMinutes;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Route() {
    }

    public Route(Long id, String routeNumber, String routeName, String sourceLocation,
                 String destinationLocation, BigDecimal distanceKm, Integer estimatedDurationMinutes, String description) {
        this.id = id;
        this.routeNumber = routeNumber;
        this.routeName = routeName;
        this.sourceLocation = sourceLocation;
        this.destinationLocation = destinationLocation;
        this.distanceKm = distanceKm;
        this.estimatedDurationMinutes = estimatedDurationMinutes;
        this.description = description;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
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
}
