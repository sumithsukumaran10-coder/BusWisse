package com.example.buswise.controller;

import com.example.buswise.dto.ApiResponse;
import com.example.buswise.dto.BusRequestDTO;
import com.example.buswise.dto.BusResponseDTO;
import com.example.buswise.entity.BusStatus;
import com.example.buswise.service.BusService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/buses")
public class BusController {

    private final BusService busService;

    public BusController(BusService busService) {
        this.busService = busService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<BusResponseDTO>>> getBuses(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) BusStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<BusResponseDTO> buses = busService.getAllBuses(search, status, pageable);
        return ResponseEntity.ok(ApiResponse.ok("Buses retrieved successfully", buses));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<BusResponseDTO>>> getAllBusesList(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) BusStatus status) {
        List<BusResponseDTO> buses = busService.getAllBusesList(search, status);
        return ResponseEntity.ok(ApiResponse.ok("All buses retrieved", buses));
    }

    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<BusResponseDTO>>> getAvailableBuses() {
        List<BusResponseDTO> available = busService.getAvailableBuses();
        return ResponseEntity.ok(ApiResponse.ok("Available buses retrieved", available));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BusResponseDTO>> getBusById(@PathVariable Long id) {
        BusResponseDTO bus = busService.getBusById(id);
        return ResponseEntity.ok(ApiResponse.ok("Bus details retrieved", bus));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BusResponseDTO>> createBus(@Valid @RequestBody BusRequestDTO dto) {
        BusResponseDTO created = busService.createBus(dto);
        return new ResponseEntity<>(ApiResponse.ok("Bus registered successfully", created), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BusResponseDTO>> updateBus(
            @PathVariable Long id,
            @Valid @RequestBody BusRequestDTO dto) {
        BusResponseDTO updated = busService.updateBus(id, dto);
        return ResponseEntity.ok(ApiResponse.ok("Bus updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBus(@PathVariable Long id) {
        busService.deleteBus(id);
        return ResponseEntity.ok(ApiResponse.ok("Bus deleted successfully", null));
    }
}
