package com.example.buswise.controller;

import com.example.buswise.dto.*;
import com.example.buswise.service.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/passengers")
    public ResponseEntity<ApiResponse<PassengerAnalyticsDTO>> getPassengerAnalytics() {
        PassengerAnalyticsDTO analytics = analyticsService.getPassengerAnalytics();
        return ResponseEntity.ok(ApiResponse.ok("Passenger demand analytics calculated", analytics));
    }

    @GetMapping("/fuel")
    public ResponseEntity<ApiResponse<FuelAnalyticsDTO>> getFuelAnalytics() {
        FuelAnalyticsDTO analytics = analyticsService.getFuelAnalytics();
        return ResponseEntity.ok(ApiResponse.ok("Fuel efficiency analytics calculated", analytics));
    }

    @GetMapping("/profitability")
    public ResponseEntity<ApiResponse<ProfitabilityAnalyticsDTO>> getProfitabilityAnalytics() {
        ProfitabilityAnalyticsDTO analytics = analyticsService.getProfitabilityAnalytics();
        return ResponseEntity.ok(ApiResponse.ok("Profitability analytics calculated", analytics));
    }

    @GetMapping("/sustainability")
    public ResponseEntity<ApiResponse<SustainabilityAnalyticsDTO>> getSustainabilityAnalytics() {
        SustainabilityAnalyticsDTO analytics = analyticsService.getSustainabilityAnalytics();
        return ResponseEntity.ok(ApiResponse.ok("Sustainability and emissions estimates calculated", analytics));
    }
}
