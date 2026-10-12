package com.example.buswise.dto;

import com.example.buswise.entity.AlertSeverity;
import com.example.buswise.entity.AlertType;
import com.example.buswise.entity.OperationalAlert;
import java.time.LocalDateTime;

public class OperationalAlertDTO {
    private Long id;
    private String title;
    private String message;
    private AlertSeverity severity;
    private AlertType alertType;
    private String relatedEntityType;
    private Long relatedEntityId;
    private Boolean acknowledged;
    private LocalDateTime createdAt;

    public OperationalAlertDTO() {
    }

    public static OperationalAlertDTO fromEntity(OperationalAlert alert) {
        if (alert == null) return null;
        OperationalAlertDTO dto = new OperationalAlertDTO();
        dto.setId(alert.getId());
        dto.setTitle(alert.getTitle());
        dto.setMessage(alert.getMessage());
        dto.setSeverity(alert.getSeverity());
        dto.setAlertType(alert.getAlertType());
        dto.setRelatedEntityType(alert.getRelatedEntityType());
        dto.setRelatedEntityId(alert.getRelatedEntityId());
        dto.setAcknowledged(alert.getAcknowledged());
        dto.setCreatedAt(alert.getCreatedAt());
        return dto;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public AlertSeverity getSeverity() {
        return severity;
    }

    public void setSeverity(AlertSeverity severity) {
        this.severity = severity;
    }

    public AlertType getAlertType() {
        return alertType;
    }

    public void setAlertType(AlertType alertType) {
        this.alertType = alertType;
    }

    public String getRelatedEntityType() {
        return relatedEntityType;
    }

    public void setRelatedEntityType(String relatedEntityType) {
        this.relatedEntityType = relatedEntityType;
    }

    public Long getRelatedEntityId() {
        return relatedEntityId;
    }

    public void setRelatedEntityId(Long relatedEntityId) {
        this.relatedEntityId = relatedEntityId;
    }

    public Boolean getAcknowledged() {
        return acknowledged;
    }

    public void setAcknowledged(Boolean acknowledged) {
        this.acknowledged = acknowledged;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
