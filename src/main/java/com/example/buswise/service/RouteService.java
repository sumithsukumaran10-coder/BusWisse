package com.example.buswise.service;

import com.example.buswise.dto.RouteRequestDTO;
import com.example.buswise.dto.RouteResponseDTO;
import com.example.buswise.entity.Route;
import com.example.buswise.exception.BusinessValidationException;
import com.example.buswise.exception.DuplicateResourceException;
import com.example.buswise.exception.ResourceNotFoundException;
import com.example.buswise.repository.RouteRepository;
import com.example.buswise.repository.TripRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RouteService {

    private final RouteRepository routeRepository;
    private final TripRepository tripRepository;

    public RouteService(RouteRepository routeRepository, TripRepository tripRepository) {
        this.routeRepository = routeRepository;
        this.tripRepository = tripRepository;
    }

    public RouteResponseDTO createRoute(RouteRequestDTO dto) {
        String cleanRouteNo = dto.getRouteNumber().trim().toUpperCase();
        if (routeRepository.existsByRouteNumberIgnoreCase(cleanRouteNo)) {
            throw new DuplicateResourceException("Route code '" + cleanRouteNo + "' already exists.");
        }

        Route route = new Route();
        route.setRouteNumber(cleanRouteNo);
        route.setRouteName(dto.getRouteName().trim());
        route.setSourceLocation(dto.getSourceLocation().trim());
        route.setDestinationLocation(dto.getDestinationLocation().trim());
        route.setDistanceKm(dto.getDistanceKm());
        route.setEstimatedDurationMinutes(dto.getEstimatedDurationMinutes());
        route.setDescription(dto.getDescription());

        Route saved = routeRepository.save(route);
        RouteResponseDTO responseDTO = RouteResponseDTO.fromEntity(saved);
        responseDTO.setTotalTripsCount(0L);
        return responseDTO;
    }

    @Transactional(readOnly = true)
    public RouteResponseDTO getRouteById(Long id) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found with ID: " + id));
        RouteResponseDTO responseDTO = RouteResponseDTO.fromEntity(route);
        responseDTO.setTotalTripsCount(tripRepository.countByRouteId(id));
        return responseDTO;
    }

    public RouteResponseDTO updateRoute(Long id, RouteRequestDTO dto) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found with ID: " + id));

        String cleanRouteNo = dto.getRouteNumber().trim().toUpperCase();
        if (routeRepository.existsByRouteNumberIgnoreCaseAndIdNot(cleanRouteNo, id)) {
            throw new DuplicateResourceException("Another route with code '" + cleanRouteNo + "' already exists.");
        }

        route.setRouteNumber(cleanRouteNo);
        route.setRouteName(dto.getRouteName().trim());
        route.setSourceLocation(dto.getSourceLocation().trim());
        route.setDestinationLocation(dto.getDestinationLocation().trim());
        route.setDistanceKm(dto.getDistanceKm());
        route.setEstimatedDurationMinutes(dto.getEstimatedDurationMinutes());
        route.setDescription(dto.getDescription());

        Route updated = routeRepository.save(route);
        RouteResponseDTO responseDTO = RouteResponseDTO.fromEntity(updated);
        responseDTO.setTotalTripsCount(tripRepository.countByRouteId(id));
        return responseDTO;
    }

    public void deleteRoute(Long id) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found with ID: " + id));

        long associatedTrips = tripRepository.countByRouteId(id);
        if (associatedTrips > 0) {
            throw new BusinessValidationException(
                    "Cannot delete Route " + route.getRouteNumber() + " (" + route.getRouteName() +
                    ") because " + associatedTrips + " trip(s) depend on it. Delete or reassign those trips first."
            );
        }

        routeRepository.delete(route);
    }

    @Transactional(readOnly = true)
    public Page<RouteResponseDTO> getAllRoutes(String search, Pageable pageable) {
        String query = (search != null && !search.trim().isEmpty()) ? search.trim() : null;
        return routeRepository.searchRoutes(query, pageable).map(r -> {
            RouteResponseDTO dto = RouteResponseDTO.fromEntity(r);
            dto.setTotalTripsCount(tripRepository.countByRouteId(r.getId()));
            return dto;
        });
    }

    @Transactional(readOnly = true)
    public List<RouteResponseDTO> getAllRoutesList(String search) {
        String query = (search != null && !search.trim().isEmpty()) ? search.trim() : null;
        return routeRepository.searchRoutesList(query).stream().map(r -> {
            RouteResponseDTO dto = RouteResponseDTO.fromEntity(r);
            dto.setTotalTripsCount(tripRepository.countByRouteId(r.getId()));
            return dto;
        }).collect(Collectors.toList());
    }
}
