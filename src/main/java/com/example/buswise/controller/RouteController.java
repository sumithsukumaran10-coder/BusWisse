package com.example.buswise.controller;

import com.example.buswise.dto.ApiResponse;
import com.example.buswise.dto.RouteRequestDTO;
import com.example.buswise.dto.RouteResponseDTO;
import com.example.buswise.service.RouteService;
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
@RequestMapping("/api/routes")
public class RouteController {

    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<RouteResponseDTO>>> getRoutes(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<RouteResponseDTO> routes = routeService.getAllRoutes(search, pageable);
        return ResponseEntity.ok(ApiResponse.ok("Routes retrieved successfully", routes));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<RouteResponseDTO>>> getAllRoutesList(
            @RequestParam(required = false) String search) {
        List<RouteResponseDTO> routes = routeService.getAllRoutesList(search);
        return ResponseEntity.ok(ApiResponse.ok("All routes retrieved", routes));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RouteResponseDTO>> getRouteById(@PathVariable Long id) {
        RouteResponseDTO route = routeService.getRouteById(id);
        return ResponseEntity.ok(ApiResponse.ok("Route details retrieved", route));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RouteResponseDTO>> createRoute(@Valid @RequestBody RouteRequestDTO dto) {
        RouteResponseDTO created = routeService.createRoute(dto);
        return new ResponseEntity<>(ApiResponse.ok("Route created successfully", created), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RouteResponseDTO>> updateRoute(
            @PathVariable Long id,
            @Valid @RequestBody RouteRequestDTO dto) {
        RouteResponseDTO updated = routeService.updateRoute(id, dto);
        return ResponseEntity.ok(ApiResponse.ok("Route updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRoute(@PathVariable Long id) {
        routeService.deleteRoute(id);
        return ResponseEntity.ok(ApiResponse.ok("Route deleted successfully", null));
    }
}
