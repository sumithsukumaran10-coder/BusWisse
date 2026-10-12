package com.example.buswise;

import com.example.buswise.dto.BusRequestDTO;
import com.example.buswise.dto.BusResponseDTO;
import com.example.buswise.entity.Bus;
import com.example.buswise.entity.BusStatus;
import com.example.buswise.entity.FuelType;
import com.example.buswise.exception.BusinessValidationException;
import com.example.buswise.exception.DuplicateResourceException;
import com.example.buswise.repository.BusRepository;
import com.example.buswise.repository.ExpenseRepository;
import com.example.buswise.repository.FuelRecordRepository;
import com.example.buswise.repository.TripRepository;
import com.example.buswise.service.BusService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BusServiceTest {

    @Mock
    private BusRepository busRepository;

    @Mock
    private TripRepository tripRepository;

    @Mock
    private FuelRecordRepository fuelRecordRepository;

    @Mock
    private ExpenseRepository expenseRepository;

    @InjectMocks
    private BusService busService;

    private Bus sampleBus;
    private BusRequestDTO sampleRequest;

    @BeforeEach
    void setUp() {
        sampleBus = new Bus(1L, "KA-01-AB-1234", "Tata Starbus Ultra", 40,
                FuelType.DIESEL, BigDecimal.valueOf(15000), BusStatus.AVAILABLE, "Well maintained");

        sampleRequest = new BusRequestDTO();
        sampleRequest.setRegistrationNumber("KA-01-AB-1234");
        sampleRequest.setBusModel("Tata Starbus Ultra");
        sampleRequest.setSeatingCapacity(40);
        sampleRequest.setFuelType(FuelType.DIESEL);
        sampleRequest.setOdometerReading(BigDecimal.valueOf(15000));
        sampleRequest.setStatus(BusStatus.AVAILABLE);
    }

    @Test
    @DisplayName("Should successfully register a new unique bus")
    void testCreateBus_Success() {
        when(busRepository.existsByRegistrationNumberIgnoreCase("KA-01-AB-1234")).thenReturn(false);
        when(busRepository.save(any(Bus.class))).thenReturn(sampleBus);

        BusResponseDTO response = busService.createBus(sampleRequest);

        assertNotNull(response);
        assertEquals("KA-01-AB-1234", response.getRegistrationNumber());
        assertEquals(40, response.getSeatingCapacity());
        verify(busRepository, times(1)).save(any(Bus.class));
    }

    @Test
    @DisplayName("Should reject bus creation if registration number already exists")
    void testCreateBus_DuplicateRegistration_ThrowsException() {
        when(busRepository.existsByRegistrationNumberIgnoreCase("KA-01-AB-1234")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> busService.createBus(sampleRequest));
        verify(busRepository, never()).save(any(Bus.class));
    }

    @Test
    @DisplayName("Should prevent changing status to MAINTENANCE when bus is on active trip")
    void testUpdateBus_ActiveTrip_ThrowsException() {
        when(busRepository.findById(1L)).thenReturn(Optional.of(sampleBus));
        when(busRepository.existsByRegistrationNumberIgnoreCaseAndIdNot("KA-01-AB-1234", 1L)).thenReturn(false);
        when(tripRepository.existsByBusIdAndStatusIn(eq(1L), anyList())).thenReturn(true);

        sampleRequest.setStatus(BusStatus.MAINTENANCE);

        assertThrows(BusinessValidationException.class, () -> busService.updateBus(1L, sampleRequest));
    }

    @Test
    @DisplayName("Should prevent deleting bus with dependent trip records")
    void testDeleteBus_HasTripDependencies_ThrowsException() {
        when(busRepository.findById(1L)).thenReturn(Optional.of(sampleBus));
        when(tripRepository.countByBusId(1L)).thenReturn(5L);

        assertThrows(BusinessValidationException.class, () -> busService.deleteBus(1L));
        verify(busRepository, never()).delete(any(Bus.class));
    }
}
