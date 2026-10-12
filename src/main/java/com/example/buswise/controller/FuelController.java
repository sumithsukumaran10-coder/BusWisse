package com.example.buswise.controller;

import com.example.buswise.dto.ApiResponse;
import com.example.buswise.dto.FuelRecordRequestDTO;
import com.example.buswise.dto.FuelRecordResponseDTO;
import com.example.buswise.service.FuelService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/fuel")
public class FuelController {

    private final FuelService fuelService;

    public FuelController(FuelService fuelService) {
        this.fuelService = fuelService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<FuelRecordResponseDTO>>> getFuelRecords(
            @RequestParam(required = false) Long busId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "recordDate") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<FuelRecordResponseDTO> records = fuelService.filterFuelRecords(busId, startDate, endDate, pageable);
        return ResponseEntity.ok(ApiResponse.ok("Fuel records retrieved successfully", records));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<FuelRecordResponseDTO>>> getAllFuelRecordsList(
            @RequestParam(required = false) Long busId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<FuelRecordResponseDTO> records = fuelService.filterFuelRecordsList(busId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.ok("All fuel records retrieved", records));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FuelRecordResponseDTO>> getFuelRecordById(@PathVariable Long id) {
        FuelRecordResponseDTO record = fuelService.getFuelRecordById(id);
        return ResponseEntity.ok(ApiResponse.ok("Fuel record retrieved", record));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<FuelRecordResponseDTO>> recordFuel(@Valid @RequestBody FuelRecordRequestDTO dto) {
        FuelRecordResponseDTO created = fuelService.recordFuel(dto);
        return new ResponseEntity<>(ApiResponse.ok("Fuel refueling record saved successfully", created), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteFuelRecord(@PathVariable Long id) {
        fuelService.deleteFuelRecord(id);
        return ResponseEntity.ok(ApiResponse.ok("Fuel record deleted successfully", null));
    }
}
