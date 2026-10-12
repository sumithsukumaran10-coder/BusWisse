package com.example.buswise.controller;

import com.example.buswise.dto.ApiResponse;
import com.example.buswise.dto.TripRequestDTO;
import com.example.buswise.dto.TripResponseDTO;
import com.example.buswise.dto.TripStatusUpdateDTO;
import com.example.buswise.entity.TripStatus;
import com.example.buswise.service.TripService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/trips")
public class TripController {

    private final TripService tripService;

    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<TripResponseDTO>>> getTrips(
            @RequestParam(required = false) TripStatus status,
            @RequestParam(required = false) Long busId,
            @RequestParam(required = false) Long routeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "departureTime") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<TripResponseDTO> trips = tripService.filterTrips(status, busId, routeId, startDate, endDate, pageable);
        return ResponseEntity.ok(ApiResponse.ok("Trips retrieved successfully", trips));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<TripResponseDTO>>> getAllTripsList(
            @RequestParam(required = false) TripStatus status,
            @RequestParam(required = false) Long busId,
            @RequestParam(required = false) Long routeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<TripResponseDTO> trips = tripService.filterTripsList(status, busId, routeId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.ok("All filtered trips retrieved", trips));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TripResponseDTO>> getTripById(@PathVariable Long id) {
        TripResponseDTO trip = tripService.getTripById(id);
        return ResponseEntity.ok(ApiResponse.ok("Trip details retrieved", trip));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TripResponseDTO>> scheduleTrip(@Valid @RequestBody TripRequestDTO dto) {
        TripResponseDTO scheduled = tripService.scheduleTrip(dto);
        return new ResponseEntity<>(ApiResponse.ok("Trip scheduled successfully", scheduled), HttpStatus.CREATED);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<TripResponseDTO>> updateTripStatus(
            @PathVariable Long id,
            @Valid @RequestBody TripStatusUpdateDTO dto) {
        TripResponseDTO updated = tripService.updateTripStatus(id, dto);
        return ResponseEntity.ok(ApiResponse.ok("Trip status updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTrip(@PathVariable Long id) {
        tripService.deleteTrip(id);
        return ResponseEntity.ok(ApiResponse.ok("Trip deleted successfully", null));
    }
}
