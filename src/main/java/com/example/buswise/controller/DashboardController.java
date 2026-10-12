package com.example.buswise.controller;

import com.example.buswise.dto.ApiResponse;
import com.example.buswise.dto.DashboardSummaryDTO;
import com.example.buswise.service.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final AnalyticsService analyticsService;

    public DashboardController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<DashboardSummaryDTO>> getDashboardSummary() {
        DashboardSummaryDTO summary = analyticsService.getDashboardSummary();
        return ResponseEntity.ok(ApiResponse.ok("Dashboard metrics calculated successfully", summary));
    }
}
