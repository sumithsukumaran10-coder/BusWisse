package com.example.buswise.controller;

import com.example.buswise.dto.ApiResponse;
import com.example.buswise.dto.OperationalAlertDTO;
import com.example.buswise.service.AlertService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OperationalAlertDTO>>> getActiveAlerts() {
        List<OperationalAlertDTO> alerts = alertService.getActiveAlerts();
        return ResponseEntity.ok(ApiResponse.ok("Active operational alerts retrieved", alerts));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<OperationalAlertDTO>>> getAllAlerts() {
        List<OperationalAlertDTO> alerts = alertService.getAllAlerts();
        return ResponseEntity.ok(ApiResponse.ok("All operational alerts retrieved", alerts));
    }

    @PutMapping("/{id}/acknowledge")
    public ResponseEntity<ApiResponse<OperationalAlertDTO>> acknowledgeAlert(@PathVariable Long id) {
        OperationalAlertDTO ack = alertService.acknowledgeAlert(id);
        return ResponseEntity.ok(ApiResponse.ok("Alert acknowledged successfully", ack));
    }

    @PostMapping("/scan")
    public ResponseEntity<ApiResponse<List<OperationalAlertDTO>>> triggerAlertScan() {
        alertService.scanAndGenerateAlerts();
        List<OperationalAlertDTO> alerts = alertService.getActiveAlerts();
        return ResponseEntity.ok(ApiResponse.ok("Rules scan completed. Active alerts refreshed.", alerts));
    }
}
