package com.example.buswise;

import com.example.buswise.dto.TripRequestDTO;
import com.example.buswise.dto.TripResponseDTO;
import com.example.buswise.dto.TripStatusUpdateDTO;
import com.example.buswise.entity.*;
import com.example.buswise.exception.BusinessValidationException;
import com.example.buswise.repository.BusRepository;
import com.example.buswise.repository.RouteRepository;
import com.example.buswise.repository.TripRepository;
import com.example.buswise.service.TripService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TripServiceTest {

    @Mock
    private TripRepository tripRepository;

    @Mock
    private BusRepository busRepository;

    @Mock
    private RouteRepository routeRepository;

    @InjectMocks
    private TripService tripService;

    private Bus availableBus;
    private Bus maintenanceBus;
    private Route sampleRoute;
    private TripRequestDTO tripRequest;

    @BeforeEach
    void setUp() {
        availableBus = new Bus(1L, "KA-01-F-9001", "Volvo 9600", 45,
                FuelType.DIESEL, BigDecimal.valueOf(10000), BusStatus.AVAILABLE, null);

        maintenanceBus = new Bus(2L, "DL-01-AX-7890", "BharatBenz 1624", 40,
                FuelType.DIESEL, BigDecimal.valueOf(95000), BusStatus.MAINTENANCE, "Brake overhaul");

        sampleRoute = new Route(10L, "RT-KA-101", "Bangalore - Mysuru", "Bangalore",
                "Mysuru", BigDecimal.valueOf(145), 150, null);

        tripRequest = new TripRequestDTO();
        tripRequest.setBusId(1L);
        tripRequest.setRouteId(10L);
        tripRequest.setDepartureTime(LocalDateTime.now().plusHours(1));
        tripRequest.setExpectedArrivalTime(LocalDateTime.now().plusHours(4));
        tripRequest.setPassengerCount(40);
        tripRequest.setTicketRevenue(BigDecimal.valueOf(16000));
        tripRequest.setAdditionalExpenses(BigDecimal.valueOf(400));
    }

    @Test
    @DisplayName("Should successfully schedule a valid non-conflicting trip")
    void testScheduleTrip_Success() {
        when(busRepository.findById(1L)).thenReturn(Optional.of(availableBus));
        when(routeRepository.findById(10L)).thenReturn(Optional.of(sampleRoute));
        when(tripRepository.findConflictingTrips(eq(1L), any(), any(), isNull())).thenReturn(Collections.emptyList());

        Trip savedTrip = new Trip(100L, availableBus, sampleRoute, tripRequest.getDepartureTime(),
                tripRequest.getExpectedArrivalTime(), null, TripStatus.SCHEDULED, 40,
                BigDecimal.valueOf(16000), BigDecimal.valueOf(400), false, null, null);

        when(tripRepository.save(any(Trip.class))).thenReturn(savedTrip);

        TripResponseDTO result = tripService.scheduleTrip(tripRequest);

        assertNotNull(result);
        assertEquals(TripStatus.SCHEDULED, result.getStatus());
        assertEquals(40, result.getPassengerCount());
        verify(tripRepository, times(1)).save(any(Trip.class));
    }

    @Test
    @DisplayName("Should reject scheduling when bus is under MAINTENANCE")
    void testScheduleTrip_BusInMaintenance_ThrowsException() {
        tripRequest.setBusId(2L);
        when(busRepository.findById(2L)).thenReturn(Optional.of(maintenanceBus));
        when(routeRepository.findById(10L)).thenReturn(Optional.of(sampleRoute));

        assertThrows(BusinessValidationException.class, () -> tripService.scheduleTrip(tripRequest));
        verify(tripRepository, never()).save(any(Trip.class));
    }

    @Test
    @DisplayName("Should reject scheduling when bus has overlapping active trip")
    void testScheduleTrip_OverlappingTrip_ThrowsException() {
        when(busRepository.findById(1L)).thenReturn(Optional.of(availableBus));
        when(routeRepository.findById(10L)).thenReturn(Optional.of(sampleRoute));

        Trip existingTrip = new Trip(99L, availableBus, sampleRoute,
                tripRequest.getDepartureTime().minusHours(1),
                tripRequest.getExpectedArrivalTime().plusHours(1),
                null, TripStatus.SCHEDULED, 35, BigDecimal.valueOf(12000), BigDecimal.ZERO, false, null, null);

        when(tripRepository.findConflictingTrips(eq(1L), any(), any(), isNull()))
                .thenReturn(List.of(existingTrip));

        assertThrows(BusinessValidationException.class, () -> tripService.scheduleTrip(tripRequest));
    }

    @Test
    @DisplayName("Should reject trip when passenger count exceeds bus capacity without overload flag")
    void testScheduleTrip_PassengerCountExceedsCapacity_ThrowsException() {
        tripRequest.setPassengerCount(50); // Bus capacity is 45!
        tripRequest.setOverloadException(false);

        when(busRepository.findById(1L)).thenReturn(Optional.of(availableBus));
        when(routeRepository.findById(10L)).thenReturn(Optional.of(sampleRoute));

        assertThrows(BusinessValidationException.class, () -> tripService.scheduleTrip(tripRequest));
    }

    @Test
    @DisplayName("Should update bus status to ON_TRIP when trip starts")
    void testUpdateTripStatus_StartTrip_SetsBusOnTrip() {
        Trip trip = new Trip(101L, availableBus, sampleRoute, LocalDateTime.now(),
                LocalDateTime.now().plusHours(3), null, TripStatus.SCHEDULED, 30,
                BigDecimal.valueOf(10000), BigDecimal.ZERO, false, null, null);

        when(tripRepository.findById(101L)).thenReturn(Optional.of(trip));
        when(tripRepository.save(any(Trip.class))).thenReturn(trip);

        TripStatusUpdateDTO updateDTO = new TripStatusUpdateDTO();
        updateDTO.setStatus(TripStatus.IN_PROGRESS);

        tripService.updateTripStatus(101L, updateDTO);

        assertEquals(BusStatus.ON_TRIP, availableBus.getStatus());
        verify(busRepository, times(1)).save(availableBus);
    }
}
