package com.example.buswise.service;

import com.example.buswise.dto.TripRequestDTO;
import com.example.buswise.dto.TripResponseDTO;
import com.example.buswise.dto.TripStatusUpdateDTO;
import com.example.buswise.entity.*;
import com.example.buswise.exception.BusinessValidationException;
import com.example.buswise.exception.ResourceNotFoundException;
import com.example.buswise.repository.BusRepository;
import com.example.buswise.repository.RouteRepository;
import com.example.buswise.repository.TripRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TripService {

    private final TripRepository tripRepository;
    private final BusRepository busRepository;
    private final RouteRepository routeRepository;

    public TripService(TripRepository tripRepository,
                       BusRepository busRepository,
                       RouteRepository routeRepository) {
        this.tripRepository = tripRepository;
        this.busRepository = busRepository;
        this.routeRepository = routeRepository;
    }

    public TripResponseDTO scheduleTrip(TripRequestDTO dto) {
        // 1. Validate Bus and Route Existence
        Bus bus = busRepository.findById(dto.getBusId())
                .orElseThrow(() -> new ResourceNotFoundException("Bus not found with ID: " + dto.getBusId()));

        Route route = routeRepository.findById(dto.getRouteId())
                .orElseThrow(() -> new ResourceNotFoundException("Route not found with ID: " + dto.getRouteId()));

        // 2. Business Rule: Bus under MAINTENANCE cannot be assigned to new trips
        if (bus.getStatus() == BusStatus.MAINTENANCE) {
            throw new BusinessValidationException(
                    "Bus #" + bus.getRegistrationNumber() + " is currently under MAINTENANCE and cannot be scheduled for a trip."
            );
        }

        // 3. Time Validation
        if (!dto.getExpectedArrivalTime().isAfter(dto.getDepartureTime())) {
            throw new BusinessValidationException("Expected arrival time must be after the departure time.");
        }

        // 4. Business Rule: Prevent Overlapping Active Trips for the same bus
        List<Trip> conflictingTrips = tripRepository.findConflictingTrips(
                bus.getId(),
                dto.getDepartureTime(),
                dto.getExpectedArrivalTime(),
                null
        );

        if (!conflictingTrips.isEmpty()) {
            Trip conflict = conflictingTrips.get(0);
            throw new BusinessValidationException(
                    "Bus #" + bus.getRegistrationNumber() + " is already assigned to active Trip #" + conflict.getId() +
                    " (" + conflict.getRoute().getRouteName() + ") from " + conflict.getDepartureTime() +
                    " to " + conflict.getExpectedArrivalTime() + ". Overlapping trips are prohibited."
            );
        }

        // 5. Business Rule: Passenger count cannot exceed seating capacity without explicit overload exception
        int passengers = dto.getPassengerCount() != null ? dto.getPassengerCount() : 0;
        validatePassengerCapacity(bus, passengers, dto.getOverloadException(), dto.getOverloadReason());

        // 6. Instantiate & Populate Trip
        Trip trip = new Trip();
        trip.setBus(bus);
        trip.setRoute(route);
        trip.setDepartureTime(dto.getDepartureTime());
        trip.setExpectedArrivalTime(dto.getExpectedArrivalTime());
        trip.setActualArrivalTime(dto.getActualArrivalTime());
        trip.setStatus(dto.getStatus() != null ? dto.getStatus() : TripStatus.SCHEDULED);
        trip.setPassengerCount(passengers);
        trip.setTicketRevenue(dto.getTicketRevenue() != null ? dto.getTicketRevenue() : BigDecimal.ZERO);
        trip.setAdditionalExpenses(dto.getAdditionalExpenses() != null ? dto.getAdditionalExpenses() : BigDecimal.ZERO);
        trip.setOverloadException(Boolean.TRUE.equals(dto.getOverloadException()));
        trip.setOverloadReason(dto.getOverloadReason());
        trip.setNotes(dto.getNotes());

        // Update bus status if created directly as IN_PROGRESS
        if (trip.getStatus() == TripStatus.IN_PROGRESS) {
            bus.setStatus(BusStatus.ON_TRIP);
            busRepository.save(bus);
        }

        Trip saved = tripRepository.save(trip);
        return TripResponseDTO.fromEntity(saved);
    }

    public TripResponseDTO updateTripStatus(Long id, TripStatusUpdateDTO dto) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found with ID: " + id));

        TripStatus currentStatus = trip.getStatus();
        TripStatus newStatus = dto.getStatus();
        Bus bus = trip.getBus();

        // Business Rule: Completed or Cancelled trips cannot be reopened
        if ((currentStatus == TripStatus.COMPLETED || currentStatus == TripStatus.CANCELLED) && currentStatus != newStatus) {
            throw new BusinessValidationException(
                    "Trip #" + trip.getId() + " is already " + currentStatus + " and cannot be transitioned to " + newStatus + "."
            );
        }

        // Apply Passenger and Financial updates if provided
        if (dto.getPassengerCount() != null) {
            boolean overload = dto.getOverloadException() != null ? dto.getOverloadException() : Boolean.TRUE.equals(trip.getOverloadException());
            String reason = dto.getOverloadReason() != null ? dto.getOverloadReason() : trip.getOverloadReason();
            validatePassengerCapacity(bus, dto.getPassengerCount(), overload, reason);
            trip.setPassengerCount(dto.getPassengerCount());
            trip.setOverloadException(overload);
            trip.setOverloadReason(reason);
        }

        if (dto.getTicketRevenue() != null) {
            trip.setTicketRevenue(dto.getTicketRevenue());
        }

        if (dto.getAdditionalExpenses() != null) {
            trip.setAdditionalExpenses(dto.getAdditionalExpenses());
        }

        if (dto.getNotes() != null) {
            trip.setNotes(dto.getNotes());
        }

        // Execute valid state transition
        if (currentStatus != newStatus) {
            if (newStatus == TripStatus.IN_PROGRESS) {
                // Bus must transition to ON_TRIP
                bus.setStatus(BusStatus.ON_TRIP);
                busRepository.save(bus);
            } else if (newStatus == TripStatus.COMPLETED) {
                // Trip completes: update arrival time, restore bus status, update odometer
                trip.setActualArrivalTime(dto.getActualArrivalTime() != null ? dto.getActualArrivalTime() : LocalDateTime.now());
                if (bus.getStatus() == BusStatus.ON_TRIP) {
                    bus.setStatus(BusStatus.AVAILABLE);
                }
                if (trip.getRoute() != null && trip.getRoute().getDistanceKm() != null) {
                    bus.setOdometerReading(bus.getOdometerReading().add(trip.getRoute().getDistanceKm()));
                }
                busRepository.save(bus);
            } else if (newStatus == TripStatus.CANCELLED) {
                // Cancel trip: release bus back to AVAILABLE
                if (bus.getStatus() == BusStatus.ON_TRIP) {
                    bus.setStatus(BusStatus.AVAILABLE);
                    busRepository.save(bus);
                }
            }
            trip.setStatus(newStatus);
        }

        if (dto.getActualArrivalTime() != null) {
            trip.setActualArrivalTime(dto.getActualArrivalTime());
        }

        Trip updated = tripRepository.save(trip);
        return TripResponseDTO.fromEntity(updated);
    }

    private void validatePassengerCapacity(Bus bus, int passengers, Boolean overloadException, String overloadReason) {
        if (passengers < 0) {
            throw new BusinessValidationException("Passenger count cannot be negative.");
        }
        if (passengers > bus.getSeatingCapacity()) {
            if (!Boolean.TRUE.equals(overloadException)) {
                throw new BusinessValidationException(
                        "Passenger count (" + passengers + ") exceeds bus seating capacity (" +
                        bus.getSeatingCapacity() + "). To proceed, explicitly mark 'Overload Exception' with a documented operational reason."
                );
            }
            if (overloadReason == null || overloadReason.trim().isEmpty()) {
                throw new BusinessValidationException("An explanatory reason is mandatory when recording passenger overload.");
            }
        }
    }

    @Transactional(readOnly = true)
    public TripResponseDTO getTripById(Long id) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found with ID: " + id));
        return TripResponseDTO.fromEntity(trip);
    }

    public void deleteTrip(Long id) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found with ID: " + id));

        // If trip was active, restore bus to available
        if (trip.getStatus() == TripStatus.IN_PROGRESS && trip.getBus().getStatus() == BusStatus.ON_TRIP) {
            trip.getBus().setStatus(BusStatus.AVAILABLE);
            busRepository.save(trip.getBus());
        }

        tripRepository.delete(trip);
    }

    @Transactional(readOnly = true)
    public Page<TripResponseDTO> filterTrips(TripStatus status, Long busId, Long routeId,
                                            LocalDateTime startDate, LocalDateTime endDate,
                                            Pageable pageable) {
        return tripRepository.filterTrips(status, busId, routeId, startDate, endDate, pageable)
                .map(TripResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public List<TripResponseDTO> filterTripsList(TripStatus status, Long busId, Long routeId,
                                                LocalDateTime startDate, LocalDateTime endDate) {
        return tripRepository.filterTripsList(status, busId, routeId, startDate, endDate).stream()
                .map(TripResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
